---
title: Messaging Cheat Sheet
tags: [messaging, cheat-sheet, revision, kafka, rabbitmq]
category: messaging
level: fundamentals
updated: "2026-10-21"
---

# Messaging Cheat Sheet

**Messaging · Revision** · Last updated 2026-10-21 · Grows as notes are added

One page to revise the messaging area. Each row points back to the note that explains it; exact definitions are in the [messaging glossary](98-messaging-glossary.md).

> [!TIP]
> **The area in one line:** services send messages through a broker instead of calling each other; queues hold work to do once, logs like Kafka keep facts for many readers.

## Queue versus log

| | Queue (RabbitMQ, SQS) | Log (Kafka, Kinesis) |
|---|---|---|
| Think of it as | Work to do | Facts that happened |
| After reading | Removed once acknowledged | Kept until retention ends |
| Replay | Normally no | Yes |
| Many readers | A queue each | Each group keeps its own offset |
| Best for | Tasks, routing, low latency | Throughput, history, many consumers |

The line is blurring: RabbitMQ has streams, and Kafka's queue-style **share groups** are production-ready from Kafka 4.2.

## Four questions for every design

| Question | Usual answer |
|---|---|
| Delivery? | At-least-once, with idempotent consumers |
| Ordering? | Kafka: only within a partition (use a key). Queue: only with one consumer and no redelivery; competing consumers or retries can reorder |
| Duplicates? | Idempotent consumer for your own state (dedupe by event ID, upserts); the provider's idempotency key for external calls |
| Failures? | Retry transient errors with backoff; dead-letter permanent ones |

## Who guarantees what

