# 11. Risks and Technical Debt

## 11.1 Risks

| Risk | Impact | Mitigation |
| :--- | :--- | :--- |
| **Stub drift**: release artifacts (JitPack) compile against the stub, not the real SDK | A signature the stub gets wrong compiles but fails at runtime in apps (`NoSuchMethodError`) | CI compiles and tests against the real AAR on every PR; the stub copies the API reference verbatim; differences found so far are recorded in ADR 0003 |
| **Partner approval** | Writes (and distribution) are impossible without Samsung's approval; write paths are verified only with mocks and Robolectric, never on a device | Request partnership; until then the Example app's diagnostic rows document the 2003 rejection |
| **No real data verified** | Read and aggregate conversions were exercised on a device with an empty Samsung Health history | Re-run the Example app with recorded steps, heart rate, sleep and exercise |
| **SDK documentation gaps** | The 1.1.0 reference omitted `PredefinedExerciseType.UNDEFINED` and the `Parcelable` bound of `DataResponse`; others may exist | Enum mirrors and tests run against the real AAR |
| **`LocalDateFilter` end semantics** | If its end date is inclusive, date-based reads include one extra day | Confirm on a device with data; adjust `TimeRange.asLocalDateFilter` |
| **Polling observer** | `observe` reads changes every interval while collected; battery and IPC cost | Default interval 1 minute; consumers schedule `changes` with WorkManager for background sync |
| **Private SDK repository** | CI depends on `kvs-coder/samsung-health-sdk` and its deploy key | Fork PRs fall back to the stub; the key is read-only and scoped to that repository |
| **Samsung SDK churn** | Samsung renamed types and changed nullability between releases | Encapsulation (§8.6) confines changes to `decorator/` |

## 11.2 Technical Debt

| Debt | Where | Proposed fix |
| :--- | :--- | :--- |
| Sleep associated reads (`associatedReadRequestBuilder`), device registration and swimming logs of the SDK are not wrapped | `service/`, `Exercise` payload | Add when a consumer needs them |
| `Exercise` sessions drop `swimmingLog` | `model/payload/Exercise.kt` | Model `SwimmingLog` / `SwimmingInterval` |
| No callback / `CompletableFuture` façade for Java and Flutter channel code | `service/` | Thin layer over the suspend API (ADR 0002) |
| CI actions still target Node.js 20 (deprecation annotations) | `.github/workflows/` | Bump `actions/checkout`, `setup-java`, `upload-artifact`, `gradle/actions` to Node 24 versions |
