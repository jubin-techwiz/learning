---
title: Learning Roadmap
tags: [index, roadmap, architect, engineer]
---

# Learning Roadmap: Software Engineer to Software Architect

This is the map of what to learn, **in the order to learn it**. Each topic becomes one short note in simple language. Every track and every series starts with an overview note, then goes step by step, with earlier topics being the foundation for later ones.

> [!NOTE]
> Drafted on 2026-10-19 and restructured on 2026-10-20. Tracks are numbered in the order you are learning them: resilience first, then Kafka (fundamentals to advanced), design patterns and microservices, followed by architecture foundations and the other tracks. System design was added on 2026-10-22 as track 14, at your request, and completed the same day (14.1 to 14.20); the Java Streams series is paused at 13.2.5. It is a plan, not an official curriculum. Topic lists for tracks that are not started yet are proposals; reorder, add or remove freely.

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
| 1 Resilience | Done | 1.1 to 1.10 | none | All ten notes follow the current note format (migrated and reviewed 2026-10-21). Revise from the cheat sheet. Hands-on demo only if you ask |
| 2 Messaging, Kafka | Done | 2.1, 2.2.1 to 2.2.18 (Kafka series complete) | Next in this area: RabbitMQ series (2.3) | Resumed on 2026-10-21 after the resilience notes were finished. Kafka error handling and the idempotent producer (2.2.8, 2.2.9) have their resilience prerequisites in place |
| 13 Java, Streams | Paused | 13.1, 13.2.1 to 13.2.4 | 13.2.5 Collectors | Added on 2026-10-21 at your request. Numbered 13 so existing note paths don't change; the order of learning is set by "Do next", not by the number. Paused on 2026-10-22 when you chose system design; nothing is missing to resume |
| 9 Cloud and containers, Docker and Kubernetes | Learning | 9.1, 9.2.1 to 9.2.9, 9.3.1 to 9.3.10 | 9.4 AWS core services for architects | Added on 2026-10-22 at your request (Docker, then Kubernetes). Builds on resilience (1.x) and system design (14.x). Hands-on on your Mac: Docker Desktop for the Docker series, the Rancher Desktop cluster for Kubernetes |
| 4 Microservices, Saga, CQRS | Learning | 4.1, 4.8.1 to 4.8.8, 4.8.99, 4.9.1 to 4.9.7, 4.9.99 | Saga and CQRS series complete; next in this area: 4.2 to 4.7, 4.10 to 4.12 | Added on 2026-10-23 at your request (saga first, then CQRS and event sourcing). The rest of the track (4.2 to 4.7, 4.10 to 4.12) follows later. Builds on resilience (1.x), messaging (2.x) and system design (14.x) |
| 14 System design | Done | 14.1 to 14.20 (area complete) | none | Added on 2026-10-22 at your request, numbered 14 for the same reason. Builds on resilience (1.x) and messaging (2.x), which are done |

## Do next

