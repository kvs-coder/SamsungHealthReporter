# 1. Build on the Samsung Health Data SDK

Date: 09.10.2026

## Status

Accepted

## Context

Versions 0.0.x wrapped the Samsung Health SDK for Android 1.4.0 (`com.samsung.android.sdk.healthdata`). Samsung deprecated it on 31.07.2025; it reaches end of service in 2028, and new partner approvals are only granted for its successor, the Samsung Health Data SDK (`com.samsung.android.sdk.health.data`, 1.1.0 from 12.03.2026). The successor has suspend APIs, works on non-Samsung phones, adds data types (activity summary, energy score, skin temperature, goals, irregular heart rhythm, sleep apnea) and write access for blood oxygen, exercise, floors climbed and sleep. It drops the lab values (albumin, ALP, …) and the electrocardiogram, and makes steps read-only.

## Decision

Rebuild the library on the Samsung Health Data SDK as 1.0.0, with the HealthKitReporter architecture: one `SamsungHealthReporter` facade sharing one `HealthDataStore` between `SamsungHealthReader`, `SamsungHealthWriter`, `SamsungHealthObserver` and `SamsungHealthManager`; library-owned `HealthType`s and `@Serializable` payloads; no SDK type in the public API (checked by the binary-compatibility validator's `library.api`).

## Consequences

- 1.0.0 is a breaking release; README lists the migration.
- Types the Data SDK doesn't offer are dropped.
- Observing changes is polling over a change-time window (`readChanges`) instead of push callbacks; consumers persist the last sync time, like HealthKit anchors.
