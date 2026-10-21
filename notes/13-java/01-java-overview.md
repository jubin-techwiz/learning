---
title: "Java Language and Platform: The Big Picture"
tags: [java, overview, language, platform]
category: java
level: fundamentals
updated: "2026-10-21"
---

# Java Language and Platform: The Big Picture

**Java · Step 13.1 · Overview** · Last updated 2026-10-21
**Prerequisites:** none

Read this first. It is the map of the Java area: why it is on the roadmap, how it is organised, and what is written so far.

## 1. What it is and why it matters

**What is it?** The Java area covers the language and platform features an engineer uses every day and an architect must judge: how the code is expressed (for example streams), and how it runs (threads, memory, the JVM).

**Why do we need it?** Every ShopFlow service is written in Java. Architecture decisions such as "this pipeline can run in parallel" or "this code allocates too much" depend on knowing what the language and runtime actually do, not on habit.

**When would I meet it?** In every code review, performance investigation and library choice.

> [!TIP]
> **Memory hook:** the architect knows what the compiler and the JVM promise, and what they only usually do.

## 2. Industry terms

| Term | Simple meaning | Why it matters | Example | Related terms |
|---|---|---|---|---|
| **JDK** | The Java Development Kit: compiler, libraries and tools | The version decides which features exist | JDK 21 | JRE, JVM |
| **JVM** | The runtime that executes Java bytecode | Where performance and memory behaviour live | HotSpot | Garbage collector |
| **LTS release** | A long-term-support Java version | What most production systems run | Java 21 | Feature release |
| **Stream API** | Library for describing computations over sequences as pipelines | The first topic in this area | `list.stream()` | Collections, lambdas |
| **Lambda / functional interface** | A short function passed as a value, and the interface it implements | The building block streams use | `x -> x * 2` | Method reference |

## 3. In a real system (ShopFlow)

**The business problem.** ShopFlow's services must stay readable and correct as teams grow, and the code in them must do what reviewers believe it does.

A reviewer sees a loop that builds a report from orders and a colleague proposes a stream pipeline "because it is more modern". Without knowing how streams really behave (when they run, what they may reorder, what is unsafe in parallel), the team can't tell a good rewrite from a hidden bug.

## 4. How it works: the topics in this area

| Step | Topic | The question it answers |
|---|---|---|
| 13.2 | **Streams** (series) | How do I express data processing as a pipeline, and when should I not? |
| later | Records, sealed types and pattern matching | How do I model data precisely? |
| later | Concurrency and virtual threads | How do threads, executors and virtual threads behave? |
| later | Memory and the JVM | How do allocation, garbage collection and tuning work? |

Only the Streams series is written so far; the rest are listed as plain text and can be reordered or changed when they start.

## 5. Running the examples on your Mac

The Java notes are hands-on: every important concept comes with a complete program you can run, its exact output, and a change to try. This section is the one place that explains how to run them.

**1. Check the Java version.** The examples use Java 21 (an LTS release); each lesson's version note says which feature arrived in which Java version. On this Mac, Java is managed with SDKMAN, and Temurin 21 is installed:

```bash
sdk current java          # shows the active version, for example 21.0.8-tem
sdk use java 21.0.8-tem   # switch this terminal to it, if another version is active
java -version             # must say version "21..."
```

If `sdk` isn't found in a new terminal, open a new one or run `source ~/.sdkman/bin/sdkman-init.sh`.

**2. Run an example from the terminal.** Copy the code block into a file with the name given on its first line (for example `PaidTotal.java`), then run:

```bash
java PaidTotal.java
```

This is Java's single-file source launcher (JDK 11 and later): it compiles the file in memory and runs the first class in it, so there is no separate `javac` step and no project to set up. Compare what you see with the note's **Expected output**.

**3. Run it from VS Code.** With the Extension Pack for Java installed, open the folder that contains the file, open the file, and click **Run** above the `main` method (or press F5). If VS Code picks the wrong JDK, run the command **Java: Configure Java Runtime** and choose Java 21. A single file without a Maven or Gradle project is supported (the Java extension treats the folder as unmanaged sources). These VS Code steps were not tested by the author; the terminal steps were.

**4. When it doesn't run.** "Unsupported class file" or syntax errors for records or `toList()` usually mean an older Java is active: check `java -version`. A class name that differs from the file name is confusing; keep them the same, as the examples do.

