---
title: System Design Cheat Sheet
tags: [system-design, cheat-sheet, revision]
category: system-design
level: fundamentals
updated: "2026-10-22"
---

# System Design Cheat Sheet

**System design · Revision** · Last updated 2026-10-22

One page to revise the system design area. It covers the whole area, [14.1](01-system-design-overview.md) to [14.20](20-case-shopflow-flash-sale.md).

> [!TIP]
> **The whole area in one line:** requirements and numbers first, boxes second; every component needs a requirement or a number behind it, and a stated cost.

## The method: ask, count, contract, sketch, dig, break

| Step | What you do | Output |
|---|---|---|
| 1 Requirements | Functional, non-functional with numbers, constraints, assumptions, out of scope; rank the NFRs | Two or three NFRs that drive the design |
| 2 Estimate | Peak QPS, read:write ratio, storage, bandwidth, availability chain | Which number is close to a limit |
| 3 API and data model | Endpoints or events; data model from the access patterns | The contract and the keys |
| 4 High-level design | Simplest components that work; walk one request through | One diagram |
| 5 Deep dive | The one to three riskiest parts | Options with costs, and a choice |
| 6 Failures and trade-offs | Fail each dependency in turn; name what you gave up; the next step at 10x | A failure table and the trade-offs |

When a number breaks a requirement, go back to step 1 and renegotiate it with the product owner.

## NFR checklist

| Quality | Ask | Write it as |
|---|---|---|
| Scale | How many users, items, requests; how spiky? | "2,500 reads a second at peak" |
| Latency | Which operation, at which percentile? | "p99 under 300 ms" |
| Availability | What may be down, for how long? | "99.9% of requests succeed each month" |
| Consistency | May a reader see old data? What then? | "New orders visible within 5 seconds" |
| Durability | May data be lost? | "No confirmed order is lost" |
| Security and privacy | Who may see or do what? | "Customers see only their own orders" |
| Cost | Any budget? | "Within the existing email contract" |

## Numbers and formulas

| Item | Rule |
|---|---|
| Seconds in a day | 86,400, about 10^5 |
| Average QPS | Daily total ÷ 86,400 |
| Peak QPS | Average × peak factor (from earlier peaks) |
| Storage | Count × size × retention × copies (plus indexes and backups) |
| Bandwidth in bits | Bytes per second × 8 |
| Units | Decimal: KB 10^3, MB 10^6, GB 10^9, TB 10^12 |
| Latency ratios (Norvig, approximate) | Memory about 100 ns; disk seek about 8 ms; US to Europe and back about 150 ms |

## Availability

| Target | Downtime a year | Downtime per 30 days |
|---|---|---|
| 99% | 3.65 days | 7.2 hours |
| 99.9% | 8.76 hours | 43.2 minutes |
| 99.95% | 4.38 hours | 21.6 minutes |
| 99.99% | 52.56 minutes | 4.32 minutes |

- **In series** (all needed): multiply. Five components at 99.9 to 99.95% give about 99.70%, about 26 hours a year.
- **In parallel** (one is enough): 1 − product of the unavailabilities, **only if failures are independent**.
- **Error budget** = requests × (1 − SLO): 2.5 million a day at 99.99% allows 250 failures.
- **Biggest lever:** take calls out of the synchronous path (queue them, or give them a fallback).
- **SLI** measures, **SLO** is the internal target, **SLA** is the contract with consequences; set the SLA more conservatively than the SLO.

## Scaling out (14.4)

| Rule | Why |
|---|---|
| Make request-serving services **stateless** | Any instance can serve any request; instances can be added, removed, redeployed |
| Hunt hidden state: sessions, local caches, local files, scheduled jobs, in-memory counters, `synchronized` | Each breaks or multiplies with several instances |
| Change shared state **atomically**: `UPDATE ... WHERE quantity > 0`, check rows affected | Read-then-write oversells even with a shared database |
| Instances = peak ÷ (measured capacity × headroom), then size per zone to survive losing one | Redundancy, not load, often sets the count |
| Max instances × pool size ≤ database connections (MySQL 8.4 default `max_connections` 151; HikariCP default pool 10) | Scaling out can overload what instances share |
| Sticky sessions only as a bridge, or for connections that are stateful by nature | They hide state, they don't move it |
| Consumers in a Kafka group ≤ partitions | Extra consumers sit idle |

## Load balancing (14.5)

