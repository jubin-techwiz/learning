---
title: <Topic name>
tags: [<area>, <technology-or-concept>, <level>]
category: <area slug, for example messaging>
level: <fundamentals | intermediate | advanced>
updated: "<YYYY-MM-DD>"
---

# <Topic name>

<!-- Always keep: 1 concept, 3 industry terms (only those needed), 4 real system, 5 how it works (numbered flow), the questions, revision, verification and Answers. Everything else is optional: delete it when it adds nothing, and never pad a short topic. Number the sections that are present, without gaps: if section 2, 6, 7 or 8 is deleted, renumber the rest. Every reference to another note is a relative link. -->

**<Area> · <Series, if any> · Step <N.N.N> · <Level>** · Last updated <YYYY-MM-DD>
**Prerequisites:** [<earlier note>](<relative link>), [<earlier note>](<relative link>)

## 1. The concept in simple English

**What is it?** <One or two plain sentences.>

**Why do we need it?** <The real problem it solves.>

**When would I meet it?** <A realistic engineering situation.>

> [!TIP]
> **Memory hook:** <a short, accurate phrase>

## 2. Mental model

<!-- Optional. Use only if an analogy genuinely helps. Delete this section otherwise. -->

<The analogy in two or three sentences.>

| In the analogy | In the real system |
|---|---|
| <thing> | <component> |

**Where the analogy breaks:** <the important difference>

## 3. Industry terms

| Term | Simple meaning | Why it matters | Example | Related terms |
|---|---|---|---|---|
| **<term>** | <plain meaning> | <problem it addresses> | <in ShopFlow or a real system> | <distinctions, common confusion> |

<Only the terms this note needs. Definitions must agree with the area glossary.>

## 4. In a real system (ShopFlow)

**The business problem.** <What ShopFlow needs, before any technology.>

<What goes wrong without the concept, in a few sentences.>

```mermaid
<"Before" diagram: where the problem shows up. Delete if a picture adds nothing.>
```

**How the concept helps.** <The solution in ShopFlow terms.>

**What it costs.** <The trade-offs it introduces.>

<!-- Optional. Delete if it adds nothing. -->
**Elsewhere:** <two or three short, generic cases from other industries (payments, ride-hailing, streaming, IoT); never claim how a named company works internally without a source>

## 5. How it works

<!-- The diagram is optional: keep it only if it shows an interaction, sequence, state, decision or failure that text can't. The numbered steps and the code block are likewise only as long as the topic needs. -->

```mermaid
<Sequence, flow or state diagram of the mechanism, including the failure path where relevant.>
```

1. **What starts it:** <...>
2. **Who takes part:** <...>
3. **How the data or state moves:** <...>
4. **On success:** <...>
5. **On failure:** <...>

**Who guarantees what** <!-- Include when several layers are involved; otherwise delete. -->

| Layer | Responsible for |
|---|---|
| Broker / platform | <...> |
| Framework | <...> |
| Application | <...> |
| External systems | <...> |

> [!NOTE]
> <Untested sketch | Run on <versions> with result <...>>. <Version caveats.>

```<language>
<small example with the important lines explained>
```

## 6. Failure scenarios

<!-- Include when failure behaviour is part of the topic. One to three scenarios. -->

### <Scenario name>

- **What happened?** <...>
- **Why?** <...>
- **What users or the business see:** <...>
- **Solutions:** <...>
- **Trade-offs:** <...>

## 7. Architectural decisions

<!-- For substantial concepts. Only the dimensions that change the decision. -->

| Use it when | Avoid it when | Alternatives |
|---|---|---|
| <...> | <...> | <...> |

<Reliability, ordering, scale, operations, security, observability or cost, whichever matter here.>

<!-- Optional. -->
**Common mistakes:** <one line, semicolon-separated: the gotchas not already covered as failure scenarios>

## 8. Try it yourself

<!-- Optional. Delete if running something teaches nothing beyond the text. -->

> [!NOTE]
> Status: <not run in this repository | run on <versions> on <date>>.

**You need:** <prerequisites and versions>

**Predict first:** <question to answer before running>

```bash
<commands>
```

**Observe:** <what to look for>. **Break it:** <one deliberate failure>. **Clean up:** <commands>.

## 9. Check your understanding

1. <conceptual question>
2. <"What happens if...?" question>
3. <troubleshooting question>
4. <design decision question>

Answers are at the end of this note.

## 10. Revision

**Remember**
- <essential point>
- <essential point>
- <essential point>

**Explain in 15 seconds:** "<concise, accurate answer>"

**Explain in 60 seconds:**
> "<what, why, how, and one important qualification or trade-off>"

**Likely follow-up questions:** <!-- Optional: interview-style, not repeating section 9. -->
- **<question>?** <one-line answer>
- **<question>?** <one-line answer>
- **<question>?** <one-line answer>

**Architect's takeaway:** <the key design decision or production lesson>

**Next topic:** [<next note>](<relative link>)

## 11. Verification and sources

> [!NOTE]
> Checked <YYYY-MM-DD> by <method>. Re-verify when you revisit this note.

- **Confirmed:** <fact> (<source>, <version>)
- **Assumptions and illustrations:** <what is illustrative, not verified behaviour>
- **To verify:** <anything not confirmed, especially version-specific defaults>
- **Decision rule:** <one line>

**Sources:** <official documentation, specifications, books>

## Answers

1. <answer with the reasoning>
2. <answer with the reasoning>
3. <answer with the reasoning>
4. <answer with the reasoning>

Back to [<overview>](<relative link>) · [Index](<relative link to notes/README.md>) · [Roadmap](<relative link to notes/roadmap.md>).
