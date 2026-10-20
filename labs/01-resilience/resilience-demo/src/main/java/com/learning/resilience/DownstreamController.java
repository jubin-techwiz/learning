package com.learning.resilience;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stands in for a remote "payment-service" so the lab needs no other process.
 * The admin endpoint switches its behaviour at runtime, which is how you inject failures.
 */
@RestController
public class DownstreamController {

    public enum Mode { OK, FAIL, SLOW, DECLINE }

    private volatile Mode mode = Mode.OK;
    private volatile long delayMs = 2000;
    private final AtomicInteger calls = new AtomicInteger();

    @PostMapping("/downstream/payment")
    public ResponseEntity<Map<String, String>> charge() throws InterruptedException {
        calls.incrementAndGet();
        return switch (mode) {
            case OK -> ResponseEntity.ok(Map.of("status", "CONFIRMED"));
            case FAIL -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "boom"));
            case SLOW -> {
                Thread.sleep(delayMs);
                yield ResponseEntity.ok(Map.of("status", "CONFIRMED"));
            }
            case DECLINE -> ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of("error", "card declined"));
        };
    }

    /** e.g. POST /admin/downstream?mode=SLOW&delayMs=1500 */
    @PostMapping("/admin/downstream")
    public Map<String, Object> configure(@RequestParam Mode mode,
                                         @RequestParam(required = false) Long delayMs) {
        this.mode = mode;
        if (delayMs != null) {
            this.delayMs = delayMs;
        }
        return state();
    }

    @PostMapping("/admin/downstream/reset-counter")
    public Map<String, Object> resetCounter() {
        calls.set(0);
        return state();
    }

    /** How many requests actually reached the downstream. When the breaker is OPEN this stops growing. */
    @GetMapping("/admin/downstream")
    public Map<String, Object> state() {
        return Map.of("mode", mode, "delayMs", delayMs, "callsReceived", calls.get());
    }
}
