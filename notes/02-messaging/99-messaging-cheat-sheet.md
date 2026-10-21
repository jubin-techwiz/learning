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
> **Memory hooks:** a **letter in a mailbox** versus a phone call (messaging); a **logbook that never gets torn up** (Kafka); **checkout lanes** with numbered receipts (partitions and offsets); a **courier** who sorts by address, drives full vans and asks for signatures (producer); a **team of cashiers**, one per lane (consumer group); a **bookmark on the train**: move it before reading and you skip, after and you reread (offset commits); a **head chef and sous-chefs**: only caught-up sous-chefs may take over (replication and the ISR); **waiters and tables**: eager redraws every table, cooperative moves only some, static members keep their tables during a short break (rebalancing); the **"undeliverable" shelf** at the post office (dead-letter topic); a **bank transfer**: both sides or neither (transactions); a **standard plug socket**: add features, never change the plug (schemas); **airport check-in desks**: families together, enough desks for the busiest day, and a 300-person tour group jams one desk (ordering and hot partitions); a **paper archive**: shred old boxes, keep the latest file per customer, or move boxes to a cheap warehouse (retention, compaction, tiered storage); a **live scoreboard** rebuilt from the list of goals (Kafka Streams); the **till roll** (CDC); an **office badge** (security); a **car dashboard** (monitoring); a **spare key with a neighbour** (disaster recovery).

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
- **Rebalances:** eager stops everyone; cooperative pauses only moving partitions; static membership (`group.instance.id`) means quick restarts don't rebalance. Commit in `onPartitionsRevoked`, never in `onPartitionsLost`.
- **Errors:** transient = retry with backoff (in place, or retry topics, which can reorder); permanent = dead-letter at once. Never let a poison message block a partition. A dependency outage is not poison: pause instead. A DLT needs alerts, an owner and replay.
- **Exactly-once** = idempotent producer + transactions (outputs and offsets atomic) + `read_committed` consumers + zombie fencing via `transactional.id`. It covers Kafka-to-Kafka only; databases and APIs need idempotency or the outbox. Kafka Streams: `exactly_once_v2`.
- **Schemas are contracts:** Avro or Protobuf in a schema registry; messages carry a schema ID; BACKWARD (default) means upgrade consumers first; add optional fields with defaults, never rename or retype; breaking change = new event type.
- **Planning:** ordering scope first (usually per entity) → key with many even values → partitions ≥ max(t/p, t/c), measured, sized 1–2 years ahead. A hot key stays hot whatever the partition count: shard it or give it its own topic.
- **Spring Boot:** properties in, beans out; one shared `KafkaTemplate`; listener container = poll + commit + error handler; concurrency × instances ≤ partitions; observation on; real broker in tests.
- **Storage:** retention for events, compaction (latest per key, tombstones delete) for current state, tiered storage for long history. Disk = volume × days × replication factor.
- **Kafka Streams:** KStream = events, KTable = latest per key; local RocksDB state backed by changelog topics; joins need co-partitioning. Flink for larger, multi-system processing.
- **Connect and CDC:** connectors instead of glue code; Debezium reads the binlog (snapshot, then changes); outbox + CDC avoids dual writes; sink DLQ yes, source DLQ no.
- **Security:** TLS everywhere, one identity per service (SCRAM, mTLS or OAuth), least-privilege ACLs; SASL alone doesn't encrypt.
- **Monitoring:** page on offline / under-min-ISR partitions and controller ≠ 1; alert on lag **growth** per group; capacity planned and load-tested before peaks.
- **DR:** zones protect within a region; a second cluster with MirrorMaker 2 (topics + offset checkpoints) protects against region loss; RPO > 0 with async copying; rehearse failover.
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