| Rule | Why |
|---|---|
| L7 for HTTP and gRPC (per-request, routing by path or host); L4 for other TCP/UDP | L4 picks a target once per connection, so long-lived HTTP/2 and gRPC connections pile onto one pod |
| Round robin when instances and requests are alike; least outstanding requests when they differ | Round robin keeps feeding a slow instance its full share |
| Readiness checks test **this instance only**, never shared dependencies | Otherwise one database blip removes every instance at once |
| Know your health-check timing: ALB defaults are 30 s interval, 5 s timeout, 2 failures, so up to about 65 s on a hung instance | Every second is requests sent to a dead target |
| Deploys: **stop sending, then stop accepting** (pause longer than the removal delay, then graceful shutdown) | Graceful shutdown alone still rejects requests that arrive during the removal delay |
| Little's law: in progress = rate × time (154/s × 0.2 s = 31) | Sizes threads and connections, and shows what a slow dependency does to a 200-thread pool |

## Caching (14.6)

| Rule | Why |
|---|---|
| Cache-aside: read cache; on a miss load and store with a TTL; on a write update the source, then **delete** the key | Delete is order-independent and safe to repeat; the TTL limits damage when a delete is lost |
| Database load = reads × **miss** ratio (90% to 99% hits = ten times less) | Size the database for the day the cache is empty too |
| Stampede: coalesce per key (`@Cacheable(sync = true)` is per instance), jitter TTLs, refresh ahead | Duplicates = request rate × reload time |
| Stale set race: a reader stores an old value after the writer's delete; leases, or a TTL as safety net | Delete on write alone doesn't close the race |
| Never decide money or stock from a cache | Read the source of truth at the moment of decision |
| Redis defaults: no memory limit, `maxmemory-policy noeviction`; a cache needs a limit and `allkeys-lru` | A full `noeviction` cache rejects new data |
| Sessions are not a cache | Keep them where nothing evicts them |

## Choosing a database (14.7)

| Rule | Why |
|---|---|
| Order of questions: access patterns (with frequency), consistency, shape, numbers, operations | The data model is the hardest decision to undo |
| Relational is the default for core business data | Transactions across rows and queries you can't predict |
| Index = sorted by the query: equality columns first, then the sort or range column | `(customer_id, created_at)` serves "a customer's newest orders", not "yesterday's orders" |
| Copy frozen facts (price and name paid); reference current ones (email) | Joins show today's values; copies must be updated everywhere |
| Key-value: by key only; document: whole aggregates; wide-column: huge known write patterns; search engine: a copy for text search | Each serves its patterns and makes others expensive |
| Analytics from a copy (CDC into a warehouse), never the live database | Ad-hoc scans slow the live service |

## Replication (14.8)

| Rule | Why |
|---|---|
| One primary orders the writes; replicas apply its log, usually behind | Replicas scale reads and give failover, never writes |
| Decide per read: after the user's own write, and for decisions, read the primary | Read-your-writes; stock and payment decisions need current data |
| Keep a user on one replica | Two replicas with different lag make data go back in time (monotonic reads) |
| Asynchronous (MySQL default): a failover can lose lag × write rate of confirmed commits | Lag 2 s at 5.79 writes/s is about 12 orders |
| Semi-synchronous: waits for one replica's **receipt**; falls back to asynchronous after `rpl_semi_sync_source_timeout` (10 s) | Monitor the fallback |
| Lag: don't trust `Seconds_Behind_Source` alone; use a heartbeat row | It often shows 0 when the receiver is late |
| Fence the old primary before promoting | Otherwise split brain |
| A replica is not a backup | Mistakes replicate in seconds |

## Partitioning and sharding (14.9)

| Rule | Why |
|---|---|
| Shard only when writes or data exceed one primary, after scaling up, caching, replicas and archiving | It is a one-way door that multiplies operations |
| Partition key: known by the frequent queries, keeps each transaction in one shard, many evenly used values, never changes | Queries with the key hit one shard; without it, all of them |
| Put the shard in generated ids (`o-4-123`), or keep a lookup table | Lookups by id stay single-shard |
| Many fixed partitions up front (or consistent hashing), never `hash mod N` | Adding a node: about 1/N of keys move instead of about 80% |
| Hot keys: split into sub-keys, cache reads, queue writes | Hashing can't spread one key |
| Range by time is for archiving, not for spreading writes | The newest range takes every write |
| Across shards: unique values via a lookup table sharded by that value; reports from a warehouse | Each shard only sees its own rows |
| MySQL `PARTITION BY` is within one server | It is not sharding |

