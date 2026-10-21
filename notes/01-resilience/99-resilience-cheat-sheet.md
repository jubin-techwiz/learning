---
title: Resilience Cheat Sheet
tags: [resilience, cheat-sheet, revision]
category: resilience
level: fundamentals
updated: "2026-10-21"
---

# Resilience Cheat Sheet

**Resilience · Revision** · Last updated 2026-10-21

One page to revise the resilience area. Each row points to the note that explains it and states its conditions.

> [!TIP]
> **The whole area in one line:** slow is worse than down, so limit the blast radius in layers: timeouts, safe retries, a breaker, bulkheads, limits and shedding at the edge, honest fallbacks, and test it all.
>
> **Memory hook, the ship:** don't wait forever at the dock (timeout), try the radio again (retry), stop calling a port on fire (breaker), sealed compartments (bulkhead), a bouncer at the gangway (rate limit), turn passengers away when full (shedding), the lifeboat (fallback), the fire drill (testing).

## One question per pattern

| Pattern | Question it answers | Use when | Main pitfall |
|---|---|---|---|
| [Timeout](02-timeouts.md) | How long will I wait? | Every outside call | No timeout, or longer than the caller's |
| [Retry, backoff, jitter](03-retries-backoff-jitter.md) | Is this failure temporary? | Transient errors on idempotent calls | Retry storms; retrying at every layer |
| [Idempotency](04-idempotency.md) | Is it safe to repeat? | Anything retried or redelivered | Trusting a database record to cover an external call |
| [Circuit breaker](05-circuit-breaker.md) | Stop calling this dependency? | A dependency can fail for a while | Counting business errors as failures |
| [Bulkhead](06-bulkhead.md) | How many calls at once? | Dependencies share a thread pool | No timeout underneath; limit as big as the pool |
| [Rate limiter](07-rate-limiting.md) | How many per second? | Edges facing callers; fragile dependencies | One global limit; a default that waits instead of rejecting |
| [Load shedding](08-load-shedding-backpressure.md) | What do I drop when overloaded? | Spikes beyond capacity | Unbounded waiting; assuming server defaults shed for you |
| [Fallback](09-fallbacks-graceful-degradation.md) | What do I answer instead? | Every refusal path | A fallback that pretends success |
| [Testing](10-testing-resilience.md) | Does it really work? | Before go-live and after changes | Testing failure but not recovery |

## Definitions used in this area

| Term | Meaning here | Not to be confused with |
|---|---|---|
| **Timeout** | A limit on waiting. When it fires, the outcome is **unknown**, not "failed" | A failure signal from the dependency |
| **Idempotent / idempotency** | Repeating has the same effect as doing it once | **Deduplication**: the mechanism (remember the key, ignore repeats) that gives idempotency for the state it controls |
| **At-most-once** | Never repeated, but may be lost (for example acknowledge before processing) | At-least-once |
| **At-least-once** | Retried until acknowledged, so it may repeat; holds only while the message is retained and retry and recovery keep running | A promise of no loss under all circumstances, or of exactly-once effects |
| **Retry** | Trying a call again, for transient errors on idempotent operations, with backoff and jitter | **Recovery**: settling an unknown outcome, by reconciliation |
| **Reconciliation** | Comparing your records with the other system's and fixing differences | Retrying blindly |
| **Circuit breaker** | Stops calls after observed failures or slow calls | **Bulkhead**: limits concurrent calls from the first second |
| **Rate limiting** | A fixed per-caller policy, over time | **Load shedding**: reacts to current overload |
| **Load shedding** | Rejecting some requests early, with a fast 503 | **Backpressure**: a slow-down signal to the sender (bounded queues, pull-based consumers, 429) |
| **Failure detection** | Noticing failure: timeouts, breaker state, health signals, alerts | Handling it |
| **Graceful degradation** | Keeping the core function while optional features use fallbacks | Pretending nothing is wrong: degraded answers are flagged and counted |

## Symptom → reach for

| Symptom | Reach for |
|---|---|
| A call sometimes hangs | Timeout |
| A call fails now and then, then works | Retry with backoff and jitter (plus idempotency) |
| Customers charged twice after a timeout | A key per operation, a durable record before the call, reconciliation ([1.4](04-idempotency.md)) |
| A dependency is down for minutes | Circuit breaker |
| A slow dependency freezes unrelated pages | Bulkhead |
| One client floods the API | Rate limiting |
| Everything slows down at 2× traffic | Bounded waiting, load shedding, backpressure ([1.8](08-load-shedding-backpressure.md)) |
| Users see raw errors | Fallback, graceful degradation |
| You're not sure any of it works | Fault-injection tests, game days, chaos experiments |

## Remember

- **Timeouts first**; a timeout means *unknown outcome*.
- **Retry only transient errors, on idempotent operations, at one layer**, with backoff and jitter (3 attempts at 5 layers = up to 243× the load).
- **A database record protects database state.** An external call needs the provider's idempotency key, a durable record made *before* the call, and reconciliation.
- **The bulkhead and breaker are siblings**: how many at once versus stop after failures.
- **Ignore business errors** (a declined card) in a breaker.
- **`accept-count` is not an HTTP request queue**: bound waiting and shed in the application.
- **A fallback may give less, never something false.**
- **Alert** when a breaker stays open or a bulkhead keeps refusing.
- **Serve some well rather than all badly.**

## Numbers from example runs

These come from single example runs on specific versions, not general measurements.

| Situation | Result | Run on |
|---|---|---|
| Breaker, 10-call window, 50% threshold | Opened at 3 failures out of 6 (earlier successes count) | Spring Boot 3.2.4, Resilience4j 2.2.0 |
| Calls while the breaker was open | About 1 ms each; the dependency received none | same |
| 10 customers, slow inventory, 10 threads | Unrelated cart page: 2.5 s without a bulkhead, 0.0015 s with a limit of 3 | same |
| 12 requests, 2 worker threads, 3 s each, default `max-connections` | All accepted at once; served in waves of two, the last two at 18 s; none refused | Spring Boot 3.2.4 (Tomcat 10.1.19), macOS |
| Same, `max-connections=4`, `accept-count=1` | 5 served; 7 failed immediately at connection level | same |

## Interview corner

**15-second answer:** "I limit the blast radius in layers: timeouts, idempotent retries with backoff, circuit breakers, bulkheads, rate limiting and shedding, and honest fallbacks, then I test it with fault injection."

**60-second answer:**
> "Resilience is about limiting how far one failure spreads, and the dangerous failure is usually a slow dependency. I put a timeout on every outside call, retry only idempotent calls with backoff and jitter at one layer, and wrap dependencies in a circuit breaker so I stop calling a broken one. For payments and other external side effects I record the operation durably before the call, send one key on every attempt and reconcile unknown outcomes. I use a bulkhead per dependency so a slow one can't use all my threads, rate-limit and shed load in the application, and make sure every refusal returns an honest fallback. Then I test failures and recovery on purpose, and watch breaker state and rejections, because fallbacks can hide outages."

**Likely follow-up questions:**
- **Which comes first?** Timeouts.
- **Bulkhead versus breaker?** Concurrent-call limit versus stop-after-failures.
- **Rate limiting versus load shedding?** Fixed per-caller policy versus reacting to current overload.
- **Does at-least-once mean nothing is lost?** Only while the message is retained and retried until acknowledged; it means duplicates are possible, so consumers deduplicate.
- **How do you know it works?** Fault-injection tests, game days, chaos experiments with stop conditions, and recovery checks.

Back to [the overview](01-resilience-overview.md) · [Index](../README.md) · [Roadmap](../roadmap.md).