| # | Topic | Step | Why now |
|---|---|---|---|
| 1 | AWS core services for architects | 9.4 | Docker and Kubernetes are complete; the cloud services they run on come next (hands-on needs an AWS account, so confirm how to proceed first) |
| 2 | Java Stream API series | 13.2.5 onward | Paused when you chose system design; 13.2.1 to 13.2.4 are written, hands-on, nothing missing to resume |
| 3 | System design interview practice | 12.6 | Practise the method of 14.2 on new problems now that the system design area is complete |
| 4 | RabbitMQ series | 2.3.1 to 2.3.14 | The queue family, to compare with Kafka |
| 5 | Choosing a messaging system | 2.4 | Completes the messaging area with a decision guide |
| 6 | Design patterns | 3.x | Next track in your order |
| 7 | Microservices (rest of the track) | 4.2 to 4.7, 4.9 to 4.12 | Builds on resilience, messaging, design patterns and system design |

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
> The whole track is written. Steps 1.5 and 1.6 were written before 1.2 to 1.4, so for the cleanest understanding read the notes in numbered order. The notes are self-contained; hands-on demos are optional and kept outside these notes. All ten notes and the cheat sheet use the current note format. Code samples are untested sketches unless a note says it was run; the results quoted in 1.5, 1.6 and 1.8 come from example runs described in those notes.

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
| 4.1 | [Microservices: the big picture](04-microservices/01-microservices-overview.md) | Architect | Done |
| 4.2 | When to use them, and when not (modular monolith) | Architect | Planned |
| 4.3 | Decomposition and bounded contexts | Architect | Planned |
| 4.4 | Service communication: synchronous and asynchronous | Both | Planned |
| 4.5 | API gateway and BFF | Architect | Planned |
| 4.6 | Service discovery and configuration | Both | Planned |
| 4.7 | Data per service and consistency | Architect | Planned |
| 4.8 | Saga and the outbox pattern (series below) | Both | Done |
| 4.9 | CQRS and event sourcing (series below) | Architect | Done |
| 4.10 | Resilience between services (links to track 1) | Both | Planned |
| 4.11 | Testing microservices | Both | Planned |
| 4.12 | Versioning, deployment and ownership | Architect | Planned |

**Series 4.8, Saga (fundamentals to advanced).** Written as a series: `04-microservices/08-saga/`. Hands-on: runnable Java 21 simulations with injected failures; the outbox lesson also uses a real PostgreSQL.

| Step | Topic | Level | Role | Status |
|---|---|---|---|---|
| 4.8.1 | [Saga: the big picture and learning order](04-microservices/08-saga/01-saga-overview.md) | Fundamentals | Both | Done |
| 4.8.2 | [Steps and compensating transactions](04-microservices/08-saga/02-saga-steps-and-compensation.md) | Fundamentals | Both | Done |
| 4.8.3 | [Choreography: coordinating with events](04-microservices/08-saga/03-choreography.md) | Intermediate | Both | Done |
| 4.8.4 | [Orchestration: a coordinator with state](04-microservices/08-saga/04-orchestration.md) | Intermediate | Both | Done |
| 4.8.5 | [Reliable messaging for sagas: outbox, inbox, idempotent consumers](04-microservices/08-saga/05-reliable-messaging-outbox-inbox.md) | Intermediate | Both | Done |
| 4.8.6 | [Isolation anomalies and countermeasures](04-microservices/08-saga/06-isolation-and-countermeasures.md) | Advanced | Both | Done |
| 4.8.7 | [Failures, timeouts and operating sagas](04-microservices/08-saga/07-failures-timeouts-operations.md) | Advanced | Both | Done |
| 4.8.8 | [Case study: the ShopFlow checkout saga](04-microservices/08-saga/08-case-checkout-saga.md) | Advanced | Both | Done |
| 4.8.99 | [Saga cheat sheet (revision)](04-microservices/08-saga/99-saga-cheat-sheet.md) | | Both | Done |

**Series 4.9, CQRS and event sourcing (fundamentals to advanced).** Written as a series: `04-microservices/09-cqrs-event-sourcing/`. Hands-on: runnable Java 21 programs; the event store lesson also uses a real PostgreSQL.

| Step | Topic | Level | Role | Status |
|---|---|---|---|---|
| 4.9.1 | [CQRS and event sourcing: the big picture and learning order](04-microservices/09-cqrs-event-sourcing/01-cqrs-es-overview.md) | Fundamentals | Both | Done |
| 4.9.2 | [CQRS: separate models for writing and reading](04-microservices/09-cqrs-event-sourcing/02-cqrs-separate-models.md) | Fundamentals | Both | Done |
| 4.9.3 | [Event sourcing: state as a sequence of events](04-microservices/09-cqrs-event-sourcing/03-event-sourcing.md) | Intermediate | Both | Done |
| 4.9.4 | [Projections and read models](04-microservices/09-cqrs-event-sourcing/04-projections-read-models.md) | Intermediate | Both | Done |
| 4.9.5 | [Snapshots, event versioning and privacy](04-microservices/09-cqrs-event-sourcing/05-snapshots-versioning-privacy.md) | Advanced | Both | Done |
| 4.9.6 | [An event store in a relational database, and integration](04-microservices/09-cqrs-event-sourcing/06-event-store-postgresql-integration.md) | Advanced | Both | Done |
| 4.9.7 | [Case study: the ShopFlow order history](04-microservices/09-cqrs-event-sourcing/07-case-order-history.md) | Advanced | Architect | Done |
| 4.9.99 | [CQRS and event sourcing cheat sheet (revision)](04-microservices/09-cqrs-event-sourcing/99-cqrs-es-cheat-sheet.md) | | Both | Done |

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

