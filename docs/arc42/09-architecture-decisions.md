# 9. Architecture Decisions

Decisions are recorded as ADRs in [`docs/adr/`](../adr/) (Nygard format).

| ADR | Decision | Status |
| :--- | :--- | :--- |
| [0001](../adr/0001-samsung-health-data-sdk.md) | Build on the Samsung Health Data SDK with the HealthKitReporter architecture; release as 1.0.0 | Accepted |
| [0002](../adr/0002-suspend-first-api.md) | Suspend-first API, cold `Flow` for observation, sealed `SamsungHealthException` | Accepted |
| [0003](../adr/0003-sdk-aar-compile-only-with-stub.md) | `compileOnly` SDK AAR with a documented-API stub fallback; real-AAR verification and CI provisioning from a private repository | Accepted |

Smaller decisions documented in `AGENTS.md`: data class `copy` instead of HealthKitReporter's `copyWith`; enum
mirroring by entry name; JUnit 4 + MockK + Robolectric; Compose MVVM for the Example app.
