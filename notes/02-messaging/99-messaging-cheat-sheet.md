---
title: Messaging Cheat Sheet
tags: [messaging, cheat-sheet, revision, kafka, rabbitmq]
category: messaging
level: fundamentals
updated: "2026-10-20"
---

# Messaging Cheat Sheet

**Category:** Messaging · **Purpose:** quick revision · **Last updated:** 2026-10-20

> [!NOTE]
> A one-page summary of the messaging area. It grows as notes are added; topics not yet learned are marked *planned*. Read the [messaging overview](01-messaging-overview.md) first if the terms are new.

## The idea in one line

Services send messages through a broker instead of calling each other, so they are decoupled in time and knowledge. The price is duplicates, ordering questions and eventual consistency.

## Queue versus log

| | Queue (RabbitMQ, SQS) | Log (Kafka, Kinesis) |
|---|---|---|
| Think of it as | Work to be done | Facts that happened |
| After reading | Message removed once acknowledged | Message kept until retention ends |
| Replay | Normally no | Yes |
| Many independent readers | One queue per reader | Each reader keeps its own offset |
| Best for | Task distribution, rich routing, low latency | High throughput, history, many consumers |

## Four questions for every design

| Question | Usual answer |
|---|---|
| Delivery guarantee? | At least once, with idempotent consumers |
| Ordering? | Only inside one queue or partition; use a key |
| Duplicates? | Make the consumer idempotent |
| Failures? | Retry with backoff, then dead-letter |

## Kafka vocabulary ([big picture](02-kafka/01-kafka-overview.md))

| Word | Meaning |
|---|---|
| Topic | Named log |
| Partition | Ordered, append-only slice of a topic; the unit of scale and of ordering |
| Offset | Position of an event inside a partition; the consumer's bookmark |
| Consumer group | Consumers sharing the work; each partition goes to one member |
| Retention | How long events are kept (default 7 days) |
| Replication | Copies of a partition on other brokers |

## Remember

- Order is guaranteed only inside a partition. Same key means same partition.
- Parallelism is capped by partitions: more consumers than partitions leaves the extras idle.
- Reading does not delete in Kafka. Each group has its own offset.
- Events are named in the past tense (`OrderPlaced`); commands in the imperative (`ChargeCard`).
- A queue is about work; a log is about facts.

## 60-second answer

> "Messaging decouples services through a broker. There are two families: queues, where a message is removed once handled, and logs like Kafka, where events are kept and each reader tracks its own position, which gives replay and many independent consumers. Every design needs answers on delivery guarantees, ordering, duplicates and failures: I use at-least-once delivery with idempotent consumers, keep order with keys, and send poison messages to a dead-letter queue."

Back to [the index](../README.md) · [Roadmap](../roadmap.md).
