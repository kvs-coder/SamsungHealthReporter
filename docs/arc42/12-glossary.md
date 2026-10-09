# 12. Glossary

| Term | Definition |
| :--- | :--- |
| Samsung Health | Samsung's health app (`com.sec.android.app.shealth`); stores the data and shows permission screens. |
| Samsung Health Data SDK | Samsung's Android SDK (`com.samsung.android.sdk.health.data`, AAR) for third-party access; successor of the deprecated Samsung Health SDK for Android. |
| SDK stub | `samsung-health-data-stub/`: compile-time stand-in for the SDK, written from its API reference. |
| Developer mode | Samsung Health setting that lets unapproved apps read data for testing. |
| Access code | Code from Samsung's partnership process, entered in developer mode to allow writes. |
| Partner approval | Samsung's registration of an app (package + signing key) for writing and distribution. |
| `HealthType` | Library enum naming one Samsung Health data type (e.g. `HEART_RATE`). |
| `Aggregation` | Library enum naming one SDK aggregate operation (e.g. `STEPS_TOTAL`). |
| Sample | A stored data point as a library payload (`Sample`); `WritableSample` if apps may write it. |
| Harmonized | The nested value part of a sample payload, in Samsung Health's units. |
| Payload | A serializable library value with `json` and a `Payload.Factory` (`make`, `collect`, `decode`). |
| `HealthDataPoint` | The SDK's data point; converted to a sample by `from(point)` and back by `asOriginal()`. |
| `uid` | Samsung Health's id of a stored data point. |
| `clientDataId` | The writing app's own id of a data point. |
| `DataSource` | App package and device id that wrote a data point. |
| `ChangeSet` | Upserted samples and deleted uids of one type in a change-time window. |
| Sync time | The `ChangeSet.until` a consumer persists for the next `changes` call. |
| `TimeRange` | End-exclusive epoch-millisecond range, read in local time. |
| `TimeGroup` | Bucket size for aggregates (`unit` × `multiplier`). |
| `Resolvable` | Error the user can fix through Samsung Health (install, update, set up). |
| Error 2003 | `ERR_ACCESS_CONTROL`: the app is not allowed to use the feature, e.g. writing without partner approval. |
| ADR | Architecture Decision Record in `docs/adr/`. |
