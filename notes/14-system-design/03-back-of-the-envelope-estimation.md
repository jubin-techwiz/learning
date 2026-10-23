---
title: Back-of-the-Envelope Estimation
tags: [system-design, estimation, capacity-planning, availability, slo, fundamentals]
category: system-design
level: fundamentals
updated: "2026-10-22"
---

# Back-of-the-Envelope Estimation

**System design · Step 14.3 · Fundamentals** · Last updated 2026-10-22
**Prerequisites:** [Requirements and the design method](02-requirements-design-method.md) (14.2)

## 1. What it is and why it matters

**What is it?** Quick, rounded arithmetic that tells you the **size** of a problem: requests per second, storage, bandwidth and allowed downtime. "Back of the envelope" means it should fit on one: a few multiplications, not a spreadsheet.

**Why do we need it?** Designs differ completely depending on whether a service handles 5 requests a second or 50,000, or stores 500 GB or 500 TB. Without the numbers, you either over-build (sharding a database that would fit on one server) or under-build (finding the limit during the sale).

**When would I meet it?** In step 2 of every design ([14.2](02-requirements-design-method.md)), when a business plan says "ten times more customers next year", when someone proposes a new component, and when you check whether an availability promise is realistic.

> [!TIP]
> **Memory hook:** the goal is the right **order of magnitude** (tens, thousands or millions), not the right digit. A day is about 100,000 seconds.

## 2. Industry terms

| Term | Simple meaning | Why it matters | Example | Related terms |
|---|---|---|---|---|
| **QPS (queries per second)** | Requests handled per second; also **RPS** | The basic unit of load | 231 catalogue reads a second on average | Throughput |
| **DAU (daily active users)** | Distinct users on a day | The usual starting number for traffic | 1 million | MAU (monthly) |
| **Peak factor** | How much busier the peak is than the average | Systems fail at the peak, not the average | The sale hour is 10 times the daily average | Peak-to-average ratio |
| **Read:write ratio** | Reads per write | Read-heavy systems use caches and replicas; write-heavy ones need different designs | 400:1 for the catalogue | Access pattern |
| **Order of magnitude** | A factor of ten | Estimates aim to be right to within one | "Thousands a second, not millions" | Rough estimate |
| **Availability, "nines"** | The share of time or requests served successfully | Each extra nine is ten times less downtime | 99.9% = about 8.8 hours a year | SLA, SLO |
| **Error budget** | The failures an SLO still allows, 100% minus the SLO | Turns reliability into an amount you can spend | 250 failed requests a day | SLO |
| **In series / in parallel** | Parts that are all needed / redundant copies where one is enough | Series lowers availability, parallel raises it | Checkout needs five services; two instances behind a load balancer | Redundancy, SPOF |

**Units.** Estimates use **decimal units**: 1 KB = 1,000 bytes, 1 MB = 10^6, 1 GB = 10^9, 1 TB = 10^12. The binary units (1 KiB = 1,024 bytes) differ by under 10% at these sizes, which doesn't matter for an estimate. Network links are quoted in **bits** per second, storage in **bytes**: multiply bytes by 8.

**Numbers worth remembering:**

| Fact | Value | In your head |
|---|---|---|
| Seconds in a day | 86,400 | about 10^5 |
| 1 million requests a day | about 12 a second | 10^6 ÷ 10^5 = 10 |
| 1 billion requests a day | about 11,600 a second | 10^9 ÷ 10^5 = 10,000 |
| Days in five years | 1,825 | about 2,000 |

**Latency, by order of magnitude.** Peter Norvig's often quoted table of approximate timings shows the ratios that shape designs: fetching from main memory about 100 ns, reading 1 MB sequentially from memory about 250 µs, a disk seek about 8 ms, reading 1 MB sequentially from disk about 20 ms, a network round trip from the US to Europe about 150 ms. The table is undated and its disk figures are for spinning disks; SSDs and modern networks are faster. Use it for the lesson it teaches: memory is far faster than disk, and a round trip between continents costs more than almost anything inside one data centre.

## 3. In a real system (ShopFlow)

**The business problem.** Before the sale, the team must answer three questions: can the current servers and database take the peak, how much order data will we keep in five years, and can checkout really promise the 99.95% availability that the business wants to put in a partner contract?

**Without estimates,** the answers are opinions. One engineer wants to shard the database "to be safe"; another says one server has always been fine. Neither can show why.

