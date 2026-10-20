package com.learning.resilience;

public record PaymentResult(String orderId, String status, boolean degraded, String detail) {

    public static PaymentResult confirmed(String orderId) {
        return new PaymentResult(orderId, "CONFIRMED", false, "charged");
    }

    /** The fallback answer: the order is accepted but payment is settled later. A product decision, not a technical one. */
    public static PaymentResult pending(String orderId, String reason) {
        return new PaymentResult(orderId, "PENDING", true, reason);
    }
}
