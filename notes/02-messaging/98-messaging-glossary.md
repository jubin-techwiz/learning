---
title: Messaging Glossary
tags: [messaging, kafka, glossary, terminology, revision]
category: messaging
level: fundamentals
updated: "2026-10-21"
---

# Messaging Glossary

**Messaging · Revision** · Last updated 2026-10-21

The canonical definitions for the messaging area. Notes may shorten these but never contradict them. Each term links to the note that teaches it.

> [!TIP]
> Agreed spellings: **dead-letter topic (DLT)** for Kafka, **dead-letter queue (DLQ)** for queue brokers and Kafka Connect; **exactly-once**, **at-least-once**, **at-most-once** (hyphenated as adjectives); **acknowledgement (ack)**.

## Core messaging

| Term | Simple meaning | Why it matters | Example | Related terms |
|---|---|---|---|---|
| **Message broker** | The middleman that stores and delivers messages | Lets services communicate without being available at the same moment | Kafka cluster, RabbitMQ | Queue, log |
| **Producer** | The application that sends messages | Decides content, key and durability settings | Order Service publishing `OrderPlaced` | Publisher |
| **Consumer** | The application that receives messages | Decides how to process, acknowledge and handle failures | Notification Service reading `OrderPlaced` | Subscriber, consumer group |
| **Event** | A message saying something happened (past tense) | Producers don't need to know who reacts | `OrderPlaced` | Command |
| **Command** | A message asking one receiver to do something | Implies an owner and an expected action | `ChargeCard` | Event |
| **Queue** | Messages wait; each is taken by one consumer and then removed | Distributes work; no replay by default | RabbitMQ queue, Amazon SQS | Log, competing consumers |
| **Log** | Messages are appended and kept; each reader keeps its own position | Many independent readers and replay | Kafka topic | Queue, retention |
| **Delivery guarantee** | What a system promises about loss and duplication | Drives idempotency and failure handling | At-least-once | At-most-once, exactly-once |
| **At-most-once** | May lose a message, never repeats it | Acceptable only for data you can lose | Committing before processing | At-least-once |
| **At-least-once** | Doesn't lose a message, may repeat it; holds only while the message is retained and retried until processed | The usual choice; needs idempotent consumers | Committing after processing | Idempotent consumer |
| **Exactly-once** | Each message's effect happens once | Kafka offers it for Kafka-to-Kafka processing only | Kafka transactions, Kafka Streams | Effectively once |
| **Effectively once** | Messages may arrive more than once, but their effect happens once | What most business systems actually need | At-least-once delivery + idempotent consumer | Exactly-once |
| **Idempotent consumer** | Handles the same message twice without a second effect | Makes at-least-once safe | Processed-events table with a unique key | Deduplication, idempotency key |
| **Poison message** | A message that will always fail processing | Can block a partition or queue | Malformed product code | Dead-letter topic |
| **Dead-letter topic (DLT) / queue (DLQ)** | Where messages that can't be processed are parked with error details | Keeps the main flow moving; needs an owner | `order-events.DLT` (name depends on configuration) | Retry topic, replay |
| **Backpressure** | A "slow down" signal from a busy receiver to the sender | Prevents unbounded queues and overload | Kafka consumers pull only what they can process | Load shedding |
| **Event replay** | Reading old messages again on purpose | Recovers from bugs and builds new read models | Resetting a group's offsets | Retention |
| **Eventual consistency** | All parts agree after a short delay, not instantly | Messaging trades instant answers for independence | Stock updated a moment after the order | Strong consistency |
| **Transactional outbox** | Write the event to an outbox table in the same database transaction as the data, then publish it | Avoids dual writes | Order and `OrderPlaced` row in one transaction | Dual write, CDC |
| **Dual write** | Writing to two systems separately | They can end up disagreeing after a crash | Insert into MySQL, then send to Kafka | Transactional outbox |

## Kafka storage and producing

