# 1. Introduction and Goals

## 1.1 Requirements Overview

SamsungHealthReporter is a Kotlin library that lets Android apps read, write and observe Samsung Health data
without touching the Samsung Health Data SDK directly. It is the Android sibling of
[HealthKitReporter](https://github.com/kvs-coder/HealthKitReporter) and follows the same shape: one facade, four
services (**reader**, **writer**, **observer**, **manager**), library-owned types and serializable payloads.

| Capability | Provided by |
| :--- | :--- |
| Check that Samsung Health is installed; connect | `SamsungHealthReporter.isAvailable`, `SamsungHealthReporter(context)` |
| Request and inspect permissions; resolve setup problems; list devices | `SamsungHealthManager` |
| Read data points of 15 data types, page by page; read the user profile | `SamsungHealthReader.read`, `readPage`, `userProfile` |
| Aggregate (totals, min/max, goals) with local-time grouping | `SamsungHealthReader.aggregate` (20 `Aggregation`s) |
| Insert, update and delete data of 11 writable types | `SamsungHealthWriter` |
| Read changes since the last sync, once or as a polling `Flow` | `SamsungHealthObserver.changes`, `observe` |
| Exchange every result as JSON or a plain map (Flutter, backends) | `Payload.json`, `Payload.Factory.make` / `collect` / `decode` |

Version 1.0.0 replaces the 0.0.x library, which wrapped the deprecated Samsung Health SDK for Android 1.4.0
(ADR 0001).

## 1.2 Quality Goals

| Priority | Quality goal | Motivation |
| :---: | :--- | :--- |
| 1 | **Encapsulation of the SDK** | Consumers never see Samsung SDK types; the SDK can change (it already renamed types and changed nullability between 1.0.0 betas) without breaking consumers. Enforced by `library/api/library.api`. |
| 2 | **Contract stability** | Payload JSON and map keys are consumed by other runtimes (Flutter, backends); renaming one is a major release. |
| 3 | **Safety** | No blocking calls, no crashes from SDK exceptions: every failure is a typed `SamsungHealthException`. |
| 4 | **Testability without a phone** | 97 JVM unit tests run against the real SDK AAR and against a documented-API stub; line coverage ≥ 99%, branch coverage ≥ 74%. |
| 5 | **Parity with HealthKitReporter** | Cross-platform consumers (the Flutter plugins) map one mental model onto both platforms. |

## 1.3 Stakeholders

| Role | Expectations |
| :--- | :--- |
| App developers (Kotlin) | Small, documented API; coroutines; clear errors; a demo of every call. |
| Cross-platform plugin maintainers (Flutter) | Stable JSON / map contract; same concepts as HealthKitReporter. |
| Maintainer (kvs-coder) | Automated quality gates and releases; conventions in `AGENTS.md`. |
| AI coding agents | Unambiguous rules (`AGENTS.md` / `CLAUDE.md`) and an accurate architecture description (this document). |
| Samsung | Apps follow the partner process: read in developer mode, write and distribute only with approval. |
| End users | Their health data is only read or written with their explicit permission in Samsung Health. |
