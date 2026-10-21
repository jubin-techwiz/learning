---
title: Learning Roadmap
tags: [index, roadmap, architect, engineer]
---

# Learning Roadmap: Software Engineer to Software Architect

This is the map of what to learn, **in the order to learn it**. Each topic becomes one short note in simple language. Every track and every series starts with an overview note, then goes step by step, with earlier topics being the foundation for later ones.

> [!NOTE]
> Drafted on 2026-10-19 and restructured on 2026-10-20. Tracks are numbered in the order you are learning them: resilience first, then Kafka (fundamentals to advanced), design patterns and microservices, followed by architecture foundations and the other tracks. It is a plan, not an official curriculum. Topic lists for tracks that are not started yet are proposals; reorder, add or remove freely.

## How to read the numbers

A step number is the path of the note: **track.step**, or **track.series.step** when a track has a series. So `1.5` is track 1, step 5 (`01-resilience/05-circuit-breaker.md`), and `2.2.5` is track 2 (messaging), series 2 (Kafka), step 5. Alphabetical order of files is always learning order.

**Status:** Done · Learning · Paused (started, deliberately set aside, resume point recorded) · Planned. Roles: *Engineer*, *Architect* or *Both*.

## How to use it

1. Pick the next topic from **Do next**.
2. Learn it in a short lesson, read the note, answer its questions, and try its experiment if it has one.
3. The note, its area overview and this page get updated.
4. Revise from the area's cheat sheet. Revisit *Done* notes about every 6 months, or when you use the topic at work.

## In progress

You can have several areas open at once. When you switch, the area you leave is marked **Paused** with its resume point, so nothing is lost. Details per area live in `progress/<area>.md` (outside Kenmora).

| Area | Status | Done | Resume at | Notes |
|---|---|---|---|---|
| 1 Resilience | Done | 1.1 to 1.10 | none | All ten notes are written. Revise from the cheat sheet. Hands-on demo only if you ask |
| 2 Messaging, Kafka | Done | 2.1, 2.2.1 to 2.2.18 (Kafka series complete) | Next in this area: RabbitMQ series (2.3) | Resumed on 2026-10-21 after the resilience notes were finished. Kafka error handling and the idempotent producer (2.2.8, 2.2.9) have their resilience prerequisites in place |

## Do next

| # | Topic | Step | Why now |
|---|---|---|---|
| 1 | RabbitMQ series | 2.3.1 to 2.3.14 | The queue family, to compare with Kafka |
| 2 | Choosing a messaging system | 2.4 | Completes the messaging area with a decision guide |
| 3 | Design patterns | 3.x | Next track in your order |
| 4 | Microservices | 4.x | Builds on resilience, messaging and design patterns |

## Tracks

### 1. Resilience and reliability
Why: every service you build calls something that will eventually fail. These patterns are **independent of each other** (one is not part of another), but they build on each other in this order.

| Step | Topic | Role | Status |
|---|---|---|---|
| 1.1 | [Resilience patterns: the big picture](01-resilience/01-resilience-overview.md) | Both | Done |
| 1.2 | [Timeouts](01-resilience/02-timeouts.md) | Both | Done |
| 1.3 | [Retries, backoff and jitter](01-resilience/03-retries-backoff-jitter.md) | Both | Done |
| 1.4 | [Idempotency](01-resilience/04-idempotency.md) | Both | Done |
| 1.5 | [Circuit breaker](01-resilience/05-circuit-breaker.md) | Both | Done |
| 1.6 | [Bulkhead](01-resilience/06-bulkhead.md) | Both | Done |
| 1.7 | [Rate limiting and throttling](01-resilience/07-rate-limiting.md) | Both | Done |
| 1.8 | [Load shedding and backpressure](01-resilience/08-load-shedding-backpressure.md) | Architect | Done |
| 1.9 | [Fallbacks and graceful degradation](01-resilience/09-fallbacks-graceful-degradation.md) | Both | Done |
| 1.10 | [Testing resilience (chaos engineering)](01-resilience/10-testing-resilience.md) | Both | Done |
| 1.99 | [Resilience cheat sheet (revision)](01-resilience/99-resilience-cheat-sheet.md) | Both | Done |

