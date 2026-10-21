---
title: Messaging Cheat Sheet
tags: [messaging, cheat-sheet, revision, kafka, rabbitmq]
category: messaging
level: fundamentals
updated: "2026-10-21"
---

# Messaging Cheat Sheet

**Messaging · Revision** · Last updated 2026-10-21 · Grows as notes are added

> [!TIP]
> **The area in one line:** services send messages through a broker instead of calling each other; queues hold work to do once, logs like Kafka keep facts for many readers.
>
> **Memory hooks:** a **letter in a mailbox** versus a phone call (messaging); a **logbook that never gets torn up** (Kafka); **checkout lanes** with numbered receipts (partitions and offsets); a **courier** who sorts by address, drives full vans and asks for signatures (producer); a **team of cashiers**, one per lane (consumer group); a **bookmark on the train**: move it before reading and you skip, after and you reread (offset commits); a **head chef and sous-chefs**: only caught-up sous-chefs may take over (replication and the ISR).

## Queue versus log

| | Queue (RabbitMQ, SQS) | Log (Kafka, Kinesis) |
|---|---|---|
| Think of it as | Work to do | Facts that happened |
| After reading | Removed | Kept until retention ends |
| Replay | Normally no | Yes |
| Many readers | A queue each | Each keeps its own offset |
| Best for | Tasks, routing, low latency | Throughput, history, many consumers |

## Four questions for every design

| Question | Usual answer |
|---|---|
| Delivery? | At least once, with idempotent consumers |
| Ordering? | Only inside one queue or partition; use a key |
| Duplicates? | Idempotent consumer |
| Failures? | Retry with backoff, then dead-letter |

## Kafka in one table

| Word | Meaning |
|---|---|
| Topic | Named log |
| Partition | Ordered, append-only slice; the unit of order and scale |
| Offset | Position in a partition; the reader's bookmark |
| Consumer group | Readers sharing the work; one partition per member |
| Retention | How long events stay (default 7 days; reading doesn't delete) |
| Replication | Copies of a partition on other brokers |
| Acks | Who must confirm a write: none, leader, all in-sync replicas |

## Remember

- **Order only inside a partition**; same key, same partition (while the count is unchanged).
- **Topic + partition + offset** identifies an event.
- **Partitions can be added, not removed**, and adding moves keys.
- **Partitions cap consumer parallelism**: one partition, one member per group; extras are idle.
- **Groups are independent**: one `group.id` per application, each with its own positions.
- **Keep the poll loop fast**: exceeding `max.poll.interval.ms` (default 5 minutes) gets a member kicked out and triggers a rebalance.
- **Watch consumer lag**: it is the consumer's health signal.
- **Committed offset = next event to read.** Commit before processing = may lose; commit after = may repeat.
- **Business events: commit after processing + idempotent consumer** (auto-commit commits on a timer, not on success).
- **`send` is asynchronous**: always handle the result.
- **Safe producer:** `acks=all` + replication factor 3 + `min.insync.replicas=2` + idempotence.
- **Batching** (`linger.ms`, compression) trades milliseconds for throughput.
- **Leader serves, followers copy, in-sync followers can take over.** Factor 3 + `min.insync.replicas=2` + `acks=all` survives one broker failure with no loss.
- **Consumers read only up to the high watermark.** Unclean leader election (off by default) trades data for availability.
- **KRaft controllers need a majority**: 3 tolerate 1 failure.
- **Events in the past tense** (`OrderPlaced`), commands in the imperative (`ChargeCard`).

## Interview corner

**15-second answer:** "Messaging decouples services through a broker; Kafka is a partitioned, durable log where order holds per partition and each consumer group keeps its own offsets."

**60-second answer:**
> "Messaging decouples services through a broker. Queues remove a message once handled; logs like Kafka keep events and let each reader track its own position, which gives replay and many independent consumers. In Kafka, topics are split into partitions; order holds only inside a partition, so related events share a key. Producers batch per partition, and for business events I use acks=all with replication factor 3, min.insync.replicas 2 and idempotence. Every design needs answers on delivery, ordering, duplicates and failures: at-least-once with idempotent consumers, keys for order, and dead-letter topics."

**Likely follow-up questions:**
- **Kafka or RabbitMQ?** Facts for many readers and replay: Kafka. Tasks with rich routing: RabbitMQ.
- **How do you keep one order's events in order?** Key by order ID.
- **How do you avoid losing events when producing?** `acks=all`, `min.insync.replicas=2`, idempotence, handle send failures, outbox for critical ones.

Back to [the messaging overview](01-messaging-overview.md) · [Index](../README.md) · [Roadmap](../roadmap.md).