**How estimation helps.** Section 4 turns ShopFlow's assumptions into numbers with two small programs. The traffic numbers show the catalogue is read-heavy and the order writes are tiny; the availability numbers show that the checkout path, as built today, can't meet 99.95%.

**What it costs.** Very little: minutes. The risk is false confidence, because the result is only as good as the assumptions. Write the assumptions next to the numbers, replace them with real metrics as soon as you have them, and confirm the important ones with a load test.

## 4. How it works

The method is the same for every estimate:

1. **Write the assumptions** (users, actions per user, sizes, peak factor), each labelled as an assumption.
2. **Convert to per second**: divide daily totals by 86,400 (or 10^5 in your head), then multiply by the peak factor.
3. **Convert to bytes**: count × size, then × retention time × number of copies.
4. **Compare with a limit**: a known capacity, a provider limit, an SLO. The comparison is the point; a number on its own decides nothing.
5. **Say what it means for the design**, and which assumption would change the conclusion if it were wrong.

The two programs below do this arithmetic so you can change the assumptions and run them again. They are complete Java 21 programs; how to run them is in [the Java overview](../13-java/01-java-overview.md), section 5. The assumptions are illustrative, not measured.

### Worked example 1: traffic, storage and bandwidth

**Concept.** Turn daily usage into peak requests per second, the order data into storage over the retention period, and responses into bandwidth.

```java
// File: CapacityEstimate.java (runnable, Java 21)
import java.util.Locale;

public class CapacityEstimate {

    // Illustrative assumptions for ShopFlow, not measured figures. Change them and run again.
    static final long DAILY_ACTIVE_USERS = 1_000_000;
    static final long PAGE_VIEWS_PER_USER = 20;      // catalogue and search reads per user per day
    static final long ORDERS_PER_DAY = 50_000;
    static final long PEAK_FACTOR = 10;              // the busiest sale hour compared with the daily average
    static final long BYTES_PER_ORDER = 2_000;       // order row, its lines and their index entries
    static final long RESPONSE_BYTES = 20_000;       // one catalogue API response (JSON, images come from a CDN)
    static final long RETENTION_YEARS = 5;
    static final long COPIES = 3;                    // primary database plus two replicas

    static final long SECONDS_PER_DAY = 24 * 60 * 60;   // 86,400

    public static void main(String[] args) {
        long readsPerDay = DAILY_ACTIVE_USERS * PAGE_VIEWS_PER_USER;
        double avgReadQps = (double) readsPerDay / SECONDS_PER_DAY;
        double peakReadQps = avgReadQps * PEAK_FACTOR;
        double avgWriteQps = (double) ORDERS_PER_DAY / SECONDS_PER_DAY;
        double peakWriteQps = avgWriteQps * PEAK_FACTOR;

        long bytesPerDay = ORDERS_PER_DAY * BYTES_PER_ORDER;
        long bytesRetained = bytesPerDay * 365 * RETENTION_YEARS;
        long bytesWithCopies = bytesRetained * COPIES;

        double peakEgress = peakReadQps * RESPONSE_BYTES;          // bytes per second

        System.out.println("Traffic");
        line("reads per day", fmt("%,d", readsPerDay));
        line("average read QPS", fmt("%,.1f", avgReadQps));
        line("peak read QPS", fmt("%,.1f", peakReadQps));
        line("average write QPS", fmt("%.2f", avgWriteQps));
        line("peak write QPS", fmt("%.2f", peakWriteQps));
        line("read:write ratio", fmt("%d:1", readsPerDay / ORDERS_PER_DAY));
        System.out.println("Storage (orders)");
        line("per day", bytes(bytesPerDay));
        line("over " + RETENTION_YEARS + " years", bytes(bytesRetained));
        line("with " + COPIES + " copies", bytes(bytesWithCopies));
        System.out.println("Bandwidth");
        line("peak egress", bytes((long) peakEgress) + "/s = " + fmt("%,.0f", peakEgress * 8 / 1_000_000) + " Mbit/s");
    }

    // Decimal units (1 MB = 1,000,000 bytes), the usual choice for estimates.
    static String bytes(long b) {
        String[] units = {"B", "KB", "MB", "GB", "TB", "PB"};
        double value = b;
        int unit = 0;
        while (value >= 1000 && unit < units.length - 1) {
            value /= 1000;
            unit++;
        }
        return fmt("%.1f %s", value, units[unit]);
    }

    // Locale.ROOT gives the same separators (1,234.5) on every machine.
    static String fmt(String pattern, Object... args) {
        return String.format(Locale.ROOT, pattern, args);
    }

    static void line(String label, String value) {
        System.out.println(fmt("  %-20s %s", label, value));
    }
}
```