> [!NOTE]
> The whole track is written. Steps 1.5 and 1.6 were written before 1.2 to 1.4, so for the cleanest understanding read the notes in numbered order. The notes are self-contained; hands-on demos are optional and kept outside these notes. Notes 1.1 to 1.3 use the current note format; 1.4 to 1.10 are being migrated.

### 2. Messaging and event-driven design
Why: events decouple teams and services, but they bring ordering, duplicate and consistency problems.

| Step | Topic | Role | Status |
|---|---|---|---|
| 2.1 | [Messaging and event-driven design: the big picture](02-messaging/01-messaging-overview.md) | Architect | Done |

**Series 2.2, Apache Kafka (fundamentals to advanced).** Written as a series: `02-messaging/02-kafka/`.

| Step | Topic | Level | Role | Status |
|---|---|---|---|---|
| 2.2.1 | [Kafka: the big picture and learning order](02-messaging/02-kafka/01-kafka-overview.md) | Fundamentals | Both | Done |
| 2.2.2 | [The log: topics, partitions and offsets](02-messaging/02-kafka/02-log-topics-partitions-offsets.md) | Fundamentals | Engineer | Done |
| 2.2.3 | [Producers: keys, partitioning, acks and batching](02-messaging/02-kafka/03-producers-keys-acks-batching.md) | Fundamentals | Engineer | Done |
| 2.2.4 | [Consumers and consumer groups](02-messaging/02-kafka/04-consumers-consumer-groups.md) | Fundamentals | Both | Done |
| 2.2.5 | [Offset commits and delivery guarantees](02-messaging/02-kafka/05-offset-commits-delivery-guarantees.md) | Fundamentals | Both | Done |
| 2.2.6 | [Brokers, replication and leader election](02-messaging/02-kafka/06-brokers-replication-leader-election.md) | Fundamentals | Both | Done |
| 2.2.7 | [Rebalancing and static membership](02-messaging/02-kafka/07-rebalancing-static-membership.md) | Intermediate | Both | Done |
| 2.2.8 | [Error handling, retries and dead-letter topics](02-messaging/02-kafka/08-error-handling-retries-dlt.md) | Intermediate | Engineer | Done |
| 2.2.9 | [Idempotent producer and transactions (exactly-once)](02-messaging/02-kafka/09-idempotent-producer-transactions.md) | Intermediate | Both | Done |
| 2.2.10 | [Schemas and evolution (Avro, Schema Registry)](02-messaging/02-kafka/10-schemas-evolution.md) | Intermediate | Architect | Done |
| 2.2.11 | [Ordering, partition planning and hot partitions](02-messaging/02-kafka/11-ordering-partition-planning-hot-partitions.md) | Intermediate | Architect | Done |
| 2.2.12 | [Kafka with Spring Boot](02-messaging/02-kafka/12-kafka-with-spring-boot.md) | Intermediate | Engineer | Done |
| 2.2.13 | [Retention, log compaction and tiered storage](02-messaging/02-kafka/13-retention-compaction-tiered-storage.md) | Advanced | Architect | Done |
| 2.2.14 | [Stream processing basics (Kafka Streams)](02-messaging/02-kafka/14-kafka-streams-basics.md) | Advanced | Both | Done |
| 2.2.15 | [Kafka Connect and change data capture](02-messaging/02-kafka/15-kafka-connect-cdc.md) | Advanced | Both | Done |
| 2.2.16 | [Security: TLS, SASL and ACLs](02-messaging/02-kafka/16-security-tls-sasl-acls.md) | Advanced | Both | Done |
| 2.2.17 | [Monitoring, consumer lag and capacity planning](02-messaging/02-kafka/17-monitoring-lag-capacity.md) | Advanced | Both | Done |
| 2.2.18 | [Multi-cluster and disaster recovery](02-messaging/02-kafka/18-multi-cluster-disaster-recovery.md) | Advanced | Architect | Done |

**Series 2.3, RabbitMQ (fundamentals to advanced).** A second series in the same area: `02-messaging/03-rabbitmq/`. Kafka is a log you read from; RabbitMQ is a broker that routes messages into queues. Learning both, and then comparing them, is what makes the choice an architect's decision.