**The shared setup.** Every example uses the same ShopFlow data, repeated at the top of each program so that each file runs on its own. This is a fragment, not a program by itself:

```java
enum Status { PAID, PENDING, CANCELLED }

record Order(String id, String customer, Status status, long totalCents) {}

static List<Order> sampleOrders() {
    return List.of(
        new Order("o-1001", "ana", Status.PAID,      8_000),
        new Order("o-1002", "ben", Status.CANCELLED, 3_000),
        new Order("o-1003", "ana", Status.PAID,      2_500),
        new Order("o-1004", "cy",  Status.PENDING,   4_500),
        new Order("o-1005", "cy",  Status.PAID,     12_000));
}
```

Amounts are in cents in a `long`, the usual way to avoid floating-point rounding with money; the examples format them as euros when printing.

**From 13.2.3 on, each order also carries its lines** (the products bought), so pipelines can flatten orders into lines. The five orders and their totals don't change, and each order's lines add up to its total:

```java
record OrderLine(String sku, int quantity, long unitCents) {}

record Order(String id, String customer, Status status, long totalCents, List<OrderLine> lines) {}
```

The full sample data with lines is listed at the start of [13.2.3](02-streams/03-transforming-filtering.md). Earlier examples keep the version without lines; both run on their own.

## 6. Architectural decisions

- **Which Java version?** Each lesson has a short version note: the *baseline* (the version the core idea comes from, for example Java 8 for streams), the *newer features* and the version that *introduced* each, and the *hands-on baseline* the examples run on (Java 21, tested with 21.0.8). The idea and the API can come from different versions, which matters when a production service still runs Java 8, 11 or 17.
- **Language feature or library?** Prefer the standard library feature when it states the intent more clearly than a loop, and keep the loop when it doesn't.

## 7. Notes in this area, in learning order

| Step | Topic | Prerequisites | Status |
|---|---|---|---|
| 13.1 | This overview | none | Done |
| 13.2 | [Java Stream API (series)](02-streams/01-streams-overview.md) | 13.1 | Learning |

## 8. Check your understanding

1. Why does the Java version matter when you read a note about a language feature?
2. Name one question about code where knowing the JVM's behaviour, not just the syntax, changes the answer.

Answers are at the end of this note.

## 9. Revision

**Remember**
- **The version decides what exists**; notes say which version they use.
- **Know what the platform promises** versus what it usually does.

**Explain in 15 seconds:** "The Java area covers language and runtime features an architect has to judge, starting with streams."

**Explain in 60 seconds:**
> "Our services are written in Java, so decisions about readability, parallelism and performance depend on what the language and the JVM really guarantee. I learn each feature by what it promises, where it is unsafe, and when a plain alternative is better, and I note the Java version, because features and defaults change between releases."

**Architect's takeaway:** judge language features by their guarantees and their failure modes, not by how modern they look.

**Next topic:** [Java Stream API: the big picture](02-streams/01-streams-overview.md) (13.2.1)

## 10. Verification and sources

> [!NOTE]
> Written 2026-10-21. Re-verify version statements when you revisit this note.

- **Confirmed:** the examples in this area are run on OpenJDK 21.0.8 (Temurin, an LTS release) and checked automatically: each runnable block is run and its output compared with the note. JEP 330 (JDK 11) added the single-file source launcher: the file is compiled in memory and the first top-level class in it, which must have a standard `main` method, is run.
- **Observed (2026-10-21):** `sdk current java` reported `21.0.8-tem`; the VS Code extensions `vscjava.vscode-java-pack` and `redhat.java` were installed. The terminal steps were run; the VS Code steps describe the installed extensions and were not exercised by the author.
- **Assumptions and illustrations:** the ShopFlow scenario; the list of later topics is a proposal.
- **To verify:** which Java version your projects run in production.
- **Decision rule:** use the standard feature when it states the intent more clearly, and state the Java version.

**Sources:** the Java SE 21 API documentation (docs.oracle.com); OpenJDK JEP 330, "Launch Single-File Source-Code Programs"; OpenJDK JEPs for newer features; SDKMAN documentation.

## Answers

1. Features, defaults and library methods are added in specific releases (for example `Stream.toList()` arrived in Java 16), so a note is only correct for the versions it states.
2. For example, whether a parallel pipeline is safe depends on how the runtime splits the work and on whether your lambdas share mutable state, not on the syntax.

Back to [the index](../README.md) · [Roadmap](../roadmap.md).
