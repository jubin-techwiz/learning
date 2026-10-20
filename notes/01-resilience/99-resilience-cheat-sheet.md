---
title: Resilience Cheat Sheet
tags: [resilience, cheat-sheet, revision]
category: resilience
level: fundamentals
updated: "2026-10-20"
---

# Resilience Cheat Sheet

**Category:** Resilience · **Purpose:** quick revision · **Last updated:** 2026-10-20

> [!NOTE]
> A one-page summary of the resilience track. It grows as notes are added; patterns not yet learned are marked *planned*. Read the [overview](01-resilience-overview.md) first if the terms are new.

## The idea in one line

Slow dependencies are more dangerous than dead ones, because slow calls pile up and use your threads. Resilience patterns limit the **blast radius**: how far one failure spreads.

## Pattern table

| Pattern | Question it answers | Protects against | Use when | Main pitfall |
|---|---|---|---|---|
| Timeout (*planned*) | How long will I wait for one call? | Calls that never finish | Every outside call, always | No timeout means nothing else can work |
| Retry (*planned*) | Is this failure temporary? | Short glitches | Idempotent calls to flaky dependencies | Retry storms |
| Idempotency (*planned*) | Is it safe to repeat this? | Duplicate side effects | Anything that gets retried | Assuming "POST is fine to repeat" |
| [Circuit breaker](05-circuit-breaker.md) | Has this dependency failed enough to stop calling it? | Wasting calls on a broken or slow service | Calls to a dependency that can fail for a while | Counting business errors as failures |
| [Bulkhead](06-bulkhead.md) | How many calls may use this dependency at once? | One dependency using all threads | Several dependencies share one thread pool | Limit as big as the pool, or a long wait |
| Rate limiter (*planned*) | How many calls per second do I allow? | Overload | Edges facing callers or fragile dependencies | Limits that are never tuned |
| Fallback (*planned*) | What do I answer when the real answer is missing? | Total failure | Any refusal path | A fallback that pretends success |

## Quick decision guide

| Symptom | Reach for |
|---|---|
| A call sometimes hangs | Timeout |
| A call fails now and then, then works | Retry with backoff and jitter (and idempotency) |
| A dependency is down for minutes | Circuit breaker |
| A slow dependency freezes unrelated pages | Bulkhead |
| Too much incoming traffic | Rate limiting, load shedding |
| Users see raw errors | Fallback / graceful degradation |

## Remember

- Timeouts first. Every other pattern assumes calls end.
- Bulkhead and circuit breaker are **siblings**, not parent and child. Use both.
- A fallback may give *less*, but never something *false*.
- Ignore business errors (a declined card) in a circuit breaker.
- Alert when a breaker stays open or a bulkhead keeps refusing; a polite fallback can hide an outage.

## Numbers from the examples

| Situation | Result |
|---|---|
| Breaker with a 10-call window and 50% threshold | Opened at 3 failures out of 6 calls (earlier successes count) |
| Calls while the breaker was open | About 1 ms each, and the dependency received none |
| 10 customers, slow inventory, 10 request threads | Unrelated cart page took 2.5 s without a bulkhead, 0.0015 s with a limit of 3 |

## 60-second answer

> "Resilience is about limiting how far one failure spreads. I put a timeout on every outside call, retry only idempotent calls with backoff and jitter, and wrap dependencies in a circuit breaker so I stop calling a broken one. I use a bulkhead per dependency so a slow one can't use all my threads, rate limit what comes in, and make sure every refusal returns an honest fallback. Then I watch breaker state and rejections, because fallbacks can hide outages."

Back to [the index](../README.md) · [Roadmap](../roadmap.md).
