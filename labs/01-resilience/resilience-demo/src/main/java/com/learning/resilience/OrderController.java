package com.learning.resilience;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;

    public OrderController(PaymentClient paymentClient, InventoryClient inventoryClient) {
        this.paymentClient = paymentClient;
        this.inventoryClient = inventoryClient;
    }

    /** Protected by a bulkhead: if the inventory pool is full, this answers at once with "UNKNOWN". */
    @GetMapping("/orders/{id}/stock")
    public StockResult stock(@PathVariable String id) {
        return inventoryClient.checkStock(id);
    }

    /** Same work with NO bulkhead: a slow inventory can tie up every request thread. */
    @GetMapping("/orders/{id}/stock-unprotected")
    public StockResult stockUnprotected(@PathVariable String id) {
        return inventoryClient.checkStockUnprotected(id);
    }

    /** e.g. POST /admin/inventory?delayMs=3000 */
    @PostMapping("/admin/inventory")
    public Map<String, Object> configureInventory(@RequestParam long delayMs) {
        inventoryClient.setDelayMs(delayMs);
        return inventoryState();
    }

    @GetMapping("/admin/inventory")
    public Map<String, Object> inventoryState() {
        return Map.of("delayMs", inventoryClient.getDelayMs(), "inFlight", inventoryClient.getInFlight());
    }

    @PostMapping("/orders/{id}/pay")
    public PaymentResult pay(@PathVariable String id) {
        return paymentClient.charge(id);
    }

    /** Never touches payment-service: should stay healthy whatever the dependency does. */
    @GetMapping("/orders/{id}/cart")
    public Map<String, Object> cart(@PathVariable String id) {
        return Map.of("orderId", id, "items", 3);
    }

    @ExceptionHandler(PaymentDeclinedException.class)
    public ResponseEntity<Map<String, String>> declined(PaymentDeclinedException e) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of("error", e.getMessage()));
    }
}