**Expected output**:

```text
Traffic
  reads per day        20,000,000
  average read QPS     231.5
  peak read QPS        2,314.8
  average write QPS    0.58
  peak write QPS       5.79
  read:write ratio     400:1
Storage (orders)
  per day              100.0 MB
  over 5 years         182.5 GB
  with 3 copies        547.5 GB
Bandwidth
  peak egress          46.3 MB/s = 370 Mbit/s
```

**Walkthrough: the same numbers in your head.**

| Quantity | Head arithmetic | Program |
|---|---|---|
| Average reads | 1M users × 20 = 20M a day; 20M ÷ 10^5 = **200 a second** | 231.5 |
| Peak reads | 200 × 10 = **2,000 a second** | 2,314.8 |
| Peak writes | 50,000 ÷ 10^5 × 10 = **5 a second** | 5.79 |
| Storage | 50,000 × 2 KB = 100 MB a day; × 365 × 5 ≈ **180 GB**; × 3 ≈ **550 GB** | 547.5 GB |
| Peak bandwidth | 2,000 × 20 KB = **40 MB/s**, × 8 = **320 Mbit/s** | 46.3 MB/s, 370 Mbit/s |

Using 10^5 instead of 86,400 makes the head figures about 14% low, which is well inside "the right order of magnitude".

**What the numbers say about the design:**
- **Read-heavy (400:1).** Reads are where the effort goes: caching ([14.6](06-caching.md)) and read replicas ([14.8](08-replication-read-replicas.md)) are candidates. Whether 2,300 reads a second needs a cache depends on what one read costs, which a load test measures; the estimate only says this is the number to test.
- **Writes are tiny.** About 6 order writes a second at peak is a small load for a single relational database, so the writes give no reason to shard.
- **Storage fits one machine.** About 550 GB with copies needs no partitioning ([14.9](09-partitioning-sharding.md)) for years.
- **Bandwidth is spread.** 370 Mbit/s at peak is shared by several instances; images, which are much bigger than these JSON responses, should come from a CDN ([14.12](12-object-storage-cdn.md)).

**Experiment.** Predict first: if the sale peak is only 3 times the average (change `PEAK_FACTOR` to 3), what happens to peak reads, peak bandwidth and the five-year storage? The result is in Answers.

**In ShopFlow.** The same five lines of arithmetic answer "do we need to shard?" in a design review: no, the data is 550 GB, and the writes are 6 a second. The discussion moves to the real question, the read peak.

### Worked example 2: availability, error budgets and dependencies

**Concept.** An availability target allows a certain amount of downtime. Parts that are **all needed** (in series) multiply their availabilities, so the whole is less available than any part. Redundant copies (in parallel) are down only when **all** of them are down, if their failures are independent.

```java
// File: AvailabilityMath.java (runnable, Java 21)
import java.util.List;
import java.util.Locale;

public class AvailabilityMath {

    record Component(String name, double availability) {}

    public static void main(String[] args) {
        System.out.println("Allowed downtime");
        for (double target : List.of(0.99, 0.999, 0.9995, 0.9999)) {
            double perYear = (1 - target) * 365 * 24 * 60;     // minutes
            double per30Days = (1 - target) * 30 * 24 * 60;
            System.out.println(fmt("  %-7s %-13s per year, %-13s per 30 days",
                    percent(target), duration(perYear), duration(per30Days)));
        }

        // Illustrative targets for the checkout path: every call is synchronous and needed.
        List<Component> checkout = List.of(
                new Component("api-gateway", 0.9995),
                new Component("order-service", 0.9995),
                new Component("payment-service", 0.999),
                new Component("inventory-service", 0.9995),
                new Component("MySQL", 0.9995));
        double inSeries = 1.0;
        for (Component c : checkout) {
            inSeries *= c.availability();                  // all must be up at the same time
        }
        System.out.println("Checkout path, " + checkout.size() + " components in series");
        System.out.println(fmt("  availability %s, downtime %s per year",
                percent(inSeries), duration((1 - inSeries) * 365 * 24 * 60)));

        double single = 0.99;
        double twoInParallel = 1 - (1 - single) * (1 - single);   // down only if both are down
        System.out.println("Two independent instances of " + percent(single) + " in parallel");
        System.out.println(fmt("  availability %s", percent(twoInParallel)));

        long requestsPerDay = 2_500_000;
        double slo = 0.9999;
        System.out.println(fmt("Error budget for %,d requests a day at %s", requestsPerDay, percent(slo)));
        System.out.println(fmt("  %d failed requests a day", Math.round(requestsPerDay * (1 - slo))));
    }

    static String percent(double a) {
        return fmt("%.2f%%", a * 100);
    }

    static String duration(double minutes) {
        if (minutes >= 48 * 60) return fmt("%.2f days", minutes / (24 * 60));
        if (minutes >= 60) return fmt("%.2f hours", minutes / 60);
        return fmt("%.2f minutes", minutes);
    }

    static String fmt(String pattern, Object... args) {
        return String.format(Locale.ROOT, pattern, args);
    }
}
```

