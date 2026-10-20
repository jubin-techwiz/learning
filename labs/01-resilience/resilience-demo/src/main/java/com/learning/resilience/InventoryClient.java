package com.learning.resilience;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stands in for a remote inventory-service. The call is simulated with a sleep, so a "slow inventory"
 * holds the calling thread exactly like a slow HTTP call would.
 *
 * Two methods, same work: one protected by a bulkhead, one not. Comparing them is the whole lesson.
 */
@Component
public class InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    private volatile long delayMs = 0;
    private final AtomicInteger inFlight = new AtomicInteger();

    /** Protected: at most N calls at once (see resilience4j.bulkhead.instances.inventory). */
    @Bulkhead(name = "inventory", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "stockFallback")
    public StockResult checkStock(String sku) {
        return remoteCall(sku);
    }

    /** Not protected: every request that arrives is allowed to wait, holding its thread. */
    public StockResult checkStockUnprotected(String sku) {
        return remoteCall(sku);
    }

    StockResult stockFallback(String sku, BulkheadFullException e) {
        log.warn("sku {}: inventory bulkhead full, answering without calling inventory", sku);
        return StockResult.unknown(sku, "inventory busy: stock not checked");
    }

    private StockResult remoteCall(String sku) {
        inFlight.incrementAndGet();
        try {
            Thread.sleep(delayMs);
            return StockResult.available(sku);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } finally {
            inFlight.decrementAndGet();
        }
    }

    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }

    public long getDelayMs() {
        return delayMs;
    }

    public int getInFlight() {
        return inFlight.get();
    }
}