| Term | Simple meaning | Why it matters | Example | Related terms |
|---|---|---|---|---|
| **Topic** | A named log for one kind of event | Unit of retention, access and naming | `orders` | Partition |
| **Partition** | An ordered, append-only slice of a topic | Unit of order and of parallelism | `orders` partition 3 | Key, offset |
| **Offset** | An event's position inside its partition | The consumer's bookmark | Offset 42 in partition 3 | Committed offset |
| **Key** | The value hashed to choose the partition | Keeps related events together and in order | Order ID | Hot partition |
| **Hot partition** | A partition getting far more traffic than the rest | One consumer falls behind; more partitions don't help | A huge seller's ID as key | Key sharding |
| **Acks** | Who must confirm a write: none, the leader, or all in-sync replicas | Decides durability versus latency | `acks=all` | `min.insync.replicas` |
| **Batching / linger** | Grouping events per partition; waiting briefly to fill a batch | Throughput for a little latency | `linger.ms=10` | Compression |
| **Idempotent producer** | The broker drops duplicates caused by producer retries | Safe retries, order kept | `enable.idempotence=true` | Transactions |
| **Transaction / fencing** | Writes and consumed offsets become visible together or not at all; an older producer with the same `transactional.id` is rejected | Exactly-once pipelines without "zombie" writers | `ProducerFencedException` | `read_committed` |
| **Retention** | Deleting old segments by age or size | Sets how far back replay can go | 7 days | Log compaction |
| **Log compaction** | Keeping at least the latest value per key | Topics that hold current state | Customer profiles | Tombstone |
| **Tombstone** | A record with a key and a null value | Deletes a key in a compacted topic | `cust-42 → null` | Log compaction |
| **Tiered storage** | Older segments in object storage, still readable | Long retention at lower cost | A year of orders in S3 | Retention |
| **Schema registry** | A service storing event schemas and checking compatibility | Producers and consumers evolve without breaking each other | Avro schema for `OrderPlaced` | Compatibility mode |
| **Compatibility mode** | The rule a new schema version must follow | Decides upgrade order | BACKWARD: upgrade consumers first | Data contract |

## Kafka consuming

| Term | Simple meaning | Why it matters | Example | Related terms |
|---|---|---|---|---|
| **Consumer group** | Consumers sharing a topic's partitions; one member per partition | Parallelism and independent readers | `inventory-service` group | Rebalance |
| **Consumer lag** | How far a group is behind the newest events | The consumer's health signal | Notification lag growing | Lag growth rate |
| **Committed offset** | The saved position of a group: the next event to read | Where reading resumes after a restart | Commit 43 after processing 42 | Auto-commit |
| **Auto-commit** | The client commits offsets periodically while polling | Simple, but only safe with synchronous processing | `enable.auto.commit=true` | Manual commit, ack mode |
| **Ack mode (Spring Kafka)** | When the listener container commits offsets | Framework-level control of delivery behaviour | `BATCH`, `RECORD`, `MANUAL` | Committed offset |
| **Session timeout / poll interval** | The deadline for heartbeats / for calling `poll()` again | Missing either removes the member and triggers a rebalance | 45 s / 5 minutes by default | Rebalance |
| **Rebalance** | Reassigning partitions when members change | Pauses work; can cause duplicates | A pod restart | Cooperative rebalancing |
| **Static membership** | A member with a fixed ID keeps its partitions through short absences | Restarts without rebalances | `group.instance.id` from the pod name | Session timeout |
| **Retry topic** | A separate topic for delayed retries | Keeps the main topic flowing; can reorder events | Payment retries after 1 s, 10 s, 1 min | Dead-letter topic |
| **Share group** | Consumers taking records one at a time from shared partitions, queue-style (KIP-932) | Queue semantics on Kafka; production-ready from Kafka 4.2 | Order-export jobs taken by any free worker | Consumer group |
| **`read_committed`** | Consumer mode that sees only committed transactions | Needed for exactly-once pipelines | Downstream of a transactional producer | Transactions |

## Kafka cluster and operations