## Consistency trade-offs (14.10)

| Rule | Why |
|---|---|
| CAP: during a partition, refuse (consistent) or answer and risk being wrong (available); P is not optional | Without a partition you can have both (Brewer, 2012) |
| PACELC: else, consistency costs latency | Paid on every request, not only in faults |
| Choose per operation by cost of wrong answer versus cost of an error | Stock, payments, balances, unique values: consistent; carts: available with merge; pages, counters: eventual |
| Quorum: W + R > N; majority = more than half of N; N=3 with 2 and 2 is the usual balance | 1 and 1 is fast but 6 of 9 cases can be stale |
| Quorum availability with 3 replicas at 99.9%: majority about 99.9997%, all three about 99.70% | `ALL` puts every replica in series |
| Conflicts: avoid (one leader per key), detect (versions), merge (CRDTs), only knowingly drop (last write wins) | Naive union merges bring removed items back |

## Asynchronous processing (14.11)

| Rule | Why |
|---|---|
| In the request only what the user must see now; the rest becomes a message | Removes slow and flaky steps from the chain (1,250 ms at 98.36% became 355 ms at 99.80%) |
| Wait = backlog ÷ capacity; capacity above the average, sized for the burst and the allowed delay | A queue turns overload into delay |
| Alert on the oldest message's age, not the count | Age is the delay users feel |
| Never dual-write: transactional outbox or CDC; publishing is at least once | A crash between two writes loses or invents events |
| Idempotent handlers; limited retries with backoff; then a dead-letter queue with an owner | At-least-once delivery; poison messages |
| Separate queues and limits for urgent and bulk work | A campaign must not delay order confirmations |
| Long jobs: 202 Accepted plus a status resource | RFC 9110: point to a status monitor |
| Downstream limits cap useful workers | 100 emails/s stays 100 emails/s |

## Object storage and CDNs (14.12)

| Rule | Why |
|---|---|
| Files in object storage, never on app disks or in the database | Shared by all instances, scales, cheap per GB |
| S3: strong read-after-write consistency (since Dec 2020), whole-object writes, multipart from about 100 MB (5 MiB to 5 GiB parts, 10,000 max) | Large uploads survive failures part by part |
| Durability (11 nines designed) is not availability (99.99% for S3 Standard) | A safe file can still be unreachable |
| Versioned names + `max-age=31536000, immutable`; invalidation only for mistakes | Same-name updates stay stale for the TTL (CloudFront default 24 h) |
| Personal responses `private` or `no-store`; private files in private buckets via short signed URLs | Shared caches must never hold personal data |
| Uploads directly to storage with presigned URLs; processing from upload events | Uploads through the app hold threads and bandwidth |
| Origin shield in front of the origin | Collapses misses from many edges |

## Unique IDs (14.13)

| Rule | Why |
|---|---|
| Auto-increment only with one writer; otherwise generate ids without coordination | Each shard's counter repeats the others' numbers |
| Database keys: time-ordered (UUID version 7, RFC 9562, or Snowflake-style 64-bit) | Random keys insert all over a B-tree; InnoDB pages fill 1/2 to 15/16 instead of about 15/16 |
| Random ids need about 122 bits | 64 random bits: 2.7% chance of a duplicate at a billion ids |
| Snowflake: 41 bits ms, 10 bits worker, 12 bits sequence (about 70 years, 1,024 workers, 4,096 per ms each) | Lease worker numbers; refuse ids when the clock goes back |
| Always a unique constraint on the key | A clash must fail loudly |
| Separate internal keys from public ids; authorise every request | Sequential public ids leak volume; unguessable ids are not access control |
| Java 21 `UUID`: `randomUUID()` is version 4; no version 7 factory | Build version 7 yourself or use a library |

## Multi-region and disaster recovery (14.14)

| Rule | Why |
|---|---|
| Set RTO (time down) and RPO (data lost) per service from business impact | Checkout and analytics don't need the same targets |
| Strategies: backup and restore, pilot light, warm standby, active/active | Each buys a lower RTO with resources paid in advance |
| RPO comes from the data path: backup interval or replication lag | Replication is not backup: it copies deletions and corruption |
| Failover: detect, decide, promote, scale, move traffic, fence, fail back | Automate the steps; decide carefully (false alarms cost data) |
| Use data-plane operations; keep capacity running where scaling during a disaster is risky | Control planes are often impaired in a regional outage |
| Active/active writes: global, local with conflict resolution, or partitioned by home region | Last writer wins silently drops concurrent updates |
| Test it: failover, restore, failback, quotas, external dependencies | Untested DR doesn't work |

