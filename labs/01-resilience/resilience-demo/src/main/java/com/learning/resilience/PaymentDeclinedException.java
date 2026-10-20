package com.learning.resilience;

/** A business outcome (HTTP 402 from payment-service). Configured as ignored, so it never trips the breaker. */
public class PaymentDeclinedException extends RuntimeException {
    public PaymentDeclinedException(String message) {
        super(message);
    }
}