| Term | Simple meaning | Why it matters | Example | Related terms |
|---|---|---|---|---|
| **Broker** | A Kafka server storing partitions | Unit of failure and capacity | Three brokers in three zones | Controller |
| **Replication factor** | How many copies each partition has | Survives broker failures | 3 | ISR |
| **Leader / follower** | The copy serving traffic / copies replicating it | Failover happens by electing a new leader | Leader on broker 1 | Leader election |
| **In-sync replicas (ISR)** | Copies fully caught up with the leader | Only they may become leader safely | 2 of 3 in sync | `min.insync.replicas` |
| **`min.insync.replicas`** | In-sync copies required for an `acks=all` write | Real durability of `acks=all` | 2 with replication factor 3 | Acks |
| **High watermark** | The offset all in-sync replicas have copied | Consumers never read past it | Offset 1000 | ISR |
| **Unclean leader election** | Letting an out-of-date replica become leader | Restores availability but can lose confirmed data; off by default | `unclean.leader.election.enable=false` | ISR |
| **Controller (KRaft)** | The Raft quorum managing metadata and electing leaders | Needs a majority to make changes | 3 controllers tolerate 1 failure | Leader election |
| **Kafka Streams** | A Java library for continuous processing of topics | Stateful processing inside services | Stock per product | KStream, KTable |
| **KStream / KTable** | A stream of independent events / the latest value per key | Two ways to read the same data | Orders / stock levels | Changelog topic |
| **Changelog topic / co-partitioning** | Compacted topic backing a Streams state store / joined topics keyed and partitioned alike | State survives restarts / joins find matching keys | `app-store-changelog` | State store |
| **Kafka Connect** | A runtime for moving data in and out of Kafka with connectors | Integration without custom code | Search-index sink | Source and sink connectors |
| **Change data capture (CDC)** | Streaming committed database changes as events | No polling, includes deletes | Debezium reading the MySQL binlog | Transactional outbox |
| **ACL** (access control list) | A rule: principal, operation, resource, allow or deny | Least privilege per service | `User:order-service` may Write `orders` | SASL, TLS |
| **Principal** | The identity Kafka checks permissions against | One per service, for traceability and least privilege | `User:order-service` | ACL |
| **SASL / TLS** | Authentication framework / encryption of traffic | Identity and confidentiality | `SASL_SSL` with SCRAM | mTLS, OAUTHBEARER |
| **MirrorMaker 2** | Kafka's tool for copying topics between clusters | Disaster recovery across regions | Primary to standby region | Offset translation |
| **Offset translation** | Mapping a group's position to the matching position in another cluster | Consumers resume near where they stopped after failover; approximate | MirrorMaker 2 checkpoints | RPO |
| **RPO / RTO** | Data you can lose / time you can be down | Drives the disaster-recovery design | Minutes / an hour | Active-passive |

## Where these are taught

[Messaging overview](01-messaging-overview.md) · [Kafka overview](02-kafka/01-kafka-overview.md) · [The log](02-kafka/02-log-topics-partitions-offsets.md) · [Producers](02-kafka/03-producers-keys-acks-batching.md) · [Consumers](02-kafka/04-consumers-consumer-groups.md) · [Offsets and delivery](02-kafka/05-offset-commits-delivery-guarantees.md) · [Replication](02-kafka/06-brokers-replication-leader-election.md) · [Rebalancing](02-kafka/07-rebalancing-static-membership.md) · [Errors and DLTs](02-kafka/08-error-handling-retries-dlt.md) · [Exactly-once](02-kafka/09-idempotent-producer-transactions.md) · [Schemas](02-kafka/10-schemas-evolution.md) · [Ordering](02-kafka/11-ordering-partition-planning-hot-partitions.md) · [Spring Boot](02-kafka/12-kafka-with-spring-boot.md) · [Retention](02-kafka/13-retention-compaction-tiered-storage.md) · [Kafka Streams](02-kafka/14-kafka-streams-basics.md) · [Connect and CDC](02-kafka/15-kafka-connect-cdc.md) · [Security](02-kafka/16-security-tls-sasl-acls.md) · [Monitoring](02-kafka/17-monitoring-lag-capacity.md) · [Disaster recovery](02-kafka/18-multi-cluster-disaster-recovery.md)

Back to [the messaging overview](01-messaging-overview.md) · [Cheat sheet](99-messaging-cheat-sheet.md) · [Index](../README.md) · [Roadmap](../roadmap.md).