| Layer | Guarantees | Does not guarantee |
|---|---|---|
| Kafka brokers | Durable, ordered storage per partition (with `acks=all`, replication factor 3, `min.insync.replicas=2`) | That anyone processed the event |
| Producer client | Retries without duplicates within a producer session (idempotence); transactions make writes and consumed offsets atomic across Kafka topics only | That your database write and the send both happen; anything outside Kafka |
| Spring Kafka | Polling, commits per ack mode (`BATCH` by default: after the poll's records are handled), retries and DLT publishing if configured | Idempotent business logic; successful processing of a dead-lettered event |
| Your application | Idempotent processing of its own state (dedupe in the same database transaction); outbox for database + event; ownership and replay of the DLT | Duplicate-free calls to other systems unless they support idempotency |
| External systems (email, payments) | Their own semantics; use their idempotency keys | Kafka's exactly-once and your database dedupe do not reach them |

## Kafka notes at a glance

| Step | Topic | Memory hook | The rule to remember |
|---|---|---|---|
| [2.2.2](02-kafka/02-log-topics-partitions-offsets.md) | Log, partitions, offsets | Checkout lanes with numbered receipts | Order only inside a partition; topic + partition + offset identifies an event; reading doesn't delete |
| [2.2.3](02-kafka/03-producers-keys-acks-batching.md) | Producers | A courier who sorts by address and asks for signatures | Same key, same partition (while the count is unchanged); `acks=all` + idempotence; `send` is asynchronous, so handle the result |
| [2.2.4](02-kafka/04-consumers-consumer-groups.md) | Consumer groups | A team of cashiers, one per lane | One partition, one member per group; partitions cap parallelism; watch lag |
| [2.2.5](02-kafka/05-offset-commits-delivery-guarantees.md) | Offset commits | A bookmark on the train | Committed offset = next to read. Commit before processing may lose; after may repeat. Commit after processing + idempotent consumer; the dedupe record covers your database state, not repeated external calls |
| [2.2.6](02-kafka/06-brokers-replication-leader-election.md) | Replication | Head chef and sous-chefs | Only in-sync replicas take over; readers see up to the high watermark; 3 KRaft controllers tolerate 1 failure |
| [2.2.7](02-kafka/07-rebalancing-static-membership.md) | Rebalancing | Waiters and tables | Cooperative moves only some partitions; static membership survives quick restarts; commit in `onPartitionsRevoked` |
| [2.2.8](02-kafka/08-error-handling-retries-dlt.md) | Errors and DLTs | The "undeliverable" shelf | Transient: retry with backoff. Permanent: dead-letter at once. Outage: pause. A DLT entry means the work was **not** done, and replays can arrive out of order; it needs alerts, an owner and replay |
| [2.2.9](02-kafka/09-idempotent-producer-transactions.md) | Exactly-once | A bank transfer | Idempotent producer + transactions + `read_committed` + fencing; Kafka-to-Kafka only; rewind after abort |
| [2.2.10](02-kafka/10-schemas-evolution.md) | Schemas | A standard plug socket | BACKWARD (default): upgrade consumers first; add optional fields with defaults; breaking change = new event type |
| [2.2.11](02-kafka/11-ordering-partition-planning-hot-partitions.md) | Partition planning | Airport check-in desks | Partitions ≥ max(t/p, t/c), measured; a hot key stays hot whatever the count |
| [2.2.12](02-kafka/12-kafka-with-spring-boot.md) | Spring Boot | Properties in, beans out | One shared `KafkaTemplate`; concurrency × instances ≤ partitions; client auto-commit off by default |
| [2.2.13](02-kafka/13-retention-compaction-tiered-storage.md) | Storage | A paper archive | Retention for events, compaction for current state, tombstones delete keys; disk = volume × days × replication factor |
| [2.2.14](02-kafka/14-kafka-streams-basics.md) | Kafka Streams | A live scoreboard | KStream = events, KTable = latest per key; state in RocksDB + changelog topics; joins need co-partitioning |
| [2.2.15](02-kafka/15-kafka-connect-cdc.md) | Connect and CDC | Reading the till roll | Outbox + CDC avoids dual writes, but is eventual and at-least-once; sink DLQ yes, source DLQ no |
| [2.2.16](02-kafka/16-security-tls-sasl-acls.md) | Security | An office badge | TLS everywhere; one identity per service; least-privilege ACLs; SASL alone doesn't encrypt |
| [2.2.17](02-kafka/17-monitoring-lag-capacity.md) | Monitoring | A car dashboard | Page on offline / under-min-ISR partitions and controller ≠ 1; alert on lag growth per group |
| [2.2.18](02-kafka/18-multi-cluster-disaster-recovery.md) | Disaster recovery | A spare key with a neighbour | A second cluster for region loss; MirrorMaker 2 offsets are approximate; RPO > 0; rehearse failover |

## Defaults worth knowing

Client, broker and topic values were read from Apache Kafka 4.3.2 on 2026-10-21; the "since version X" history comes from the Apache Kafka upgrade notes and KIPs; the Spring Kafka rows come from its reference documentation (3.1 to 4.1 pages agree). Defaults change between versions, so re-verify for yours.

| Setting | Default | Note |
|---|---|---|
| `retention.ms` | 7 days (168 hours) | Per topic |
| `acks` | `all` (since 3.0) | Idempotence also defaults to on since 3.0; set it explicitly on early 3.x clients |
| `linger.ms` | 5 ms from Kafka 4.0 (0 before) | Small wait to fill batches |
| `max.block.ms` | 60 s | How long `send` can block for metadata or buffer space |
| `max.poll.interval.ms` | 5 minutes | Exceed it and the member leaves the group |
| `session.timeout.ms` | 45 s (since 3.0) | Missed heartbeats |
| `max.poll.records` | 500 | Records per poll |
| `auto.offset.reset` | `latest` | A new group skips history unless set to `earliest` |
| `isolation.level` | `read_uncommitted` | Set `read_committed` for transactional pipelines |
| `offsets.retention.minutes` | 7 days | Committed offsets of an idle group expire |
| `delivery.timeout.ms` / `transaction.timeout.ms` | 2 min / 1 min | Producer give-up time / open transaction limit |
| `auto.commit.interval.ms` | 5 s | Client auto-commit; Spring Kafka turns auto-commit off |
| `delete.retention.ms` | 24 hours | How long tombstones stay in compacted topics |
| Unclean leader election | Off | Turning it on trades data for availability |
| Spring `DefaultErrorHandler` | 10 attempts, no delay, then log and skip | No DLT unless you configure one |
| Authorisation with no matching ACL | Deny (except `super.users`) | Unless `allow.everyone.if.no.acl.found=true` |

## Interview corner

**15-second answer:** "Messaging decouples services through a broker; Kafka is a partitioned, durable log where order holds per partition and each consumer group keeps its own offsets."

**60-second answer:**
> "Messaging decouples services through a broker. Queues remove a message once handled; logs like Kafka keep events and let each reader track its own position, which gives replay and many independent consumers. In Kafka, topics are split into partitions; order holds only inside a partition, so related events share a key. For business events I use acks=all, replication factor 3, min.insync.replicas 2 and idempotence, commit offsets after processing, and make consumers idempotent. Permanent failures go to a dead-letter topic, and database changes are published through an outbox, which is reliable but at-least-once."

**Likely follow-up questions:**
- **Kafka or RabbitMQ?** Facts for many readers and replay: Kafka. Tasks with rich routing: RabbitMQ.
- **How do you keep one order's events in order?** Key by order ID, and don't change the partition count casually.
- **Does Kafka give exactly-once?** Inside Kafka, with transactions. Across a database or an email provider, no: use idempotency and the outbox.
- **How do you avoid losing events when producing?** `acks=all`, `min.insync.replicas=2`, idempotence, handle send failures, outbox for critical ones.

Back to [the messaging overview](01-messaging-overview.md) · [Glossary](98-messaging-glossary.md) · [Index](../README.md) · [Roadmap](../roadmap.md).
