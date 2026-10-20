# Resilience Demo: Circuit Breaker and Bulkhead

A small Spring Boot app for experimenting with two resilience patterns and watching them work: a **circuit breaker** and a **bulkhead**. It simulates its own slow and failing dependencies, so there is nothing else to install or start.

This README is the documentation for this project. The learning notes about the patterns live elsewhere; this file explains how to run, read and extend the demo itself.

**Location:** `labs/01-resilience/resilience-demo` · **Port:** 8085 · **Stack:** Java 17+, Spring Boot 3.2.4, Resilience4j 2.2.0

## What it demonstrates

| Pattern | The question it answers | Where to see it |
|---|---|---|
| Circuit breaker | Has this dependency failed or slowed down enough that I should stop calling it? | Payment flow: `POST /orders/{id}/pay` |
| Bulkhead | How many calls may use this dependency at the same time? | Stock flow: `GET /orders/{id}/stock` |

It also shows the supporting ideas: fallbacks, ignoring business errors, timeouts, and how one slow dependency can starve unrelated endpoints.

## Prerequisites

- Java 17 or newer (developed on Java 21)
- Maven 3.9+
- Port **8085** free
- Internet access the first time, to download dependencies (`mvn -o` works offline once they are cached)

## Quick start

```bash
mvn spring-boot:run      # start the app on http://localhost:8085
./demo.sh                # terminal 2: circuit breaker walkthrough (about 40 seconds)
./demo-bulkhead.sh       # terminal 2: bulkhead comparison (about 15 seconds)
mvn test                 # 9 automated tests, about 13 seconds
```