> [!NOTE]
> 6.2, 6.4 and 6.5 overlap the system design notes 14.10, 14.6, 14.8 and 14.9, which are taught first, at design level. When this track starts, its notes go deeper (database internals) and link to track 14 instead of repeating it.

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
Why: where and how your services run decides cost, scale and recovery time. The track starts with Docker (a service packaged as an image and run as a container), then Kubernetes (running many containers reliably across machines), then the cloud around them.

| Step | Topic | Role | Status |
|---|---|---|---|
| 9.1 | [Cloud and containers: the big picture](09-cloud-and-containers/01-cloud-and-containers-overview.md) | Both | Done |

**Series 9.2, Docker (fundamentals to practice).** Written as a series: `09-cloud-and-containers/02-docker/`. Hands-on: every lesson uses real commands and their output.

| Step | Topic | Level | Role | Status |
|---|---|---|---|---|
| 9.2.1 | [Docker: the big picture and learning order](09-cloud-and-containers/02-docker/01-docker-overview.md) | Fundamentals | Both | Done |
| 9.2.2 | [Containers: isolated processes with limits](09-cloud-and-containers/02-docker/02-containers-isolated-processes.md) | Fundamentals | Both | Done |
| 9.2.3 | [Images and layers](09-cloud-and-containers/02-docker/03-images-and-layers.md) | Fundamentals | Engineer | Done |
| 9.2.4 | [A Dockerfile for a Java service](09-cloud-and-containers/02-docker/04-dockerfile-java-service.md) | Fundamentals | Engineer | Done |
| 9.2.5 | [Networking and ports](09-cloud-and-containers/02-docker/05-networking-and-ports.md) | Intermediate | Engineer | Done |
| 9.2.6 | [Data and configuration: volumes and environment](09-cloud-and-containers/02-docker/06-data-and-configuration.md) | Intermediate | Engineer | Done |
| 9.2.7 | [Lifecycle: signals, graceful shutdown, health checks and restarts](09-cloud-and-containers/02-docker/07-lifecycle.md) | Intermediate | Both | Done |
| 9.2.8 | [Docker Compose for local development](09-cloud-and-containers/02-docker/08-docker-compose.md) | Intermediate | Engineer | Done |
| 9.2.9 | [Container security and supply chain basics](09-cloud-and-containers/02-docker/09-container-security.md) | Advanced | Both | Done |
| 9.2.99 | [Docker cheat sheet (revision)](09-cloud-and-containers/02-docker/99-docker-cheat-sheet.md) | | Both | Done |

**Series 9.3, Kubernetes (fundamentals to advanced).** Written as a series: `09-cloud-and-containers/03-kubernetes/`.