## Case study: URL shortener (14.15)

| Decision | Answer |
|---|---|
| Shape | Read-mostly key-value lookup: code to URL; small data, hot keys in campaigns |
| Codes | Base62; 7 characters = 3.5 trillion; random (unguessable, retry on clash) or counter (enumerable); unique constraint always |
| Redirect | `302`/`307` to count clicks and change destinations; `301`/`308` are cacheable by default |
| Read path | Cache-aside with long TTLs; clicks as events after the redirect |
| Availability | Redirects above creation: serve from cache, replicate to another region |
| Abuse | Trusted creators, allowed destinations, safe-browsing checks, rate limits, a kill switch |

## Case study: distributed rate limiter (14.16)

| Decision | Answer |
|---|---|
| Why shared | Per-instance limits are too strict (uneven balancing) or too loose (multiplied) |
| Correctness | Atomic `INCR` or a Lua script (increment, expire, check); never read-then-write |
| Cost | Local leases of small batches cut store calls by the batch size |
| Algorithm | Token bucket or sliding window; fixed windows allow twice the limit at boundaries |
| Store failure | Short timeout, circuit breaker, local fallback; fail open (search) or closed (login) per endpoint |
| Response | 429 with `Retry-After`, spread so clients don't retry together |
| Regions | A share of the limit per region, rebalanced in the background |

## Case study: notification system (14.17)

| Decision | Answer |
|---|---|
| Flow | Events in; decide (consent, channel, quiet hours), render, deduplicate, queue by priority, send within limits, track |
| Priority | Separate urgent and bulk queues; reserve at least the transactional peak of each provider limit |
| Duplicates | Key = event id + channel; provider idempotency keys; idempotent webhooks |
| Campaigns | Scheduled and paced: 1.2 million emails at 100/s take over 3 hours |
| Failover | Second provider for urgent channels; a timeout may still have delivered |
| Consent | Checked per message at send time; one-click unsubscribe (RFC 8058); bulk email needs SPF, DKIM, DMARC |
| Hygiene | Remove bounced addresses, complainers from marketing, invalid push tokens |

## Case study: news feed (14.18)

| Decision | Answer |
|---|---|
| Fan-out | Push post ids for sources under a follower threshold; pull huge sources at read time; merge |
| Why hybrid | Push cost grows with followers (1 post = 1 million writes); pull cost grows with sources followed |
| Storage | Capped lists of time-ordered ids (`LPUSH` + `LTRIM`), active users only; posts hydrated from a cache |
| Paging | Cursor ("older than id 108"), never offsets |
| Deletes and unfollows | Filter during hydration; timelines are rebuildable caches |
| Freshness | Eventually consistent: seconds for pushed posts |

## Case study: chat system (14.19)

| Decision | Answer |
|---|---|
| Transport | WebSocket to a stateful gateway tier; REST for history and catch-up |
| Truth | Store first with a server-assigned sequence per conversation, then push |
| Retries | Client message ids make sends idempotent (keep the same id for every retry) |
| Catch-up | On reconnect or a gap: fetch everything after the last contiguous sequence |
| Routing | A registry of user device to gateway, expiring; offline means push notification |
| Presence | Heartbeats with expiry; approximate, never the basis for delivery |
| Operations | Size gateways by open connections; drain slowly on deploy; clients reconnect with backoff and jitter |

## Case study: ShopFlow flash sale (14.20)

| Decision | Answer |
|---|---|
| Absorb | CDN for the page and queue status; waiting room with ordered positions |
| Admit | At checkout's safe rate, with short-lived signed admission tokens required at checkout |
| Reserve | Atomic conditional decrement on stock buckets; holds that expire and return; never oversell |
| Hot key | Buckets spread decrements; a sold-out flag answers the crowd without counters |
| Isolate | Own reservation service, keys and pools; pre-scaled; load shedding; reserved payment quota |
| Fairness | Purchase limits per account and card, rate limits on joins, bot defences, an honest fast "sold out" |
| Prove it | A game day with the real traffic shape, failures injected, sales reconciled against stock |

## Recurring decisions

| Decision | First option when | Second option when |
|---|---|---|
| Scale up or out | Moderate load, simplicity | Redundancy needed, growing load, stateless service |
| Sync or async | Caller needs the answer now | Work can follow; bursts; several consumers |
| Strong or eventual consistency | Money, stock, wrong decisions from stale reads | Feeds, counters, notifications |