| Step | Topic | Level | Role | Status |
|---|---|---|---|---|
| 2.3.1 | RabbitMQ: the big picture and how it differs from Kafka | Fundamentals | Both | Planned |
| 2.3.2 | The AMQP model: producers, exchanges, queues and bindings | Fundamentals | Both | Planned |
| 2.3.3 | Exchange types and routing (direct, topic, fanout, headers) | Fundamentals | Engineer | Planned |
| 2.3.4 | Consumers, acknowledgements and prefetch | Fundamentals | Engineer | Planned |
| 2.3.5 | Durability: durable queues, persistent messages, publisher confirms | Fundamentals | Both | Planned |
| 2.3.6 | Dead-letter exchanges, message TTL and retry patterns | Intermediate | Both | Planned |
| 2.3.7 | Messaging patterns: work queues, publish/subscribe, request/reply | Intermediate | Both | Planned |
| 2.3.8 | Quorum queues and high availability | Intermediate | Architect | Planned |
| 2.3.9 | Ordering, redelivery and idempotent consumers | Intermediate | Both | Planned |
| 2.3.10 | RabbitMQ with Spring Boot (Spring AMQP) | Intermediate | Engineer | Planned |
| 2.3.11 | Streams, and when to use them instead of queues | Advanced | Architect | Planned |
| 2.3.12 | Clustering, federation and shovel | Advanced | Architect | Planned |
| 2.3.13 | Security and multi-tenancy (virtual hosts, permissions) | Advanced | Both | Planned |
| 2.3.14 | Monitoring, flow control and capacity planning | Advanced | Both | Planned |

The topic list is a proposal; refine it, and check it against the current RabbitMQ version, when the series starts.

**After the series, a comparison note and a revision sheet:**

| Step | Topic | Role | Status |
|---|---|---|---|
| 2.4 | Choosing a messaging system: Kafka, RabbitMQ and cloud queues | Architect | Planned |
| 2.98 | [Messaging glossary (canonical terms)](02-messaging/98-messaging-glossary.md) | Both | Growing |
| 2.99 | [Messaging cheat sheet (revision)](02-messaging/99-messaging-cheat-sheet.md) | Both | Growing |

### 3. Design patterns
Why: a shared vocabulary of proven solutions makes designs faster to explain and review.

| Step | Topic | Role | Status |
|---|---|---|---|
| 3.1 | Design patterns: the big picture | Both | Planned |

**Series 3.2, GoF object-oriented patterns:** creational (factory, builder, singleton), structural (adapter, decorator, facade, proxy), behavioural (strategy, observer, template method, chain of responsibility, command, state).
**Series 3.3, enterprise and application patterns:** layering, repository, unit of work, DTO, service layer, dependency injection, hexagonal architecture.

Detailed step lists are created when this track starts.

### 4. Microservices
Why: microservices solve organisational and scaling problems and create distributed-systems problems; an architect needs both sides.

| Step | Topic | Role | Status |
|---|---|---|---|
| 4.1 | Microservices: the big picture | Architect | Planned |
| 4.2 | When to use them, and when not (modular monolith) | Architect | Planned |
| 4.3 | Decomposition and bounded contexts | Architect | Planned |
| 4.4 | Service communication: synchronous and asynchronous | Both | Planned |
| 4.5 | API gateway and BFF | Architect | Planned |
| 4.6 | Service discovery and configuration | Both | Planned |
| 4.7 | Data per service and consistency | Architect | Planned |
| 4.8 | Saga and the outbox pattern | Both | Planned |
| 4.9 | CQRS and event sourcing | Architect | Planned |
| 4.10 | Resilience between services (links to track 1) | Both | Planned |
| 4.11 | Testing microservices | Both | Planned |
| 4.12 | Versioning, deployment and ownership | Architect | Planned |

### 5. Architecture foundations
Why: an architect's main job is choosing between trade-offs and explaining the choice.