**Expected output**:

```text
Allowed downtime
  99.00%  3.65 days     per year, 7.20 hours    per 30 days
  99.90%  8.76 hours    per year, 43.20 minutes per 30 days
  99.95%  4.38 hours    per year, 21.60 minutes per 30 days
  99.99%  52.56 minutes per year, 4.32 minutes  per 30 days
Checkout path, 5 components in series
  availability 99.70%, downtime 26.25 hours per year
Two independent instances of 99.00% in parallel
  availability 99.99%
Error budget for 2,500,000 requests a day at 99.99%
  250 failed requests a day
```

**Walkthrough.**

| Line | Arithmetic | What it teaches |
|---|---|---|
| 99.99% per year | 0.0001 × 525,600 minutes = 52.56 minutes | Each extra nine is ten times less downtime |
| Checkout in series | 0.9995 × 0.9995 × 0.999 × 0.9995 × 0.9995 = 0.9970 | Five good parts give a worse whole: 99.70% is about **26 hours a year**, so promising 99.95% is not realistic as built |
| Two instances in parallel | 1 − (0.01 × 0.01) = 0.9999 | Two 99% instances give 99.99%, **only if** they fail independently |
| Error budget | 2,500,000 × 0.0001 = 250 | The SLO becomes a daily allowance of failed requests |

The 52.56 minutes and the 250 failed requests match the examples in Google's *Site Reliability Engineering* book (chapter "Embracing Risk"), which is a useful check on the arithmetic.

**Two cautions.** First, "independent" is a strong assumption: two instances in the same zone, running the same faulty release, or sharing one database, fail together, so real redundancy is usually much less than the formula says. Second, the series model assumes every call is required. A call that has a fallback ([1.9](../01-resilience/09-fallbacks-graceful-degradation.md)), or that happens after the response through a queue, is not in series, which is the most effective way to raise availability (practice exercise 3).

**Experiment.** Predict first: if the Payment Service alone improves from 99.9% to 99.99%, what does the checkout path reach? The result is in Answers.

**In ShopFlow.** The calculation turns a contract discussion into a design discussion: to promise 99.95%, the team must take parts out of the synchronous path, add redundancy and fallbacks, or promise less.

## 5. Failure scenarios

### Designed for the average

1. **What happened?** The catalogue was load-tested at 300 requests a second, the daily average plus a margin. At 9:00 on sale day, traffic reached about ten times the average, and pages timed out.
2. **Why?** The estimate stopped at the average; nobody applied a peak factor.
3. **What did users see?** Slow pages and errors in the first, most valuable hour of the sale.
4. **Solutions:** always estimate the peak, using the previous sale's metrics as the peak factor when you have them; load-test at the peak with headroom; add [load shedding](../01-resilience/08-load-shedding-backpressure.md) for traffic beyond it.
5. **What they cost:** capacity that sits idle most of the year (or autoscaling to manage), and a bigger load test.

### Bits and bytes

1. **What happened?** A link for about 46 MB/s of peak traffic was ordered as 50 Mbit/s.
2. **Why?** Storage and application metrics use bytes; network links use bits. 46 MB/s is about 370 Mbit/s.
3. **What did users see?** Slow responses once traffic passed about one eighth of the planned peak.
4. **Solutions:** write the unit on every number; convert with × 8 before comparing with a link.
5. **What they cost:** nothing but care.