## 60-second answers

- **What is system design?** Choosing components, data and interactions so a system meets functional and non-functional requirements at its scale, and defending the trade-offs ([14.1](01-system-design-overview.md)).
- **How do you approach a design?** Requirements with numbers, estimate, API and data, simplest design, deep dive on the riskiest part, failures and trade-offs ([14.2](02-requirements-design-method.md)).
- **Why estimate?** To find the order of magnitude and compare it with a limit before designing; then measure ([14.3](03-back-of-the-envelope-estimation.md)).
- **How do you scale a service out?** Make it stateless by moving sessions, files, caches, jobs and counters to shared stores, update shared state atomically, run instances across zones behind a load balancer, and cap autoscaling at what the database can take ([14.4](04-scaling-out-stateless-services.md)).
- **What does a load balancer do?** Gives one address, picks a healthy target with an algorithm, and drains targets that leave; health checks test the instance itself, and deploys stop sending before they stop accepting ([14.5](05-load-balancing.md)).
- **How do you cache safely?** Cache-aside with a TTL and delete on write; coalesce misses for hot keys; never decide money or stock from the cache; plan for the cache being empty ([14.6](06-caching.md)).
- **SQL or NoSQL?** Decide from access patterns and consistency, not fashion: relational by default for core data, specialised stores for named patterns, each copy kept in sync deliberately ([14.7](07-choosing-a-database.md)).
- **What do read replicas give you?** Read scale and failover, not write scale; reads may lag, so reads after a user's own write and reads for decisions go to the primary, and an asynchronous failover can lose the last commits ([14.8](08-replication-read-replicas.md)).
- **When and how do you shard?** Only when one primary can't take the writes or data; choose a key the frequent queries and transactions know, create many fixed partitions, and plan for hot keys and for queries without the key ([14.9](09-partitioning-sharding.md)).
- **What does CAP really say?** During a network partition an operation can't be both linearizable and answered everywhere, so choose per operation; PACELC adds that without partitions consistency costs latency; quorums tune it with W + R > N ([14.10](10-consistency-cap-pacelc-quorums.md)).
- **What would you make asynchronous?** Everything the user doesn't need to see now, through an outbox into a queue or log, with idempotent handlers, a dead-letter queue, and workers sized for the burst; decisions stay synchronous ([14.11](11-asynchronous-processing.md)).
- **Where do images and files go?** Object storage, delivered by a CDN with versioned names and long TTLs; private files through short-lived signed URLs; uploads straight to storage ([14.12](12-object-storage-cdn.md)).
- **How do you generate ids across shards?** Without coordination: UUID version 7 or Snowflake-style ids for database keys, random UUIDs for public tokens, a unique constraint always ([14.13](13-unique-id-generation.md)).
- **How do you design disaster recovery?** Set RTO and RPO per service, choose the cheapest strategy that meets them, keep point-in-time backups in another region and account, and test failover and restores regularly ([14.14](14-multi-region-disaster-recovery.md)).
- **Design a URL shortener.** Base62 codes with a unique constraint, `302` redirects from a cache, click events counted asynchronously, and abuse controls on creation ([14.15](15-case-url-shortener.md)).
- **Design a distributed rate limiter.** One shared counter per key in Redis, changed atomically, with small local leases; token bucket or sliding window; 429 with `Retry-After`; fail open or closed per endpoint ([14.16](16-case-distributed-rate-limiter.md)).
- **Design a notification system.** Events in; decide from preferences and consent, render, deduplicate by event and channel, send urgent before bulk within provider limits, track delivery and clean up ([14.17](17-case-notification-system.md)).
- **Design a news feed.** Hybrid fan-out (push for small sources, pull for huge ones), capped timelines of ids for active users, hydration from a post cache, cursor pagination and filtering on read ([14.18](18-case-news-feed.md)).
- **Design a chat system.** WebSocket gateways and a connection registry; messages stored first with a per-conversation sequence and pushed second; idempotent sends; catch-up after the last sequence; presence for display only ([14.19](19-case-chat-system.md)).
- **Design a flash sale.** Absorb the crowd at the edge, admit buyers at checkout's safe rate with tokens, reserve stock atomically with expiring holds on bucketed counters, isolate the sale from the shop, and tell everyone else quickly ([14.20](20-case-shopflow-flash-sale.md)).

Back to [the System design overview](01-system-design-overview.md) · [the index](../README.md) · [Roadmap](../roadmap.md).