| Step | Topic | Role | Status |
|---|---|---|---|
| 5.1 | Architecture foundations: the big picture | Architect | Planned |
| 5.2 | Quality attributes and non-functional requirements | Architect | Planned |
| 5.3 | Architecture styles: monolith, modular monolith, microservices, event-driven | Architect | Planned |
| 5.4 | Trade-off analysis | Architect | Planned |
| 5.5 | C4 diagrams and clear architecture drawings | Both | Planned |
| 5.6 | Architecture Decision Records (ADRs) | Architect | Planned |

### 6. Data and distributed systems
Why: most production incidents are really data consistency problems.

| Step | Topic | Role | Status |
|---|---|---|---|
| 6.1 | Data and distributed systems: the big picture | Architect | Planned |
| 6.2 | Consistency models and the CAP idea in practice | Architect | Planned |
| 6.3 | MySQL indexing, isolation levels and locking | Engineer | Planned |
| 6.4 | Caching strategies and invalidation | Both | Planned |
| 6.5 | Replication, partitioning and sharding | Architect | Planned |

### 7. APIs and integration
Why: APIs are the contracts between teams, and they are expensive to change.

| Step | Topic | Role | Status |
|---|---|---|---|
| 7.1 | APIs and integration: the big picture | Both | Planned |
| 7.2 | REST design, errors and idempotency keys | Both | Planned |
| 7.3 | OpenAPI and contract testing | Engineer | Planned |
| 7.4 | API versioning and deprecation | Architect | Planned |
| 7.5 | gRPC and when to prefer it | Both | Planned |

### 8. Security
Why: security decisions are hard to retrofit.

| Step | Topic | Role | Status |
|---|---|---|---|
| 8.1 | Security: the big picture | Both | Planned |
| 8.2 | OAuth2 and OIDC authorization code flow with PKCE | Both | Planned |
| 8.3 | JWT validation and token handling | Engineer | Planned |
| 8.4 | Keycloak and multi-tenant design | Architect | Planned |
| 8.5 | Secrets management and the OWASP Top 10 | Both | Planned |

### 9. Cloud and containers
Why: where and how your services run decides cost, scale and recovery time.

| Step | Topic | Role | Status |
|---|---|---|---|
| 9.1 | Cloud and containers: the big picture | Both | Planned |
| 9.2 | Docker image basics and good practice | Engineer | Planned |
| 9.3 | Kubernetes core: pods, services, deployments | Both | Planned |
| 9.4 | Kubernetes probes and rolling updates | Both | Planned |
| 9.5 | Autoscaling (HPA) and resource limits | Both | Planned |
| 9.6 | AWS core services for architects | Architect | Planned |
| 9.7 | Infrastructure as code (Terraform) | Both | Planned |
| 9.8 | Cloud cost awareness | Architect | Planned |

### 10. Observability and operations
Why: you can't fix what you can't see.

| Step | Topic | Role | Status |
|---|---|---|---|
| 10.1 | Observability: the big picture | Both | Planned |
| 10.2 | Metrics, logs and traces: how they differ | Both | Planned |
| 10.3 | OpenTelemetry basics | Engineer | Planned |
| 10.4 | Prometheus and Grafana dashboards | Engineer | Planned |
| 10.5 | SLIs, SLOs and error budgets | Architect | Planned |
| 10.6 | Incident response and post-mortems | Both | Planned |

### 11. Delivery and quality
Why: a good design that ships badly is still a bad outcome.

| Step | Topic | Role | Status |
|---|---|---|---|
| 11.1 | Delivery and quality: the big picture | Both | Planned |
| 11.2 | Testing strategy: unit, integration, contract | Both | Planned |
| 11.3 | CI/CD pipelines | Engineer | Planned |
| 11.4 | Blue-green, canary and feature flags | Both | Planned |
| 11.5 | DORA metrics | Architect | Planned |

### 12. Architect skills
Why: much of the architect role is communication and judgement, not code.

| Step | Topic | Role | Status |
|---|---|---|---|
| 12.1 | Architect skills: the big picture | Architect | Planned |
| 12.2 | Running a design review | Architect | Planned |
| 12.3 | Build versus buy decisions | Architect | Planned |
| 12.4 | Managing technical debt | Both | Planned |
| 12.5 | Presenting trade-offs to non-technical people | Architect | Planned |
| 12.6 | System design interview practice | Both | Planned |

Back to [the index](README.md).