Stop the app with `Ctrl+C`. If port 8085 stays busy afterwards, see [Troubleshooting](#troubleshooting).

## Project layout

```
pom.xml                                   Spring Boot 3.2.4, Resilience4j 2.2.0
demo.sh                                   circuit breaker walkthrough against the running app
demo-bulkhead.sh                          bulkhead comparison against the running app
src/main/resources/application.yml        all settings, each with a comment
src/main/java/com/learning/resilience/
  OrderController.java                    public endpoints
  PaymentClient.java                      @CircuitBreaker + fallback + timeouts
  InventoryClient.java                    @Bulkhead + fallback (simulated slow call)
  DownstreamController.java               fake payment-service you can switch OK / FAIL / SLOW / DECLINE
  PaymentResult / StockResult             response types (with a "degraded" flag)
  PaymentDeclinedException                business error that must not trip the breaker
src/test/java/com/learning/resilience/
  CircuitBreakerFlowTest.java             7 tests, one per breaker behaviour
  BulkheadTest.java                       2 tests: with and without a bulkhead
```

## Endpoints

| Method and path | Purpose |
|---|---|
| `POST /orders/{id}/pay` | Charge through the circuit breaker. Returns `CONFIRMED`, `PENDING` (fallback) or HTTP 402 (declined) |
| `GET /orders/{id}/cart` | Unrelated endpoint that never calls a dependency. Used to show isolation |
| `GET /orders/{id}/stock` | Stock check **with** a bulkhead (max 3 at once) |
| `GET /orders/{id}/stock-unprotected` | Same work with **no** bulkhead |
| `POST /admin/downstream?mode=OK\|FAIL\|SLOW\|DECLINE[&delayMs=n]` | Change how the fake payment service behaves |
| `GET /admin/downstream` | Current mode and how many calls actually reached the payment service |
| `POST /admin/downstream/reset-counter` | Reset that call counter |
| `POST /admin/inventory?delayMs=n` | Make the simulated inventory call slow |
| `GET /admin/inventory` | Current delay and number of inventory calls in flight |
| `GET /actuator/circuitbreakers` | Breaker state, failure rate, slow-call rate, rejected calls |
| `GET /actuator/circuitbreakerevents` | Every state change and rejected call |
| `GET /actuator/bulkheadevents` | Every permitted and rejected bulkhead call |
| `GET /actuator/metrics` | Includes `resilience4j.bulkhead.*` and `resilience4j.circuitbreaker.*` |
| `GET /actuator/health` | Includes the breaker's health |

## Configuration

All in `src/main/resources/application.yml`.

| Setting | Value | Effect |
|---|---|---|
| `server.tomcat.threads.max` | 10 | Small on purpose so thread exhaustion is easy to see (Tomcat's default is 200) |
| `payment.read-timeout` | 3s | Timeout on the payment call |
| `circuitbreaker.payment.slidingWindowSize` | 10 | Judge the last 10 calls |
| `...minimumNumberOfCalls` | 5 | Don't judge before 5 calls |
| `...failureRateThreshold` | 50 | Open at 50% failures |
| `...slowCallDurationThreshold` / `slowCallRateThreshold` | 1s / 80 | Open when 80% of calls take over 1s |
| `...waitDurationInOpenState` | 10s | Stay open for 10 seconds |
| `...permittedNumberOfCallsInHalfOpenState` | 3 | Test calls before deciding |
| `...ignoreExceptions` | `PaymentDeclinedException` | A declined card does not count as a failure |
| `bulkhead.inventory.maxConcurrentCalls` | 3 | At most 3 inventory calls at once |
| `bulkhead.inventory.maxWaitDuration` | 0 | Extra callers are refused at once, not queued |

## Scenarios and what to expect

### 1. Circuit breaker lifecycle (`./demo.sh`)

1. Healthy calls return `CONFIRMED`; the breaker is `CLOSED`.
2. Switch to `FAIL`. After enough failures the breaker goes `OPEN`.
3. While open, orders return `PENDING` with "circuit open" in about 1 ms, and the fake payment service receives **no** calls.
4. Switch back to `OK`. After 10 seconds the breaker goes `HALF_OPEN`; 3 healthy test calls close it.
5. `DECLINE` (HTTP 402) for many calls: the breaker stays `CLOSED`.
6. `SLOW` (2 seconds) with no errors: the breaker still opens, because of the slow-call rate.

Example output from a real run (shortened):

```
pay f5 -> HTTP 200 0.0012s  "status":"PENDING" "detail":"circuit open: payment deferred"
"state":"OPEN"
requests that actually reached payment-service: "callsReceived":5
```

> The failure window includes earlier successes. After one healthy call and four failures the breaker already sees 4 of 5 failed (80%) and opens on the fourth failure, not the fifth.

### 2. Bulkhead (`./demo-bulkhead.sh`)

Inventory is made slow (3 seconds). Ten customers ask for stock at the same time, then the cart page is opened.

| | Without bulkhead | With bulkhead (limit 3) |
|---|---|---|
| Stock checks that ran | 10 | 3 |
| Refused at once | 0 | 7 |
| **Cart page** | **about 2.5 s** | **about 0.002 s** |

## Tests

- `CircuitBreakerFlowTest`: closed passes calls through; failures open it and shield the dependency; business declines do not trip it; slow calls open it; recovery through half-open; failed trial calls reopen it; unrelated endpoints stay healthy.
- `BulkheadTest`: without a bulkhead the unrelated cart page waits for a free thread; with a bulkhead only 3 calls are in flight and the cart page is instant.

Tests start the whole app on a random port and shorten some settings (1s open wait, 500ms slow threshold) so they run quickly.

## Experiments

1. Set `permittedNumberOfCallsInHalfOpenState` to 50 and recover from an outage while sending traffic. What does the recovering service see?
2. Set `minimumNumberOfCalls: 1` and cause one failure. What happens, and why is that risky?
3. Remove `ignoreExceptions` and run the `DECLINE` scenario.
4. Add `@Retry` on top of `@CircuitBreaker`, then the reverse. Compare `callsReceived` for the same outage.
5. Lower `payment.read-timeout` below the `SLOW` delay. Is a timeout a failure or a slow call?
6. Set the bulkhead `maxConcurrentCalls` to 10, then to 1, and rerun `./demo-bulkhead.sh`.
7. Set the bulkhead `maxWaitDuration: 5s` and watch the cart page again.

## Troubleshooting

| Symptom | Cause and fix |
|---|---|
| `Port 8085 was already in use` | A previous run is still alive. `mvn spring-boot:run` starts a separate Java process that can outlive Maven. Find it with `lsof -nP -iTCP:8085 -sTCP:LISTEN` and stop it with `kill <pid>`. |
| Scripts print empty or odd numbers | The app is not running yet. Wait for "Started ResilienceDemoApplication", or check `curl localhost:8085/actuator/health`. |
| Breaker state looks wrong between runs | State lives in memory. Restart the app for a clean breaker, or use the reset-counter endpoint for call counts. |
| `mvn -o` fails to resolve a dependency | Run once without `-o` while online. |
| Bulkhead results differ from the table | Timings depend on your machine; the pattern (3 run, 7 refused) should not change. |

## Known limits

- One instance only. Each instance has its own breaker and bulkhead state, which is also true in production.
- The "remote" services are simulated inside the app (a controller and a sleep). Network behaviour such as connection errors is not covered.
- State is in memory and resets on restart.
- Demo scripts and example output were produced on one macOS machine with Java 21.

## Possible next additions

This is the single demo for the whole resilience area. Timeouts, retries with backoff and jitter, idempotency keys, a rate limiter and a fallback cache can each be added as another small flow in this same app, so patterns can be combined (for example retry plus breaker plus bulkhead). Add each new flow with its endpoints, settings, scenario, test and troubleshooting notes in the sections above.
