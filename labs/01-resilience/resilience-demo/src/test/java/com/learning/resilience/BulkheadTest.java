package com.learning.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

/**
 * The lesson in two tests. Setup: Tomcat has 10 request threads, inventory takes 2 seconds,
 * and 10 customers ask for stock at the same moment. Then someone opens the cart page.
 *
 *   without a bulkhead -> all 10 threads wait on inventory, the cart page has to queue behind them
 *   with a bulkhead    -> only 3 calls wait, the other 7 are refused at once, the cart page is instant
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BulkheadTest {

    private static final int REQUEST_THREADS = 10;   // = server.tomcat.threads.max
    private static final int INVENTORY_DELAY_MS = 2000;

    @Autowired TestRestTemplate rest;
    @Autowired InventoryClient inventory;

    @AfterEach
    void drain() {
        inventory.setDelayMs(0);
        await().atMost(Duration.ofSeconds(10)).until(() -> inventory.getInFlight() == 0);
    }

    @Test
    void withoutBulkheadASlowDependencyBlocksUnrelatedPages() throws Exception {
        inventory.setDelayMs(INVENTORY_DELAY_MS);

        List<Future<StockResult>> calls = fireTogether("/orders/%d/stock-unprotected");
        await().atMost(Duration.ofSeconds(2)).until(() -> inventory.getInFlight() == REQUEST_THREADS);

        long cartMs = timeCartRequest();

        assertThat(cartMs).as("cart page had to wait for a free thread").isGreaterThan(1000);
        for (Future<StockResult> f : calls) {
            assertThat(f.get().degraded()).isFalse();    // everyone got an answer, but only after waiting
        }
    }

    @Test
    void withBulkheadOnlyThreeCallsWaitAndTheRestAreRefusedInstantly() throws Exception {
        inventory.setDelayMs(INVENTORY_DELAY_MS);

        List<Future<StockResult>> calls = fireTogether("/orders/%d/stock");
        await().atMost(Duration.ofSeconds(2)).until(() -> inventory.getInFlight() == 3);

        long cartMs = timeCartRequest();

        assertThat(cartMs).as("cart page is not affected").isLessThan(500);
        assertThat(inventory.getInFlight()).isEqualTo(3);     // never more than 3, however many arrive

        int checked = 0;
        int refused = 0;
        for (Future<StockResult> f : calls) {
            StockResult r = f.get();
            if (r.degraded()) {
                refused++;
                assertThat(r.detail()).contains("inventory busy");
            } else {
                checked++;
            }
        }
        assertThat(checked).isEqualTo(3);
        assertThat(refused).isEqualTo(7);
    }

    // ---- helpers ---------------------------------------------------------

    /** Sends REQUEST_THREADS requests at the same moment and returns their futures. */
    private List<Future<StockResult>> fireTogether(String pathTemplate) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(REQUEST_THREADS);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<StockResult>> futures = new ArrayList<>();
        for (int i = 0; i < REQUEST_THREADS; i++) {
            int n = i;
            futures.add(pool.submit(() -> {
                go.await();
                return rest.getForObject(pathTemplate.formatted(n), StockResult.class);
            }));
        }
        go.countDown();
        pool.shutdown();
        return futures;
    }

    private long timeCartRequest() {
        long start = System.nanoTime();
        @SuppressWarnings("unchecked")
        Map<String, Object> cart = rest.getForObject("/orders/1/cart", Map.class);
        assertThat(cart).containsKey("items");
        return (System.nanoTime() - start) / 1_000_000;
    }
}
