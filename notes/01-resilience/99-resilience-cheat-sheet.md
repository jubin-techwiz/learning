---
title: Resilience Cheat Sheet
tags: [resilience, cheat-sheet, revision]
category: resilience
level: fundamentals
updated: "2026-10-21"
---

# Resilience Cheat Sheet

**Resilience · Revision** · Last updated 2026-10-21

> [!TIP]
> **The whole track in one line:** slow is worse than down, so limit the blast radius in layers: timeouts, safe retries, a breaker, bulkheads, limits at the edge, honest fallbacks, and test it all.
>
> **Memory hook, the ship:** don't wait forever at the dock (timeout), try the radio again (retry), stop calling a port on fire (breaker), sealed compartments (bulkhead), a bouncer at the gangway (rate limit), turn passengers away when full (shedding), the lifeboat (fallback), the fire drill (testing).

## One question per pattern

| Pattern | Question it answers | Use when | Main pitfall |
|---|---|---|---|
| [Timeout](02-timeouts.md) | How long will I wait? | Every outside call | No timeout, or longer than the caller's |
| [Retry, backoff, jitter](03-retries-backoff-jitter.md) | Is this failure temporary? | Transient errors on idempotent calls | Retry storms; retrying at every layer |
| [Idempotency](04-idempotency.md) | Is it safe to repeat? | Anything retried or redelivered | A new key on each retry |
| [Circuit breaker](05-circuit-breaker.md) | Stop calling this dependency? | A dependency can fail for a while | Counting business errors as failures |
| [Bulkhead](06-bulkhead.md) | How many calls at once? | Dependencies share a thread pool | Limit as big as the pool |
| [Rate limiter](07-rate-limiting.md) | How many per second? | Edges facing callers; fragile dependencies | One global limit for everyone |
| [Load shedding](08-load-shedding-backpressure.md) | What do I drop when overloaded? | Spikes beyond capacity | Unbounded queues; shedding health checks |
| [Fallback](09-fallbacks-graceful-degradation.md) | What do I answer instead? | Every refusal path | A fallback that pretends success |
| [Testing](10-testing-resilience.md) | Does it really work? | Before go-live and after changes | Testing failure but not recovery |

## Symptom → reach for

| Symptom | Reach for |
|---|---|
| A call sometimes hangs | Timeout |
| A call fails now and then, then works | Retry with backoff and jitter (plus idempotency) |
| Customers charged twice after a timeout | Idempotency key |
| A dependency is down for minutes | Circuit breaker |
| A slow dependency freezes unrelated pages | Bulkhead |
| One client floods the API | Rate limiting |
| Everything slows down at 2x traffic | Load shedding, bounded queues, backpressure |
| Users see raw errors | Fallback, graceful degradation |
| You're not sure any of it works | Fault-injection tests, game days, chaos experiments |

## Remember

- **Timeouts first**; a timeout means *unknown outcome*.
- **Retry only transient errors, on idempotent operations, at one layer**, with backoff and jitter (3 attempts at 5 layers = 243× load).
- **The bulkhead and breaker are siblings**: how many at once versus stop after failures.
- **Ignore business errors** (a declined card) in a breaker.
- **A fallback may give less, never something false.**
- **Alert** when a breaker stays open or a bulkhead keeps refusing.
- **Serve some well rather than all badly.**

## Numbers from example runs

| Situation | Result |
|---|---|
| Breaker, 10-call window, 50% threshold | Opened at 3 failures out of 6 (earlier successes count) |
| Calls while the breaker was open | About 1 ms each; the dependency received none |
| 10 customers, slow inventory, 10 threads | Unrelated cart page: 2.5 s without a bulkhead, 0.0015 s with a limit of 3 |

## Interview corner

**15-second answer:** "I limit the blast radius in layers: timeouts, idempotent retries with backoff, circuit breakers, bulkheads, rate limiting and shedding, and honest fallbacks, then I test it with fault injection."

**60-second answer:**
> "Resilience is about limiting how far one failure spreads, and the dangerous failure is usually a slow dependency. I put a timeout on every outside call, retry only idempotent calls with backoff and jitter at one layer, and wrap dependencies in a circuit breaker so I stop calling a broken one. I use a bulkhead per dependency so a slow one can't use all my threads, rate-limit and shed load at the edge, and make sure every refusal returns an honest fallback. Then I test failures and recovery on purpose, and watch breaker state and rejections, because fallbacks can hide outages."

**Likely follow-up questions:**
- **Which comes first?** Timeouts.
- **Bulkhead versus breaker?** Concurrent-call limit versus stop-after-failures.
- **Rate limiting versus load shedding?** Fixed per-caller policy versus reacting to current overload.
- **How do you know it works?** Fault-injection tests, game days, chaos experiments with stop conditions.

Back to [the overview](01-resilience-overview.md) · [Index](../README.md) · [Roadmap](../roadmap.md).