| Step | Topic | Level | Role | Status |
|---|---|---|---|---|
| 9.3.1 | [Kubernetes: the big picture and learning order](09-cloud-and-containers/03-kubernetes/01-kubernetes-overview.md) | Fundamentals | Both | Done |
| 9.3.2 | [Pods, ReplicaSets and Deployments](09-cloud-and-containers/03-kubernetes/02-pods-replicasets-deployments.md) | Fundamentals | Both | Done |
| 9.3.3 | [Services, DNS and Ingress](09-cloud-and-containers/03-kubernetes/03-services-dns-ingress.md) | Fundamentals | Both | Done |
| 9.3.4 | [Configuration and secrets](09-cloud-and-containers/03-kubernetes/04-configuration-and-secrets.md) | Intermediate | Engineer | Done |
| 9.3.5 | [Probes, rolling updates and graceful shutdown](09-cloud-and-containers/03-kubernetes/05-probes-rolling-updates-graceful-shutdown.md) | Intermediate | Both | Done |
| 9.3.6 | [Resources, scheduling and autoscaling](09-cloud-and-containers/03-kubernetes/06-resources-scheduling-autoscaling.md) | Intermediate | Both | Done |
| 9.3.7 | [Storage and StatefulSets](09-cloud-and-containers/03-kubernetes/07-storage-statefulsets.md) | Advanced | Both | Done |
| 9.3.8 | [Packaging and delivery: Kustomize and Helm](09-cloud-and-containers/03-kubernetes/08-kustomize-and-helm.md) | Advanced | Engineer | Done |
| 9.3.9 | [Security: RBAC, service accounts and Pod security](09-cloud-and-containers/03-kubernetes/09-kubernetes-security.md) | Advanced | Both | Done |
| 9.3.10 | [Troubleshooting and day-two operations](09-cloud-and-containers/03-kubernetes/10-troubleshooting-day-two.md) | Advanced | Both | Done |
| 9.3.99 | [Kubernetes cheat sheet (revision)](09-cloud-and-containers/03-kubernetes/99-kubernetes-cheat-sheet.md) | | Both | Done |

**After the two series, the cloud around them:**

| Step | Topic | Role | Status |
|---|---|---|---|
| 9.4 | AWS core services for architects | Architect | Planned |
| 9.5 | Infrastructure as code (Terraform) | Both | Planned |
| 9.6 | Cloud cost awareness | Architect | Planned |

The old placeholder steps 9.2 to 9.8 were reorganised into these series on 2026-10-22 (nothing had been written under them). The topic lists are proposals; the Kubernetes series is refined when it starts.

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

12.6 will be a practice plan that uses the method of [14.2](14-system-design/02-requirements-design-method.md) and the case studies 14.15 to 14.20, rather than a separate set of designs.

### 13. Java language and platform
Why: every ShopFlow service is written in Java, and architecture decisions about readability, parallelism and performance depend on what the language and the JVM really guarantee. This track was added after the original twelve, so it is numbered 13 to keep existing paths stable.

| Step | Topic | Role | Status |
|---|---|---|---|
| 13.1 | [Java language and platform: the big picture](13-java/01-java-overview.md) | Both | Done |

**Series 13.2, Java Stream API (fundamentals to advanced).** Written as a series: `13-java/02-streams/`.

| Step | Topic | Level | Role | Status |
|---|---|---|---|---|
| 13.2.1 | [Java Stream API: the big picture](13-java/02-streams/01-streams-overview.md) | Fundamentals | Both | Done |
| 13.2.2 | [The pipeline: source, operations and laziness](13-java/02-streams/02-pipeline-laziness.md) | Fundamentals | Engineer | Done |
| 13.2.3 | [Transforming and filtering: `map`, `filter`, `flatMap`, `sorted`, `limit`](13-java/02-streams/03-transforming-filtering.md) | Fundamentals | Engineer | Done |
| 13.2.4 | [Terminal operations and `Optional`](13-java/02-streams/04-terminal-operations-optional.md) | Fundamentals | Engineer | Done |
| 13.2.5 | Collectors: `groupingBy`, `toMap`, `partitioningBy`, `teeing` | Intermediate | Engineer | Planned |
| 13.2.6 | Primitive streams and boxing | Intermediate | Engineer | Planned |
| 13.2.7 | Lambdas in pipelines: statelessness, side effects, exceptions | Intermediate | Both | Planned |
| 13.2.8 | Parallel streams: when they help and when they hurt | Advanced | Architect | Planned |
| 13.2.9 | Streams versus loops in production code | Advanced | Both | Planned |
| 13.2.10 | Custom stages: Stream Gatherers | Advanced | Both | Planned |

