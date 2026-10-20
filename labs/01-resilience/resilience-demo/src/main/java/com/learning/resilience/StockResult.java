package com.learning.resilience;

public record StockResult(String sku, String status, boolean degraded, String detail) {

    public static StockResult available(String sku) {
        return new StockResult(sku, "IN_STOCK", false, "checked");
    }

    /** The fallback answer when the inventory pool is full: the page still loads, just without live stock. */
    public static StockResult unknown(String sku, String reason) {
        return new StockResult(sku, "UNKNOWN", true, reason);
    }
}
