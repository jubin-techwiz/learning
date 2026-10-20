package com.learning.resilience;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);

    private final RestTemplate http;
    private final Environment env;
    private final String configuredBaseUrl;

    public PaymentClient(RestTemplateBuilder builder,
                         Environment env,
                         @Value("${payment.base-url:}") String configuredBaseUrl,
                         @Value("${payment.connect-timeout}") Duration connectTimeout,
                         @Value("${payment.read-timeout}") Duration readTimeout) {
        // Always set timeouts: without one, a hung dependency holds the calling thread indefinitely,
        // and the breaker can't even see the failure.
        this.http = builder.setConnectTimeout(connectTimeout).setReadTimeout(readTimeout).build();
        this.env = env;
        this.configuredBaseUrl = configuredBaseUrl;
    }

    /**
     * The breaker wraps this method via an AOP proxy, so it only works when called from ANOTHER bean
     * (OrderController). A self-invocation inside this class would bypass the proxy and the breaker.
     */
    @CircuitBreaker(name = "payment", fallbackMethod = "pendingPayment")
    public PaymentResult charge(String orderId) {
        try {
            http.postForObject(baseUrl() + "/downstream/payment", null, String.class);
            return PaymentResult.confirmed(orderId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 402) {
                // 402 = business answer. Translate it so the breaker (via ignoreExceptions) does not count it.
                throw new PaymentDeclinedException("card declined for order " + orderId);
            }
            throw e;   // other 4xx: a bug on our side, still surfaces as a failure
        }
        // 5xx and timeouts propagate as exceptions -> counted as failures.
    }

    PaymentResult pendingPayment(String orderId, Throwable t) {
        if (t instanceof PaymentDeclinedException declined) {
            throw declined;   // fallback sees ALL exceptions, even ignored ones; a decline is not a degradation
        }
        if (t instanceof CallNotPermittedException) {
            log.warn("order {}: breaker OPEN, short-circuited without calling payment-service", orderId);
            return PaymentResult.pending(orderId, "circuit open: payment deferred");
        }
        log.warn("order {}: payment call failed ({}), deferring", orderId, t.toString());
        return PaymentResult.pending(orderId, "payment-service error: payment deferred");
    }

    private String baseUrl() {
        if (!configuredBaseUrl.isBlank()) {
            return configuredBaseUrl;
        }
        return "http://localhost:" + env.getProperty("local.server.port", env.getProperty("server.port", "8085"));
    }
}
