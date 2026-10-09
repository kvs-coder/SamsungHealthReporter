# 10. Quality Requirements

## 10.1 Quality Tree

| Quality | Sub-quality | Scenario |
| :--- | :--- | :--- |
| Maintainability | Encapsulation | Q1, Q2 |
| Maintainability | Changeability | Q3 |
| Compatibility | Contract stability | Q4 |
| Reliability | Fault handling | Q5, Q6 |
| Performance | Responsiveness | Q7 |
| Testability | Off-device verification | Q8, Q9 |
| Usability (for developers) | Learnability | Q10 |

## 10.2 Quality Scenarios

| Id | Stimulus | Response | Measure |
| :--- | :--- | :--- | :--- |
| Q1 | A contributor exposes an SDK type in a public signature | CI fails | `apiCheck` diff on `library/api/library.api` |
| Q2 | Samsung changes an SDK signature | Only `decorator/`, payload conversions and the stub change | No public API change |
| Q3 | Samsung adds a data type | One `HealthType` entry; the compiler lists every `when` to extend | Fixture added to `allSamples`; tests stay green |
| Q4 | A Flutter plugin sends a map built from an older JSON | `make(from:)` decodes it | No renamed keys without a major release |
| Q5 | Samsung Health is missing, outdated or rejects a request | A typed `SamsungHealthException` is thrown; the app does not crash | No raw `HealthDataException` escapes (`sdkCall` around every build and call) |
| Q6 | The app lacks partner approval and requests write permissions | `NotAuthorized` with SDK code 2003 in `cause`; reads still work when requested separately | Verified on a Pixel 7a |
| Q7 | A read of a large range runs on the main dispatcher | The UI stays responsive | Suspend calls only; no `runBlocking`, `Looper.prepare()` or blocking waits |
| Q8 | A contributor without the SDK runs the build | Everything compiles and tests run against the stub | `./gradlew :library:testDebugUnitTest` without `library/libs` |
| Q9 | A PR changes behavior | Unit tests run against the real SDK and the stub | 97 tests; Kover gates ≥ 99% lines, ≥ 74% branches |
| Q10 | A developer looks for how to call an API | README snippet and an Example app row exist | One demo row per public method |