**Planned moves** (made when the destination lesson is written; 13.2.2 then keeps a short summary and a link):

| Destination | Material to move from 13.2.2 |
|---|---|
| 13.2.4 Terminal operations | `Stream.toList()` versus `Collectors.toList()` and list mutability (**done** 2026-10-22) |
| 13.2.7 Lambdas in pipelines | Side effects, `peek()`, shared mutable state and interference |
| 13.2.8 Parallel streams | The detailed parallel execution trace and performance considerations |

Other Java topics (records and pattern matching, concurrency and virtual threads, memory and the JVM) are proposals, to be listed when they start.

### 14. System design
Why: turning requirements into components, data and interactions that hold up at the expected scale, and defending the trade-offs, is the core of the architect's job and of senior interviews. Added on 2026-10-22 and numbered 14 to keep existing paths stable. Written as a flat area: `14-system-design/`. **Complete (14.1 to 14.20, 2026-10-22).**

| Step | Topic | Level | Role | Status |
|---|---|---|---|---|
| 14.1 | [System design: the big picture](14-system-design/01-system-design-overview.md) | Fundamentals | Both | Done |
| 14.2 | [Requirements and the design method](14-system-design/02-requirements-design-method.md) | Fundamentals | Both | Done |
| 14.3 | [Back-of-the-envelope estimation](14-system-design/03-back-of-the-envelope-estimation.md) | Fundamentals | Both | Done |
| 14.4 | [Scaling out: stateless services and horizontal scaling](14-system-design/04-scaling-out-stateless-services.md) | Fundamentals | Both | Done |
| 14.5 | [Load balancing](14-system-design/05-load-balancing.md) | Fundamentals | Both | Done |
| 14.6 | [Caching](14-system-design/06-caching.md) | Fundamentals | Both | Done |
| 14.7 | [Choosing a database](14-system-design/07-choosing-a-database.md) | Fundamentals | Architect | Done |
| 14.8 | [Replication and read replicas](14-system-design/08-replication-read-replicas.md) | Fundamentals | Both | Done |
| 14.9 | [Partitioning and sharding](14-system-design/09-partitioning-sharding.md) | Fundamentals | Architect | Done |
| 14.10 | [Consistency trade-offs: CAP, PACELC and quorums](14-system-design/10-consistency-cap-pacelc-quorums.md) | Intermediate | Architect | Done |
| 14.11 | [Asynchronous processing: queues, workers and fan-out](14-system-design/11-asynchronous-processing.md) | Intermediate | Both | Done |
| 14.12 | [Object storage and content delivery networks](14-system-design/12-object-storage-cdn.md) | Intermediate | Both | Done |
| 14.13 | [Unique ID generation](14-system-design/13-unique-id-generation.md) | Intermediate | Both | Done |
| 14.14 | [Multi-region design and disaster recovery](14-system-design/14-multi-region-disaster-recovery.md) | Advanced | Architect | Done |
| 14.15 | [Case study: URL shortener](14-system-design/15-case-url-shortener.md) | Advanced | Both | Done |
| 14.16 | [Case study: distributed rate limiter](14-system-design/16-case-distributed-rate-limiter.md) | Advanced | Both | Done |
| 14.17 | [Case study: notification system](14-system-design/17-case-notification-system.md) | Advanced | Both | Done |
| 14.18 | [Case study: news feed](14-system-design/18-case-news-feed.md) | Advanced | Architect | Done |
| 14.19 | [Case study: chat system](14-system-design/19-case-chat-system.md) | Advanced | Architect | Done |
| 14.20 | [Case study: ShopFlow flash sale](14-system-design/20-case-shopflow-flash-sale.md) | Advanced | Architect | Done |
| 14.99 | [System design cheat sheet (revision)](14-system-design/99-system-design-cheat-sheet.md) | | Both | Done |

Overlaps with other tracks (5.2, 6.2, 6.4, 6.5 and 12.6) are listed in [the overview](14-system-design/01-system-design-overview.md), section 5.

Back to [the index](README.md).
