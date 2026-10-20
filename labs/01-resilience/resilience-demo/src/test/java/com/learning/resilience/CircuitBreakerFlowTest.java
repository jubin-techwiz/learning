package com.learning.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Each test maps to one idea from the lesson. Settings are shortened so the suite runs in seconds:
 * 10-call window, minimum 5 calls, 50% failure threshold, 1s open wait, 500ms slow-call threshold.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "resilience4j.circuitbreaker.instances.payment.waitDurationInOpenState=1s",
                "resilience4j.circuitbreaker.instances.payment.slowCallDurationThreshold=500ms"
        })
class CircuitBreakerFlowTest {

    @Autowired TestRestTemplate rest;
    @Autowired CircuitBreakerRegistry registry;

    CircuitBreaker breaker;

    @BeforeEach
    void reset() {
        breaker = registry.circuitBreaker("payment");
        breaker.reset();                       // back to CLOSED with an empty window
        setDownstream("OK", null);
        rest.postForObject("/admin/downstream/reset-counter", null, Map.class);
    }

    @Test
    void closedBreakerPassesCallsThrough() {
        PaymentResult r = pay("o-1");

        assertThat(r.status()).isEqualTo("CONFIRMED");
        assertThat(r.degraded()).isFalse();
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void sustainedFailuresOpenTheBreakerAndShieldTheDownstream() {
        setDownstream("FAIL", null);

        // 5 failing calls reach the minimum, failure rate 100% >= 50% -> OPEN.
        for (int i = 0; i < 5; i++) {
            assertThat(pay("o-" + i).degraded()).isTrue();     // fallback served even while still CLOSED
        }
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        int reachedDownstream = downstreamCalls();
        assertThat(reachedDownstream).isEqualTo(5);

        // While OPEN: instant fallback, and NOTHING reaches the downstream.
        for (int i = 0; i < 20; i++) {
            PaymentResult r = pay("blocked-" + i);
            assertThat(r.degraded()).isTrue();
            assertThat(r.detail()).contains("circuit open");
        }
        assertThat(downstreamCalls()).isEqualTo(reachedDownstream);
    }

    @Test
    void businessDeclinesDoNotTripTheBreaker() {
        setDownstream("DECLINE", null);

        for (int i = 0; i < 12; i++) {
            ResponseEntity<String> r = rest.postForEntity("/orders/d-" + i + "/pay", null, String.class);
            assertThat(r.getStatusCode()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);
        }
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
        assertThat(downstreamCalls()).isEqualTo(12);   // every call still went through
    }

    @Test
    void slowCallsAloneCanOpenTheBreakerEvenWithoutErrors() {
        setDownstream("SLOW", 700L);    // succeeds, but slower than the 500ms threshold

        for (int i = 0; i < 5; i++) {
            assertThat(pay("s-" + i).status()).isEqualTo("CONFIRMED");   // no errors at all
        }
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        assertThat(breaker.getMetrics().getSlowCallRate()).isGreaterThanOrEqualTo(80f);
    }

    @Test
    void breakerRecoversThroughHalfOpen() {
        setDownstream("FAIL", null);
        for (int i = 0; i < 5; i++) {
            pay("f-" + i);
        }
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        setDownstream("OK", null);      // dependency heals; the breaker doesn't know yet

        // After waitDurationInOpenState it moves to HALF_OPEN on its own (automatic transition enabled).
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN));

        // 3 permitted trial calls, all healthy -> CLOSED.
        for (int i = 0; i < 3; i++) {
            assertThat(pay("t-" + i).degraded()).isFalse();
        }
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void failedTrialCallsReopenTheBreaker() {
        setDownstream("FAIL", null);
        for (int i = 0; i < 5; i++) {
            pay("f-" + i);
        }
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN));

        // Dependency is still broken, so the trial calls fail and the breaker re-opens.
        for (int i = 0; i < 3; i++) {
            pay("retrial-" + i);
        }
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void unrelatedEndpointsStayHealthyWhileBreakerIsOpen() {
        setDownstream("FAIL", null);
        for (int i = 0; i < 5; i++) {
            pay("x-" + i);
        }
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        ResponseEntity<Map> cart = rest.getForEntity("/orders/1/cart", Map.class);
        assertThat(cart.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ---- helpers ---------------------------------------------------------

    private PaymentResult pay(String orderId) {
        return rest.postForObject("/orders/" + orderId + "/pay", null, PaymentResult.class);
    }

    private void setDownstream(String mode, Long delayMs) {
        String url = "/admin/downstream?mode=" + mode + (delayMs != null ? "&delayMs=" + delayMs : "");
        rest.postForObject(url, null, Map.class);
    }

    private int downstreamCalls() {
        Map<?, ?> state = rest.getForObject("/admin/downstream", Map.class);
        return ((Number) state.get("callsReceived")).intValue();
    }
}
