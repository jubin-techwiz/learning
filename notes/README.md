---
title: Learning Notes
tags: [index, learning]
---

# Learning Notes

Personal reference notes, one per topic, written in simple language. Each note should be readable months later without the original lesson, and useful to architects and engineers alike.

**Purpose:** learn what a software engineer and software architect needs, in a simple and structured way, and be able to revise it quickly. Start at the [Learning Roadmap](roadmap.md), which lists every track, the order to learn them in, and what is done.

> [!TIP]
> Import this whole `notes` folder into Kenmora (Import page, choose the folder). It is the only folder meant for Kenmora. Re-import with "skip" or "replace and keep bookmarks and progress".

## Notes by area

### 01 · Resilience

Start with the overview, which explains how the patterns relate. When you want to revise, use the cheat sheet.

| Step | Note | Status |
|---|---|---|
| 1.1 | [Resilience patterns: the big picture](01-resilience/01-resilience-overview.md) | Done |
| 1.5 | [Circuit Breaker](01-resilience/05-circuit-breaker.md) | Done |
| 1.6 | [Bulkhead](01-resilience/06-bulkhead.md) | Done |
| | [Resilience cheat sheet](01-resilience/99-resilience-cheat-sheet.md) (revision) | Growing |

Planned in this area: timeouts (1.2), retries, backoff and jitter (1.3), idempotency (1.4), rate limiting (1.7), load shedding and backpressure (1.8), fallbacks and graceful degradation (1.9), testing resilience (1.10).

### 02 · Messaging

Start with the overview (queues versus logs, and the four questions every design must answer). The Kafka series then goes from fundamentals to advanced.

| Step | Note | Status |
|---|---|---|
| 2.1 | [Messaging and event-driven design: the big picture](02-messaging/01-messaging-overview.md) | Done |
| 2.2.1 | [Apache Kafka: the big picture](02-messaging/02-kafka/01-kafka-overview.md) (series index) | Done |
| | [Messaging cheat sheet](02-messaging/99-messaging-cheat-sheet.md) (revision) | Growing |

Planned in this area: Kafka fundamentals, intermediate and advanced topics (2.2.2 to 2.2.18), a RabbitMQ series (2.3.x) and a comparison note, "choosing a messaging system" (2.4).

### Coming next

Design patterns (track 3) and microservices (4), followed by architecture foundations (5), data, APIs, security, cloud, observability, delivery and architect skills. The full list, with levels, is in the [roadmap](roadmap.md).

## How the notes are organised

```
notes/
  roadmap.md
  01-resilience/
    01-resilience-overview.md      area index: how the topics relate and in what order
    05-circuit-breaker.md          topic notes, numbered in learning order
    99-resilience-cheat-sheet.md   one-page revision sheet
  02-messaging/                    created when the first note is written
    01-messaging-overview.md
    02-kafka/                      a series: one subject taught from fundamentals to advanced
      01-kafka-overview.md
      02-... .md
```

- **Numbers are paths.** The folder number is the track, the file number is the step (series add one more level), so `01-resilience/05-circuit-breaker.md` is step 1.5 and alphabetical order is learning order.
- **Every area and series starts with an overview** that doubles as its index, and may end with a cheat sheet (`99-...`).
- **Levels:** big series are grouped as Fundamentals, Intermediate and Advanced in their overview, and each note records its `level` in the front matter.
- Every note starts with front matter (`title`, `tags`, plus `category`, `level`, `updated`), then follows the same sections: one-minute summary, where it fits, why it matters for your role (engineer and architect), problem, core idea, how it works, example, mistakes, operating it, example run and results, industry check, Q&A, interview answer.
- **Notes are self-contained.** Examples and results are written inside each note. Hands-on demo apps live elsewhere and are optional.
- Plain Markdown that Kenmora renders: callouts (`> [!NOTE]`), Mermaid diagrams, tables and highlighted code. Links between notes are relative. Obsidian-style `[[wikilinks]]` are not used because Kenmora doesn't convert them.

## How to study a topic

1. Read the area's overview to see where the topic fits.
2. Read the topic note, top to bottom.
3. Answer its Quick Q&A without looking.
4. Optional: try the example yourself if there is a demo.
5. Revise later from the cheat sheet, and revisit the note after about 6 months or when you use the topic at work.

## How they are kept up to date

- After each lesson, the learning coach creates or updates the topic's note, the area overview, this page, and the topic's status in [the roadmap](roadmap.md).
- Each note has an **Industry check** section. Re-verify it before relying on a note.
- Progress tracking (completed lessons, weak areas, next lesson) lives separately in `progress/`, outside this folder.
