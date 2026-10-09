# 4. Solution Strategy

| Goal / constraint | Approach | Where |
| :--- | :--- | :--- |
| Encapsulate the SDK | Library-owned `HealthType`, value enums and `@Serializable` payloads; SDK mapping only in `decorator/` and `internal` members; public API dump checked in CI | §5, §8.4, ADR 0001 |
| One mental model with HealthKitReporter | Facade + four services sharing one injected `HealthDataStore`; payloads with `harmonized` values; `make(from:)` factories | §5.1, ADR 0001 |
| Safety | Suspend-first API on the caller's coroutine context; every SDK call and request build inside `sdkCall { }`, which rethrows a sealed `SamsungHealthException`; input validated before any SDK call | §6, §8.5, ADR 0002 |
| Non-redistributable, login-gated SDK | `compileOnly` AAR; documented-API stub module as fallback; CI fetches the AAR from a private repository and tests against both | §7, ADR 0003 |
| Cross-runtime contract | kotlinx.serialization; sample JSON carries a `type` discriminator; maps decoded through the same serializers | §8.4 |
| Observing without push callbacks | The SDK records changes per time window; the observer exposes them as a `ChangeSet` and a polling cold `Flow`; consumers persist the sync time like HealthKit anchors | §6.5 |
| Exhaustiveness | `when` over every `HealthType` / `Aggregation` entry, no `else`; tests prove flags, registry and enum mirrors agree | §8.6 |
| Verifiable without device data | MockK-mocked store for services, documented SDK builders for conversions, Robolectric where the SDK parcels; a Compose demo with one row per public call for device checks | §8.8 |