### A promise the dependencies can't keep

1. **What happened?** A partner contract (an SLA) promised 99.95% for checkout, while the five services it needs together give about 99.70%.
2. **Why?** Each team's 99.9% or 99.95% sounded good alone; nobody multiplied them.
3. **What did the business see?** Contract penalties after the first bad month.
4. **Solutions:** compute the chain before promising; remove calls from the synchronous path (queue them, or give them fallbacks); add redundancy where failures really are independent; promise less in the SLA than the internal SLO.
5. **What they cost:** redesign of the checkout path, or a less attractive contract.

## 6. Architectural decisions

| Estimate when | Don't rely on an estimate when | Alternatives |
|---|---|---|
| At the start of every design, briefly; when a number is close to a known limit (provider limit, machine size, SLO), in more detail | Real production metrics exist: measure instead; the decision is expensive and the estimate is near the limit: confirm with a load test | Production metrics and dashboards; load tests; capacity models built from real data |

**Common mistakes:** too much precision ("2,314.8 requests a second" implies an accuracy the assumptions don't have, so round it to "about 2,000 to 2,500"); forgetting copies (replicas, backups) and indexes in storage; mixing bits and bytes; adding availabilities of parts in series instead of multiplying them.

## 7. Try it yourself

Three small practice exercises. Try each on paper first, then check with the runnable solutions in the Answers section. All numbers are illustrative assumptions.

1. **Notification rate.** Each order causes three messages (confirmation email, shipped email, push message). With 50,000 orders a day and a peak factor of 10, how many messages a second are sent at peak? Is an email provider limit of 100 a second enough? How long does a burst of 200,000 back-in-stock alerts take at that limit (the case from [14.2](02-requirements-design-method.md))?
2. **Click events in Kafka.** Every page view (20 million a day) produces a 500-byte click event in a Kafka topic with 7 days of retention and a replication factor of 3 ([retention](../02-messaging/02-kafka/13-retention-compaction-tiered-storage.md), [replication](../02-messaging/02-kafka/06-brokers-replication-leader-election.md)). How much disk does the topic use across the cluster, and what is the peak ingest rate with a peak factor of 10?
3. **A fraud check.** The fraud team wants a 99.9% fraud-check service in the checkout path. What does checkout reach (starting from the 99.70% of worked example 2)? What if the fraud check runs after the order is accepted instead?

## 8. Check your understanding

1. Where should a peak factor come from, and what do you do when you don't have one?
2. About how many requests a second is 1 billion requests a day? Work it out in your head.
3. Two instances of a 99.9% service run behind a load balancer. Why can the real availability be far below the formula's 99.9999%?
4. The business wants to promise 99.95% for checkout, and the chain of dependencies computes to 99.70%. Name three ways forward.

Answers are at the end of this note.

## 9. Revision

**Remember**
- A day is about **10^5 seconds**; divide daily totals by it, then multiply by the **peak factor**.
- Storage = count × size × retention × **copies**; bandwidth in bits = bytes × 8.
- Availabilities **in series multiply**; redundant copies help only if failures are **independent**.
- An estimate is for the **order of magnitude** and for comparing with a **limit**; then measure.

**Explain in 15 seconds:** "A back-of-the-envelope estimate turns assumptions about users and sizes into requests per second at peak, storage and bandwidth, and checks availability targets, so you know the order of magnitude before you design."

**Explain in 60 seconds:**
> "I start from stated assumptions: daily users, actions per user, sizes and a peak factor. Dividing daily totals by about 100,000 seconds gives the average rate, and the peak factor gives the rate to design for. Count times size times retention times copies gives storage, and bytes times eight gives the bandwidth in bits. For availability, parts that are all needed multiply, so five components at 99.9 to 99.95% give about 99.7%, around 26 hours of downtime a year. The numbers only matter when compared with a limit: a server's measured capacity, a provider's rate limit or an SLO. They tell me where to look, and a load test or real metrics confirm it."

**Likely follow-up questions:**
- **How precise should an estimate be?** Within a factor of about two to ten; round aggressively and say which assumption matters most.
- **What is an error budget used for?** Deciding how much risk to take: while budget remains, ship changes; when it's spent, slow down and fix reliability.
- **Why does adding a synchronous dependency lower availability even if it is very reliable?** Because checkout now needs it too; its downtime adds to the others'.
- **Average or peak?** Design and test for the peak; use the average for cost and storage.

**Architect's takeaway:** a five-line estimate settles arguments that opinions can't. Write the assumptions next to the numbers, compare with a real limit, and replace guesses with measurements as soon as they exist.

**Next topic:** [Scaling out: stateless services and horizontal scaling](04-scaling-out-stateless-services.md) (14.4)

## 10. Verification and sources

> [!NOTE]
> Checked 2026-10-22. All five programs were run on OpenJDK 21.0.8, and their output is checked by the repository's example checker. Re-verify when you revisit this note.

- **Confirmed:** 99.99% availability allows 52.56 minutes of downtime a year, and 2.5 million requests a day at a 99.99% daily target allow 250 errors (Google, *Site Reliability Engineering*, chapter "Embracing Risk", read online 2026-10-22; the programs reproduce both numbers). The latency figures are from Peter Norvig's "Teach Yourself Programming in Ten Years" (norvig.com, read 2026-10-22), where they are labelled approximate and undated.
- **Assumptions and illustrations:** every ShopFlow number (users, page views, orders, sizes, peak factor, service availabilities, the email limit) is an illustrative assumption. The series and parallel formulas assume independent failures and, for series, that every call is required.
- **To verify:** your own services' measured capacity before deciding from these numbers; current latency figures for your hardware and cloud (SSD and network latencies are much lower than the spinning-disk figures in Norvig's table).
- **Decision rule:** estimate the peak, compare it with a limit, and let the comparison decide where to look; confirm with measurements before an expensive decision.

