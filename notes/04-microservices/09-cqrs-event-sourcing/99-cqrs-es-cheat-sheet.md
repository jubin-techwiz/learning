---
title: CQRS and Event Sourcing Cheat Sheet
tags: [microservices, cqrs, event-sourcing, cheat-sheet, revision]
category: microservices
level: fundamentals
updated: "2026-10-23"
---

# CQRS and Event Sourcing Cheat Sheet

**Microservices · CQRS and event sourcing · Revision** · Last updated 2026-10-23

One page to revise the series, [4.9.1](01-cqrs-es-overview.md) to [4.9.7](07-case-order-history.md).

> [!TIP]
> **The whole series in one line:** CQRS gives reads and writes their own models; event sourcing stores what happened instead of what is; together, small pure rules decide new events, projections build disposable views from them, and the cost is eventual consistency, event design and operations.

## Do I need these patterns?

| Question | If yes |
|---|---|
| Is it plain CRUD with simple rules? | Stay with CRUD. Fowler: "for most systems CQRS adds risky complexity" |
| Do reads and writes differ a lot in shape or load? | CQRS (start with read tables, then a separate store) |
| Does history matter (audit, disputes, "how did we get here")? | Event sourcing for that aggregate |
| Is the data reference data (catalogue, settings)? | CRUD, cached |
| Do users need an immediately consistent view of everything? | Be careful: projections are eventually consistent |
| Is the team new to event-driven design? | Start small, one bounded context |

## The pieces

| Piece | Rule | Lesson |
|---|---|---|
| **Command / query** | A command is an intent that may be refused and returns an acknowledgement (a position); a query changes nothing | [4.9.2](02-cqrs-separate-models.md) |
| **Levels of CQRS** | Logical split → read tables in the same transaction → separate store (eventual) → plus events | [4.9.2](02-cqrs-separate-models.md) |
| **Read your writes** | Use the command's response, or wait for the view to reach the command's position | [4.9.2](02-cqrs-separate-models.md) |
| **Event** | Immutable fact, past tense, business language, small, with metadata | [4.9.3](03-event-sourcing.md) |
| **Aggregate / stream** | Smallest consistency unit; one stream per aggregate; keep streams short | [4.9.3](03-event-sourcing.md) |
| **`decide` / `evolve`** | `decide` = rules, returns events or refuses; `evolve` = pure, never refuses, no side effects | [4.9.3](03-event-sourcing.md) |
| **Expected version** | Append only if the stream is still at the version you read; the loser reloads and decides again | [4.9.3](03-event-sourcing.md) |
| **Projection** | Log → read model with a checkpoint; idempotent; rebuildable; new question = new projection | [4.9.4](04-projections-read-models.md) |
| **Reactor** | Side effects (email, commands) only on new events, never during rebuild | [4.9.4](04-projections-read-models.md) |
| **Rebuild** | New read model beside the old one from position 0, compare, switch, delete | [4.9.4](04-projections-read-models.md) |
| **Snapshot** | A cache of state at a version; regenerate when the logic changes | [4.9.5](05-snapshots-versioning-privacy.md) |
| **Schema change** | Tolerant readers → versioned events → upcasters on read; in-place rewrite as a last resort | [4.9.5](05-snapshots-versioning-privacy.md) |
| **Privacy** | Personal data outside the stream, or per-person key and delete the key; erase derived copies too | [4.9.5](05-snapshots-versioning-privacy.md) |
| **Event table** | Unique `(stream_id, version)`; positions have gaps; late commits need a safe watermark | [4.9.6](06-event-store-postgresql-integration.md) |
| **Checkpoint** | Same transaction as the read-model change; skip and record poison events | [4.9.6](06-event-store-postgresql-integration.md) |
| **Public events** | A versioned contract published by a relay (or CDC), at-least-once, keyed by stream | [4.9.6](06-event-store-postgresql-integration.md) |

## Four combinations

| | One model | Separate read models |
|---|---|---|
| **State stored** | CRUD (default) | CQRS only |
| **Events stored** | Event sourcing only | CQRS + event sourcing |

## Checklist for a design

1. Name the questions that need history or a different shape; if there are none, use CRUD.
2. Choose the bounded contexts that get the patterns; leave the rest alone.
3. Model events with the domain experts: facts in the past tense; keep aggregates small.
4. Write `decide` and `evolve` as pure functions; test rules as given-when-then.
5. Append with the expected version; handle the conflict by reloading.
6. One projection per question; checkpoint with the read model; idempotent; no side effects; a rebuild runbook.
7. Return a position from commands; implement read-your-writes where users just acted.
8. Publish public events through a relay or CDC; keep internal events private.
9. Plan for versions (upcasters, compatibility tests), privacy (key per person) and growth (partitioning, snapshots).
10. Monitor lag per projection, skipped events and rebuild time.

## Interview questions

- **What is CQRS?** Separate models for commands and queries; the read side is shaped for queries and eventually consistent ([4.9.2](02-cqrs-separate-models.md)).
- **What is event sourcing?** Store the sequence of events as the source of truth; state is derived by replay ([4.9.3](03-event-sourcing.md)).
- **Are they the same?** No: independent, often combined ([4.9.1](01-cqrs-es-overview.md)).
- **How do you stop two writers overwriting each other?** Append with an expected version; unique `(stream, version)` in a table ([4.9.3](03-event-sourcing.md), [4.9.6](06-event-store-postgresql-integration.md)).
- **How do you handle stale reads?** Return the new data in the command's response, or wait for the view's position ([4.9.2](02-cqrs-separate-models.md)).
- **How do you fix a bug in a read model?** Fix the projection and rebuild it from position 0 beside the old one ([4.9.4](04-projections-read-models.md)).
- **How do you change an event's shape?** Version it and upcast on read; never rewrite history in place ([4.9.5](05-snapshots-versioning-privacy.md)).
- **How do you honour the right to erasure?** Keep personal data out of events or use per-person keys and delete the key, then erase derived copies ([4.9.5](05-snapshots-versioning-privacy.md)).
- **What goes wrong with a relational event store?** Late commits hiding events from readers, gaps in positions, stuck projections ([4.9.6](06-event-store-postgresql-integration.md)).
- **When would you not use them?** Simple CRUD, reference data, short-lived systems, teams new to events, or where immediate consistent views are required ([4.9.1](01-cqrs-es-overview.md)).

Back to [the series overview](01-cqrs-es-overview.md) · [the Microservices overview](../01-microservices-overview.md) · [the index](../../README.md) · [Roadmap](../../roadmap.md).
