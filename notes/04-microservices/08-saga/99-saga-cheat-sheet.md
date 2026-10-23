---
title: Saga Cheat Sheet
tags: [microservices, saga, cheat-sheet, revision]
category: microservices
level: fundamentals
updated: "2026-10-23"
---

# Saga Cheat Sheet

**Microservices · Saga · Revision** · Last updated 2026-10-23

One page to revise the Saga series, [4.8.1](01-saga-overview.md) to [4.8.8](08-case-checkout-saga.md).

> [!TIP]
> **The whole series in one line:** a saga is a chain of local transactions, each with an idempotent undo, coordinated by events or an orchestrator, made reliable with an outbox and a durable state, and made safe under concurrency with pending states and order-independent updates.

## Do I need a saga?

| Question | If yes |
|---|---|
| Does one business operation change data in more than one service? | You need to coordinate: a saga (or merge the data into one service) |
| Can the data that must change together live in one service? | Do that first: a local ACID transaction beats any saga |
| Can every step be undone (or moved after the point of no return)? | A saga fits |
| Must other users never see an intermediate state? | Sagas will not give you that without countermeasures; reconsider the boundaries |
| Are all participants able and willing to join a distributed transaction? | Rare; usually no: sagas |

## The pieces

| Piece | Rule | Lesson |
|---|---|---|
| **Steps** | Compensable (undoable) first, **pivot** (go/no-go), retriable (retried) last | [4.8.2](02-saga-steps-and-compensation.md) |
| **Compensation** | A new transaction (status change or counter-entry), idempotent, tolerant of arriving early or twice | [4.8.2](02-saga-steps-and-compensation.md) |
| **Order** | Likeliest to fail and cheapest to undo first; irreversible steps (email, shipment) after the pivot | [4.8.2](02-saga-steps-and-compensation.md) |
| **Choreography** | Events, no coordinator; good for 2 to 4 steps; flow implicit; cycles and lost compensations | [4.8.3](03-choreography.md) |
| **Orchestration** | A coordinator with a persisted state machine; commands out, replies in; keep it thin | [4.8.4](04-orchestration.md) |
| **Reliability** | Outbox (data and message in one transaction), publish then mark, at-least-once, inbox or natural key, stable message ids, key by saga id | [4.8.5](05-reliable-messaging-outbox-inbox.md) |
| **Isolation** | None by default. Countermeasures: semantic lock, commutative updates (deltas), pessimistic view, reread value, version file, risk-based concurrency | [4.8.6](06-isolation-and-countermeasures.md) |
| **Failures** | Refusal: compensate. Technical error: retry (same key, backoff, jitter, limit). No reply: outcome unknown, so compensate safely. Unfixable: `NEEDS_ATTENTION` + alert | [4.8.7](07-failures-timeouts-operations.md) |
| **Operations** | Saga id everywhere; metrics: in flight, stuck, oldest idle, compensation rate; reaper; versioned definitions; fault-injection tests | [4.8.7](07-failures-timeouts-operations.md) |

## What a saga guarantees

| Property | Saga | Note |
|---|---|---|
| Atomicity | Eventually: all steps or compensated | During the saga, partial state is visible |
| Consistency | Yes, at the end, if compensations are correct | Business rules, not database constraints |
| Isolation | **No** | Design countermeasures |
| Durability | Yes, each local transaction | Plus a durable saga state for recovery |

## Choreography or orchestration

| | Choreography | Orchestration |
|---|---|---|
| Flow lives in | Subscriptions of many services | One orchestrator |
| Best for | Few steps, stable flow, reacting services | More steps, branches, deadlines, visibility |
| Main risk | Implicit flow, cycles, forgotten compensations | A god service, a component to keep available |

## Checklist for a saga design

1. List the steps with their services, and write each compensation in business words.
2. Classify each step; choose the **pivot** deliberately; move irreversible steps after it.
3. Give every command an id (`sagaId:step`); make steps and undos idempotent; handle "undo before action".
4. Persist the saga state with the outgoing command (outbox); accept only the expected reply.
5. Choose the isolation countermeasures for every shared record; add a `*_PENDING` state where users can act.
6. Write the failure policy: refusal, technical error, unknown outcome, compensation failure; set retries, timeouts and deadlines.
7. Add monitoring: saga id in logs, stuck sagas, oldest idle, `NEEDS_ATTENTION` alerts.
8. Test every path with fault injection, duplicates and a crash in the middle.

## Interview questions

- **What is a saga?** A business transaction made of local transactions with compensating transactions for failures, replacing a distributed transaction ([4.8.1](01-saga-overview.md)).
- **Why not two-phase commit?** It blocks, couples availability, and many participants (payment providers, brokers) cannot join ([4.8.1](01-saga-overview.md)).
- **What is a pivot transaction?** The go/no-go step; after it succeeds the saga completes, so later steps are retried, not undone ([4.8.2](02-saga-steps-and-compensation.md)).
- **How do you make compensations safe?** Idempotent, keyed by saga and step, tolerant of arriving early or twice ([4.8.2](02-saga-steps-and-compensation.md)).
- **Choreography versus orchestration?** Events with an implicit flow versus a coordinator with explicit, persisted state ([4.8.3](03-choreography.md), [4.8.4](04-orchestration.md)).
- **How do you avoid lost or duplicated messages?** Transactional outbox, at-least-once delivery, idempotent consumers with an inbox ([4.8.5](05-reliable-messaging-outbox-inbox.md)).
- **What about isolation?** Sagas have none; use semantic locks, commutative updates, pessimistic views and rereads ([4.8.6](06-isolation-and-countermeasures.md)).
- **What do you do on a timeout?** Treat it as an unknown outcome: retry with the same key, then compensate with an operation that is safe either way ([4.8.7](07-failures-timeouts-operations.md)).
- **What if a compensation fails?** Retry, then move to needs-attention and alert a person with the saga state ([4.8.7](07-failures-timeouts-operations.md)).
- **Design checkout across four services.** Orchestrated saga: create order (lock), reserve stock, authorise card, capture as pivot, confirm, email; idempotent commands, durable state, failure matrix ([4.8.8](08-case-checkout-saga.md)).

Back to [the Saga overview](01-saga-overview.md) · [the Microservices overview](../01-microservices-overview.md) · [the index](../../README.md) · [Roadmap](../../roadmap.md).