**Sources:** Google, *Site Reliability Engineering* (sre.google/sre-book), chapter "Embracing Risk"; Peter Norvig, "Teach Yourself Programming in Ten Years" (norvig.com/21-days.html), section "Approximate timing for various operations"; Alex Xu, *System Design Interview: An Insider's Guide*, chapter on back-of-the-envelope estimation.

## Answers

**Experiments**

- **Peak factor 3** (worked example 1): peak reads fall from 2,314.8 to **694.4 a second**, peak writes to 1.74, and peak bandwidth to **13.9 MB/s = 111 Mbit/s**. Storage stays at **547.5 GB**: it depends on totals, not on the peak. Lesson: the peak factor is the assumption that decides the capacity, so take it from real metrics of earlier sales.
- **Payment Service at 99.99%** (worked example 2): checkout rises from 99.70% to **99.79%, 18.38 hours down a year**. Improving one part helps only a little, because the other four still multiply; 99.95% is still out of reach.

**Check your understanding**

1. From metrics of earlier peaks (last year's sale, the busiest hour of a normal week), as the ratio of peak to average. Without data, state an assumption (for a sale, perhaps 5 to 10), design and load-test for it, and mark it as the most important assumption to replace with measurements.
2. 10^9 ÷ 10^5 = **about 10,000 a second** (11,574 without rounding). At peak, multiply by the peak factor.
3. The formula assumes the two fail **independently**. In practice they often fail together: the same faulty release reaches both, both sit in one zone, or both depend on one database or one load balancer, which are themselves in series.
4. Any three of: take calls out of the synchronous path (queue them, run them after the order is accepted); give non-essential calls a fallback; add redundancy where failures really are independent (several zones); improve the weakest parts; or promise less in the SLA and keep the internal SLO higher.

**Practice exercises, runnable solutions**

Exercise 1, notification rate:

```java
// File: NotificationRate.java (runnable, Java 21)
import java.util.Locale;

public class NotificationRate {

    public static void main(String[] args) {
        long ordersPerDay = 50_000;
        long messagesPerOrder = 3;          // confirmation email, shipped email, push message
        long peakFactor = 10;
        double providerLimitPerSecond = 100;
        long stockAlertBurst = 200_000;     // subscribers to one popular product

        long messagesPerDay = ordersPerDay * messagesPerOrder;
        double avgPerSecond = messagesPerDay / 86_400.0;
        double peakPerSecond = avgPerSecond * peakFactor;
        double headroom = providerLimitPerSecond / peakPerSecond;
        double burstSeconds = stockAlertBurst / providerLimitPerSecond;

        System.out.println(fmt("messages per day      %,d", messagesPerDay));
        System.out.println(fmt("average per second    %.2f", avgPerSecond));
        System.out.println(fmt("peak per second       %.2f", peakPerSecond));
        System.out.println(fmt("headroom at peak      %.1fx (provider limit divided by peak rate)", headroom));
        System.out.println(fmt("stock-alert burst     %,.0f seconds = %.1f minutes", burstSeconds, burstSeconds / 60));
    }

    static String fmt(String pattern, Object... args) {
        return String.format(Locale.ROOT, pattern, args);
    }
}
```

**Expected output**:

```text
messages per day      150,000
average per second    1.74
peak per second       17.36
headroom at peak      5.8x (provider limit divided by peak rate)
stock-alert burst     2,000 seconds = 33.3 minutes
```

Yes, the limit is enough for normal traffic, with almost six times headroom at peak. The 200,000-alert burst takes about **33 minutes**, which is why the stock alert design in [14.2](02-requirements-design-method.md) caps and spreads the burst and keeps transactional emails on a separate limit.

Exercise 2, click events in Kafka:

```java
// File: ClickEventStorage.java (runnable, Java 21)
import java.util.Locale;

public class ClickEventStorage {

    public static void main(String[] args) {
        long eventsPerDay = 1_000_000L * 20;     // one event per page view
        long bytesPerEvent = 500;
        long retentionDays = 7;
        long replicationFactor = 3;
        long peakFactor = 10;

        long bytesPerDay = eventsPerDay * bytesPerEvent;
        long retained = bytesPerDay * retentionDays;
        long onDisk = retained * replicationFactor;
        double avgIngest = bytesPerDay / 86_400.0;            // bytes per second

        System.out.println(fmt("per day               %.1f GB", bytesPerDay / 1e9));
        System.out.println(fmt("retained (%d days)     %.1f GB", retentionDays, retained / 1e9));
        System.out.println(fmt("on disk (x%d)          %.1f GB", replicationFactor, onDisk / 1e9));
        System.out.println(fmt("average ingest        %.1f KB/s", avgIngest / 1e3));
        System.out.println(fmt("peak ingest           %.1f MB/s", avgIngest * peakFactor / 1e6));
    }

    static String fmt(String pattern, Object... args) {
        return String.format(Locale.ROOT, pattern, args);
    }
}
```

**Expected output**:

```text
per day               10.0 GB
retained (7 days)     70.0 GB
on disk (x3)          210.0 GB
average ingest        115.7 KB/s
peak ingest           1.2 MB/s
```

About **210 GB** across the cluster, at an ingest rate of about 1.2 MB/s at peak. Neither is large for a Kafka cluster; the estimate shows that retention and replication, not the rate, decide the disk.

Exercise 3, a fraud check:

```java
// File: CheckoutWithFraud.java (runnable, Java 21)
import java.util.List;
import java.util.Locale;

public class CheckoutWithFraud {

    public static void main(String[] args) {
        List<Double> today = List.of(0.9995, 0.9995, 0.999, 0.9995, 0.9995);   // gateway, order, payment, inventory, MySQL
        double fraudCheck = 0.999;

        double before = product(today);
        double syncFraud = before * fraudCheck;     // checkout now also needs the fraud check to be up
        double asyncFraud = before;                 // checkout no longer waits for it

        print("today", before);
        print("fraud check in the call path", syncFraud);
        print("fraud check after the order (async)", asyncFraud);
    }

    static double product(List<Double> parts) {
        double result = 1.0;
        for (double a : parts) {
            result *= a;
        }
        return result;
    }

    static void print(String label, double a) {
        double hours = (1 - a) * 365 * 24;
        System.out.println(String.format(Locale.ROOT, "%-37s %.2f%%  (%.1f hours down per year)", label, a * 100, hours));
    }
}
```

**Expected output**:

```text
today                                 99.70%  (26.2 hours down per year)
fraud check in the call path          99.60%  (35.0 hours down per year)
fraud check after the order (async)   99.70%  (26.2 hours down per year)
```

In the call path, the fraud check lowers checkout to **99.60%**, about 9 more hours of downtime a year. Run after the order is accepted, it stays out of the chain and checkout keeps **99.70%**; the price is that a fraudulent order must be cancelled afterwards instead of being refused, which is a business decision.

Back to [the System design overview](01-system-design-overview.md) · [the index](../README.md) · [Roadmap](../roadmap.md).
