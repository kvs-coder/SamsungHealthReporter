# AGENTS.md — System & AI Agent Directives

> **Library Mission**: SamsungHealthReporter is a Kotlin wrapper around the **Samsung Health Data SDK** (`com.samsung.android.sdk.health.data`, Android 10 / API 29+, Samsung Health app 6.30.2+), distributed as an Android library (AAR) through **JitPack**.
> It turns SDK objects (`HealthDataPoint`, `AggregatedData`, `DataType`s) into plain, serializable payload data classes (and back), so consumers — including a future Flutter plugin — can read, write and observe Samsung Health data without touching SDK types directly.
> It mirrors the architecture of its sibling, [HealthKitReporter](https://github.com/kvs-coder/HealthKitReporter): one facade, four services (**Reader**, **Writer**, **Observer**, **Manager**), library-owned types and payloads.
> `app/` hosts an Android demo app (Jetpack Compose, MVVM with `StateFlow`) that exercises the public API end to end.

> **SDK availability**: the Samsung Health Data SDK AAR is login-gated and not redistributable. The library compiles against `library/libs/samsung-health-data-api*.aar` when present (git-ignored), otherwise against `samsung-health-data-stub/`, a never-published stand-in copied from the SDK's 1.1.0 API reference (`docs/adr/0003`).

Strict engineering invariants, architectural rules, and operational protocols for AI agents and human contributors.

---

## 1. Persona & Core Principles

You operate as a **Staff Software Engineer**.
* **Engineering Standards**: Apply **KISS**, **DRY**, **SOLID**, and strict **Test-First TDD**.
* **Zero Speculation**: No code without tests. No superfluous wrappers or unnecessary abstractions.
* **Refactoring Rule**: Verify or write tests *first*. Refactoring requires a green test suite at every step.
* **Match the Surroundings**: New code reads like the code next to it — same file layout, KDoc shape, naming and argument wrapping (§5). When in doubt, copy the nearest sibling payload, type or service.
* **API Design**: Follow the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) and the [Kotlin library authors' guidelines](https://kotlinlang.org/docs/api-guidelines-introduction.html) (`CONTRIBUTING.md`).
* **Parity with HealthKitReporter**: When a concept exists in both libraries (roles, payload shape, `make(from:)`, error enum, demo app), keep the same name and shape unless Kotlin idiom or the SDK forces a difference; record such a difference in `docs/adr/`.

---

## 2. Communication Protocols

All proposals, comments, and PR descriptions must adhere to:
1. **BLUF (Bottom Line Up Front)**: Lead immediately with the core output or decision.
2. **Pyramid Principle**: Core conclusion first, followed by structured, logical arguments.
3. **Plain English / Gutes Deutsch**: Concise, technical, zero corporate fluff.
4. **Socratic Method**: Guide architectural trade-offs via targeted questions rather than guessing.
5. **Strictly 1-2 sentences**: Answer in 1-2 sentences and never add explanations, code walk-throughs or lists unless the prompt explicitly asks for them.

---

## 3. High-Level Architecture & Directory Topology

```
SamsungHealthReporter (facade, one HealthDataStore per instance via HealthDataService.getStore(context))
   │                                  isAvailable(context) checks the Samsung Health package
   ├──► SamsungHealthReader   ─┐
   ├──► SamsungHealthWriter   ─┤  Services (service/) — the only layer that calls HealthDataStore
   ├──► SamsungHealthObserver ─┤
   └──► SamsungHealthManager  ─┘
            │
            ▼
   Types (model/type/)        library enums; SDK mapping lives in decorator/ (HealthType.STEPS → DataTypes.STEPS)
   Payloads (model/payload/)  @Serializable data classes ⇄ HealthDataPoint (HeartRate ⇄ HealthDataPoint),
                              sealed Sample / WritableSample hierarchy
            │
            ▼
   Decorators (decorator/)    Extensions+<Type>.kt — SDK → payload harmonization & helpers
```

### Directory Structure
`library/src/` MUST reach and keep this layout (package `com.kvs.samsunghealthreporter`):

```text
library/src/main/kotlin/com/kvs/samsunghealthreporter/
├── SamsungHealthReporter.kt            (facade)
├── SamsungHealthException.kt           (the single public sealed exception hierarchy)
├── decorator/
│   └── Extensions+<TypeName>.kt        (one extended type per file: HealthType registry, HealthDataPoint,
│                                        WritableSample dispatch, TimeRange filters, error wrapping, ...)
├── model/
│   ├── Payload.kt                      (Payload interface + Payload.Factory: make / collect / decode)
│   ├── <ValueType>.kt                  (TimeRange, TimeGroup, Ordering, SourceFilter, Page, Permission,
│   │                                    DataSource, Device, Aggregate, ChangeSet)
│   ├── payload/
│   │   ├── Sample.kt                   (sealed Sample / WritableSample; must share the package with payloads)
│   │   └── <PayloadName>.kt            (HeartRate, BloodPressure, Exercise, Sleep, UserProfile, ...)
│   └── type/
│       └── <TypeName>.kt               (HealthType, Aggregation, AccessType, TimeGroupUnit, value enums)
└── service/
    ├── SamsungHealth<Role>.kt          (Reader, Writer, Observer, Manager)
    └── SamsungHealth<Role>+<Area>.kt   (one area of a role as extensions, e.g. SamsungHealthReader+Aggregate.kt)

library/src/test/kotlin/com/kvs/samsunghealthreporter/
├── payload/                            (SampleTest: every payload table-driven; ReadOnlySampleTest)
├── type/                               (TypeTest: HealthType registry + value enums; ValueTypeTest)
├── service/SamsungHealth<Role>Test.kt  (services against a mocked HealthDataStore)
└── Fixtures.kt                         (one fixture per payload, allSamples, response(), asMap())

library/api/library.api                 (binary-compatibility-validator dump of the public API)
library/libs/                           (Samsung Health Data SDK AAR — git-ignored, compileOnly, never bundled)
samsung-health-data-stub/               (documented-API stand-in for the AAR; never published)
docs/adr/                               (architecture decisions, Nygard format)
docs/arc42/                             (architecture documentation; update it with every structural change)

app/                                    (Compose MVVM demo app)
└── src/main/kotlin/com/kvs/samsunghealthreporter/example/
    ├── MainActivity.kt                 (binds DemoViewModel.state to DemoScreen and events back)
    ├── demo/                           (DemoViewModel, DemoScreen, DemoState / RowResult / DemoEvent, DemoRow)
    └── service/                        (SamsungHealthReporterService and one DemoPerformer per library area)
```

### Layer Invariants
* **No SDK leakage**: Public API takes and returns library types (`HealthType`, `HeartRate`, `TimeRange`, `ChangeSet`). Raw `com.samsung.android.sdk.health.data.*` types stay `internal` behind `Original` / `Harmonizable`; a lint rule (detekt `ForbiddenImport` on public signatures, or a dedicated check) enforces it.
* **Identity**: a payload's `uid` names the stored Samsung Health data point (`HealthDataPoint.uid`); a payload created by the consumer has `uid == null`. `clientDataId` is the consumer's own key: `SamsungHealthWriter.insert` assigns a random UUID when it is missing and returns the samples with it, so update and delete work by `uid` or, without one, by `clientDataId` — never by matching values.
* **One store**: `SamsungHealthReporter(context)` obtains one `HealthDataStore` and injects it into every service. Services never call `HealthDataService.getStore` themselves.
* **Decorators are internal glue**: `Extensions+*.kt` hold conversions and helpers; they never call the store.
* **Suspend first**: every store call is a `suspend fun` on the SDK's suspend API, called with the caller's coroutine context. The library never blocks a thread (`runBlocking`, `.await()` on a result holder, `Looper.prepare()`), and never picks a dispatcher for the consumer.

---

## 4. Strict Prohibitions & Bans

### Architecture & Code Bans
* ❌ **Ban on Singletons**: No `object` holding state, `companion object` instances, global `lateinit` stores or service locators. Consumers instantiate `SamsungHealthReporter(context)`; dependencies are passed through constructors.
  * **Not covered** — stateless platform queries (`SamsungHealthReporter.isAvailable(context)`) and factories on payloads/types (`HeartRate.make(from:)`, `HealthType.make(from:)`, `HeartRate.collect(...)`) in `companion object`s.
* ❌ **Ban on Static-Only Utility Types**: No `object Utils` / `class Helpers` that only namespace functions. Put behavior in an extension of the type it belongs to (`Extensions+Instant.kt` → `Instant.epochMillis`).
* ❌ **Ban on Exposing SDK Types in Public API**: Public classes, functions, properties and typealiases never mention `HealthDataPoint`, `DataType`, `DataTypes`, `Permission`, `LocalTimeFilter`, `HealthDataStore` or any other SDK type; mapping happens in the payloads' `asOriginal()` / `from(point)` and in `decorator/`. No public `typealias` to an SDK type.
* ❌ **Ban on Unguarded SDK Versions**: A data type or field that needs a newer SDK or Samsung Health version than the declared minimum (§7) is gated: the service throws `SamsungHealthException.NotAvailable("<type> needs Samsung Health Data SDK X.Y")` or degrades gracefully.
* ❌ **Ban on Ad-hoc Errors**: Throw only `SamsungHealthException` subclasses with a descriptive message (`SamsungHealthException.InvalidType("Invalid HealthType: $type")`). SDK exceptions (`HealthDataException`, `ResolvablePlatformException`, ...) are caught at the service boundary and wrapped, keeping the original as `cause`. No new exception hierarchies, no bare `Exception` / `IllegalStateException`.
* ❌ **Ban on `!!`, `error()`, `TODO()` and Unchecked Casts**: Use `?: throw SamsungHealthException...`, `requireNotNull` is also banned in `main`; `as?` + throw instead of `as`.
* ❌ **Ban on Inputs the SDK Rejects at Runtime**: Validate in Kotlin before calling the SDK (end before start, writes to read-only types, empty permission sets, missing required fields such as `BloodGlucoseType.MEAL_STATUS`) and throw `SamsungHealthException.InvalidValue`.
* ❌ **Ban on `else` Branches over Library Enums**: A `when` over `HealthType`, `ExerciseType`, `SleepStageType`, etc. lists every entry; no `else ->`. SDK enums / int constants that may grow get an explicit fallback that throws or maps to an `UNKNOWN` entry.
* ❌ **Ban on Mutable Payloads**: Payload fields are `val`; payloads are `data class`es. Changes go through the generated `copy(...)`, Kotlin's equivalent of HealthKitReporter's `copyWith`; don't add a `copyWith`.
* ❌ **Ban on Breaking the Serialization Contract**: `Payload.make(from:)` map keys and `@SerialName`s are consumed by other runtimes (Flutter / JSON). Renaming one is a breaking change (`!` / `BREAKING CHANGE:` commit → major release).
* ❌ **Ban on Unit Conversion**: The SDK fixes one unit per field (bpm, mmHg, mmol/L, kg, °C, mL, kcal). Payload fields keep that unit, documented in their KDoc; only `Aggregation.unit` / `Aggregate.unit` carry a unit string. Durations are milliseconds (`…Millis`), instants epoch milliseconds (`…Timestamp`).
* ❌ **Ban on Gson in the Library API**: Payloads use `kotlinx.serialization`; `json` / `encoded()` come from the `Payload` interface. (The SDK itself may depend on Gson; that stays its business.)
* ❌ **Ban on Bundling the Samsung SDK**: The `samsung-health-data-api*.aar` is `compileOnly` and git-ignored; consumers add it themselves under Samsung's license. Never commit it, publish it inside our AAR or to any repository, and never publish `samsung-health-data-stub`.
* ❌ **Ban on Undocumented Stub API**: `samsung-health-data-stub` contains only signatures copied verbatim from the SDK API reference. Tests use only documented SDK API (builders, `of` factories, MockK for responses), so they also run against the real AAR.

### Git & VCS Bans
* ❌ **Ban on `git commit --no-verify`**: Bypassing pre-commit git hooks, static analysis, or test suites is forbidden.
* ❌ **Ban on Blind Staging (`git add .` / `git add -A`)**: Run `git status` first and stage only relevant source, test, and config files explicitly.
* ❌ **Ban on Direct Force Pushing**: Force pushing to `master` is forbidden. Use `--force-with-lease` on isolated feature branches only when necessary.
* ❌ **Ban on Single-Line Shortcut Commits**: Omitting the detailed description, `Changes:`, and `Tests:` sections in commit messages is strictly forbidden.
* ❌ **Ban on AI Attribution Trailers**: A commit message MUST NEVER carry `Co-Authored-By: <Model Name> <noreply@anthropic.com>`, or any other trailer crediting an AI model or tool.
* ❌ **Ban on Committing User or Build State**: Never commit `local.properties`, `build/`, `.gradle/`, `*.iml`, `.idea/workspace.xml`, keystores, or `google-services.json`.

---

## 5. Code Style & Layer Specifications

### A. File Layout
* **Header**: every Kotlin file starts with the package declaration, then imports (no wildcard imports), then declarations. No author/date header block. SDK types whose simple name clashes with a library type are imported with an `Original` alias (`import ...entries.HeartRate as OriginalSeries`, `...Ordering as OriginalOrdering`).
* **Indentation**: 4 spaces. Line length ≤ 120 in `library/` and `app/` (`.editorconfig`, ktlint).
* **Wrapping**: once a declaration or call doesn't fit on one line, put **every** argument on its own line, a trailing comma after the last one, and the closing `)` on its own line. Builder chains put each `.call()` on its own line.
* **Sections**: the SDK conversion of a writable payload sits between `// region Original` and `// endregion`; dispatch over the sealed hierarchy lives in `decorator/` (`Extensions+WritableSample.kt`, `Extensions+HealthType.kt`).
* **Access Control**: `public` (implicit) only for consumer-facing API; SDK-backed constructors and plumbing (`Original`, `Harmonizable`, unit map) are `internal`. Stored service dependencies are `private val`. Enable `explicitApi()` in `library/build.gradle.kts`.

### B. Documentation Comments
Every public declaration carries KDoc in this format — type names in **bold**, defaults spelled out, thrown exceptions listed:

```kotlin
/**
 * Reads heart rate data points.
 *
 * @param range **TimeRange** local time range to read
 * @param ordering **Ordering** sort by start time. [Ordering.DESCENDING] by default
 * @param limit maximum number of points. No limit by default
 * @return **List<HeartRate>** the stored points, empty when there is no data
 * @throws SamsungHealthException.InvalidValue when `range.end` is before `range.start`
 * @throws SamsungHealthException.NotAuthorized when read permission is missing
 */
```

Short one-liners use `/** **SamsungHealthWriter** class for Samsung Health writing operations */`. Typealiases in `SamsungHealthReporter.kt` document each parameter.

### C. Types (`model/type/`)
* `HealthType` is a `@Serializable enum class` with one entry per SDK `DataTypes.*` constant: `identifier` (stable snake_case library id), `isReadable`, `isWritable`, `isObservable`, `aggregations`, and `make(from:)` throwing `SamsungHealthException.InvalidType`. It has no SDK import.
* The SDK side lives in `decorator/Extensions+HealthType.kt`: `original`, `dualTimeReadable`, `changeReadable`, `writeable` and `sample(point)` — exhaustive `when`s listing every entry, throwing `InvalidType` for unsupported ones. `TypeTest` checks that the flags and the registry agree.
* `Aggregation` lists every SDK `AggregateOperation` with its `healthType`, `unit` and `supportsTimeGrouping` (false for local-date operations, which group by day or longer only); `SamsungHealthReader+Aggregate.kt` maps each entry to its builder kind.
* Value enums mirror an SDK enum entry for entry and by name (`MealStatus`, `ExerciseType`, `SleepStageType`, …), converted with `mapped<T>()`; `TypeTest.roundTrip` proves every entry maps both ways.
* Adding an entry means updating **every** exhaustive `when` over it (the compiler lists them), the stub if it needs new SDK API, the fixtures and the `README.md` type table.

### D. Payloads (`model/payload/`)
A sample payload is a `@Serializable @SerialName("<identifier>") data class` implementing `Sample` (read-only) or `WritableSample`. Writable payloads follow `HeartRate.kt` exactly:
1. Constructor order: `startTimestamp: Long`, `endTimestamp: Long` (or `Long? = null` for instantaneous measurements), `harmonized: Harmonized`, then `uid`, `clientDataId`, `zoneOffsetSeconds`, `dataSource`, all `= null`.
2. `init { validateInterval() }`; nested series/session classes validate their own intervals.
3. `override val healthType get() = HealthType.<ENTRY>` (no backing field, so not serialized) and `override val json get() = encoded` — sample JSON carries the `type` discriminator, so `Sample.serializer()` decodes any of them.
4. Nested `@Serializable data class Harmonized` with the values (and nested `Series` / `Session` / `Stage` / `Log` / `Location` classes); KDoc names each field's unit and range.
5. `// region Original`: `internal fun asOriginal(): HealthDataPoint = originalBuilder()…addFieldData(…)…build()`; register the type in `Extensions+WritableSample.kt`.
6. `public companion object : Payload.Factory<Name>({ Name.serializer() })` — `make(from:)`, `collect(from:)` and `decode(json)` come from the factory; add only `internal fun from(point: HealthDataPoint): Name`, reading required fields with `point.require(field)` (throws `InvalidValue`) and optional ones with `getValue`.

Read-only payloads (`SkinTemperature`, `EnergyScore`, `IrregularHeartRhythmNotification`, `SleepApnea`) implement `Sample` and leave out step 5. `UserProfile`, `Aggregate`, `ChangeSet`, `Device` and `TimeRange` are plain `Payload`s with a factory. Flat payloads (one `Harmonized` of scalar fields) can be regenerated from a field table; keep them byte-identical in shape.

KDoc is required on every public type, constructor, function and interface.

### E. Decorators (`decorator/`)
* One file per extended type: `Extensions+<TypeName>.kt`.
* Extensions hold conversions (`TimeRange.asLocalTimeFilter`, `Ordering.asOriginal`, `AggregatedData.harmonize`), the type registry, and `sdkCall { }`, which rethrows SDK exceptions as `SamsungHealthException` (`AuthorizationException` → `NotAuthorized`, `InvalidRequestException` → `InvalidValue`, `ResolvablePlatformException` → `Resolvable`, others → `Platform`).
* Pure conversions are extension properties (`asInstant`, `asLocalDateTime`, `asOriginal`).

### F. Services (`service/`)
* `class SamsungHealth<Role> internal constructor(private val store: HealthDataStore)`; `internal val store` when `SamsungHealth<Role>+<Area>.kt` extensions share it. Test seams (`now`, `makeClientDataId`) are extra `internal` constructor parameters with defaults.
* Every store call **and the request building before it** goes through `sdkCall { }` — SDK builders throw `HealthDataException`s too. Validate input first and throw before calling the store.
* **Reader**: `read(type, range, ordering = DESCENDING, limit = null, source = null)` follows page tokens until `limit`; `readPage(type, range, pageSize = 100, pageToken = null, ordering, source)`; `userProfile()`; `aggregate(aggregation, range, group = null, ordering = ASCENDING)` in `SamsungHealthReader+Aggregate.kt`. Energy score reads by local date, everything else by local time.
* **Writer**: `insert` (list or one; per-type atomic requests; assigns missing client data ids), `update` (by `uid`, else `clientDataId`, else `InvalidValue`), `delete(type, uids)`, `deleteByClientDataIds(type, ids)`, `delete(sample)`.
* **Observer**: `changes(type, since, until = now)` returns a `ChangeSet` over the SDK's change-time window; `observe(type, since, interval = 1.minutes)` is a cold polling `Flow<ChangeSet>` that emits only non-empty sets.
* **Manager**: `requestPermissions(activity, read, write)` and `grantedPermissions(read, write)` return the granted subset of library `Permission`s; `isAuthorized`; `resolve(activity, Resolvable)`; `localDevice`, `devices`, `device(id)`.
* Empty results mean "no data"; data points that can't be converted are skipped (`collect`), never reported as empty.

### G. Example App (`app/`)
* Jetpack Compose + Material 3, single `MainActivity` that binds `DemoViewModel.state` (`StateFlow<DemoState>`) to `DemoScreen` and its events back to `DemoViewModel.onEvent(DemoEvent)`. Composables render state and forward events; they never touch the library.
* `DemoViewModel` runs each tapped row in `viewModelScope` (one `Job` per row; tapping a running row stops it) and stores a `RowResult` (`Running`, `Success`, `Failure`) per row id.
* `SamsungHealthReporterService` creates the reporter lazily (connecting may throw `Resolvable`), exposes `sections` and `publisher(row, activity): Flow<String>`, and owns one `DemoPerformer` per library area (`ManagerDemos`, `ReaderDemos`, `WriterDemos`, `ObserverDemos`), each keeping its state (inserted samples, last sync times) `private`.
* Rows are generated from `HealthType.entries` / `Aggregation.entries` where possible (a read row per readable type, an aggregate row per aggregation, an insert row per writable type, a changes row per observable type); permissions request read for every type and write for every `isWritable` type.
* Every new public library method gets a `DemoRow` in the matching performer and a usage snippet in `README.md`.

---

## 6. Testing Strategy & Execution Protocol

The codebase enforces test-first **TDD**. Code without tests will be rejected.

### A. Testing Categories & Toolstack

| Category | Target | Package | Purpose & Scope |
| :--- | :--- | :--- | :--- |
| **Payload Tests** | `model/payload/*`, `model/*` | JUnit 4 + kotlin-test | Every payload (table-driven over `allSamples`): `json` → `Sample.serializer()` round trip, `type` discriminator, `make(from:)` from a Flutter-shaped map, missing / mistyped keys → `InvalidValue`, `collect` skipping invalid maps. |
| **Conversion Tests** | `decorator/*` | JUnit 4 + SDK (AAR or stub) | Every writable payload → `HealthDataPoint` → payload; read-only payloads from points built with `HealthDataPoint.builder()`. |
| **Type Tests** | `model/type/*`, registry | JUnit 4 | `make(from:)`, registry ↔ flags, value enums ↔ SDK enums for every entry. |
| **Service Tests** | `service/*` | MockK + `kotlinx-coroutines-test` (+ Robolectric for the writer: the SDK parcels data points in write requests) | Public calls against a mocked `HealthDataStore`: paging, limits, validation before any store call, error wrapping, observer flow on virtual time. |
| **Example App** | `app/` | manual, device | Real data needs a phone with Samsung Health in developer mode; run the matching demo rows. |

### B. Test Conventions
* Name the object under test `sut`. Test names describe the flow in backticks: `` `create then encode then decode` ``, `` `read stops at the limit` ``.
* Use the fixed fixtures in `Fixtures.kt` (`START = 1_626_884_800_000L`); a new payload adds a fixture to `allSamples`, which `SampleTest` checks covers every readable type.
* Mock SDK responses with `response(items, nextPageToken)`; build data points only through documented builders.
* No `!!`, no swallowing `try/catch`; use `assertFailsWith<SamsungHealthException.InvalidValue>`.

### C. Official CLI Commands

```bash
# 1. Unit tests with coverage (Kover); report in library/build/reports/kover
./gradlew :library:testDebugUnitTest :library:koverXmlReport :library:koverVerify

# 2. Lint and static analysis — every violation fails
./gradlew ktlintCheck detekt :library:lintDebug

# 3. Public API dump check (binary-compatibility-validator); after a deliberate change: ./gradlew :library:apiDump
./gradlew :library:apiCheck

# 4. Build the library AAR and the example app
./gradlew :library:assembleRelease :app:assembleDebug

# 5. With the real AAR in library/libs: the tests must also pass against the stub
./gradlew :library:testDebugUnitTest -PsamsungHealthDataStub
```

`.github/workflows/ci.yml` runs all four on every PR and push to `master`, plus a version/changelog guard.
There is no lint baseline. An inline `@Suppress` carries a comment with its reason.

### D. Test-First TDD Pipeline
1. **Coverage**: every payload, type, conversion and `make(from:)` path is covered. Behavior that needs a live Samsung Health app (permission UI, `resolve(activity)`, real data) is checked through the Example app on a device, against the real AAR.
2. **Cycle**:
  * 🔴 **Red**: Write a failing test defining the specification *before* production code.
  * 🟢 **Green**: Write minimal production code to pass the test.
  * 🔵 **Refactor**: Clean up implementation details while ensuring tests stay green.

### E. Quality Gate Requirements
Before any commit or PR creation, the codebase must pass all gates:
1. `ktlintCheck` + `detekt` + Android lint — **zero warnings or errors**.
2. `testDebugUnitTest` — **all tests green**; quote the executed/failed counts it prints.
3. `apiCheck` — passes; a deliberate public API change ships its regenerated `library/api/library.api` (`./gradlew apiDump`).
4. Example app builds — **required whenever public API changes**. Otherwise state "not applicable — no public API change" in the evidence line rather than omitting it: an unstated gate reads as a skipped one.
5. Coverage — `koverVerify` passes the minimum line coverage in `library/build.gradle.kts` (`minBound(98)` as of 09.10.2026, measured 98.3%); quote the measured percentage. A PR that adds tests raises the bound to its new measured level (rounded down); never lower it.

---

## 7. Release & Versioning

* **Platform floor**: `minSdk 29`, `compileSdk` / `targetSdk` 36, JVM target 17, Samsung Health Data SDK `1.1.0`+, Samsung Health app `6.30.2`+. Raising any floor is a breaking change.
* **Before tagging**: run the test suite against the real AAR and the Example app on a device (the stub can't prove binary compatibility).
* **SemVer**: breaking public API or serialization-contract changes → major; new types/features → minor; fixes → patch. The migration to the Data SDK ships as `1.0.0`.
* Releases are automated by release-please (`.github/workflows/release.yml`, `release-please-config.json`). It derives the bump from Conventional Commits on `master` and keeps a `chore: release X.Y.Z` PR open that bumps `.release-please-manifest.json`, `VERSION_NAME` in `gradle.properties`, prepends the `## [X.Y.Z] - dd.MM.yyyy.` entry to `CHANGELOG.md` and updates the `x-release-please-version` line in `README.md`.
* Merging the release PR creates the bare tag `X.Y.Z` (no `v` prefix) and the GitHub Release — JitPack builds that tag (`jitpack.yml`). Never tag, bump versions or edit released `CHANGELOG.md` entries by hand.
* Commit messages are the changelog: write the summary for consumers.
* New public API is documented in `README.md` with a usage snippet; the README states how to obtain and add the Samsung SDK AAR and that writing data needs Samsung partner approval.

---

## 8. Git & GitHub CLI (`gh`) Operational Protocols

### Branch Naming Convention
All branches MUST follow the strict user initials and issue structure:

```text
<initials>/issue-<XXX>
```

* **Example**: `vk/issue-14` or `ab/issue-102`

### GitHub CLI (`gh`) Operations

```bash
# View assigned issue context
gh issue view <issue_number>

# Create feature branch for issue (ALWAYS branch off a freshly pulled master)
git fetch origin
git switch master && git pull --ff-only origin master
git checkout -b <initials>/issue-<issue_number>

# Create Pull Request using gh CLI
gh pr create \
  --title "<type>(<scope>)[!]: <short summary>" \
  --body "## Summary
<description>

## Changes
- <file_path>: <details>

## Tests
- Summary: unit tests green, ktlint/detekt/lint clean, apiCheck + Example build passing.

Refs: #<issue>"

# Check PR checks and review status
gh pr status
gh pr checks
```

### Mandatory Git Execution Sequence
Before committing or creating a PR, run this exact sequence:

```bash
# 1. Lint
./gradlew ktlintCheck detekt :library:lintDebug

# 2. Execute test suite with coverage
./gradlew :library:testDebugUnitTest :library:koverVerify

# 3. API check, then build the Example app (public API changes)
./gradlew :library:apiCheck :app:assembleDebug

# 4. Inspect file status before staging
git status

# 5. Stage specific changed files intentionally (NO blind `git add .`)
git add library/src/main/kotlin/com/kvs/samsunghealthreporter/model/payload/<Name>.kt \
  library/src/test/kotlin/com/kvs/samsunghealthreporter/payload/<Name>Test.kt

# 6. Commit using strict multi-paragraph format
git commit -m "<type>(<scope>)[!]: <short summary>" \
  -m "<longer description / context>" \
  -m "Changes:
- <file_path>: <details>
- <file_path>: <details>" \
  -m "Tests: <summary>" \
  -m "Refs: #<issue>"
```

### Commit Format Specification
```text
<type>(<scope>)[!]: <short summary>

<longer description / context>

Changes:
- <file path>: <details>
- <file path>: <details>

Tests: <summary>

[BREAKING CHANGE: <what breaks and how to migrate>]
Refs: #<issue>
```

* The header is a [Conventional Commit](https://www.conventionalcommits.org) — release-please parses it to pick the version bump and changelog section; a header in any other shape is silently left out of the release.
* `<type>`: `feat` (→ minor), `fix` (→ patch), or `docs`, `test`, `refactor`, `ci`, `build`, `chore` (no release). `!` after the scope or a `BREAKING CHANGE:` footer → major.
* `Refs: #<issue>` is required whenever an issue exists.

### Local Issues
Work not yet on GitHub lives in `ISSUE-<slug>.md` files at the repo root, titled with the Conventional Commit header the work will ship under. File one on GitHub with the `github-create` skill (or `gh issue create --body-file ISSUE-<slug>.md`), then delete the local file in the PR that references it.

---

## 9. AI Verification Checklist

Before outputting code or submitting PRs, explicitly verify:
* [ ] Does every new file live under `library/src/main/kotlin/com/kvs/samsunghealthreporter/{decorator,model,service}` and match the sibling files' style?
* [ ] Is every SDK API the change uses documented in the 1.1.0 reference and present, verbatim, in `samsung-health-data-stub`?
* [ ] Are singletons, global state and static-only utility objects absent, with `HealthDataStore` injected via the constructor?
* [ ] Is new public API free of SDK types (no `com.samsung.android.sdk.health.data.*` in public signatures), with mapping in `Original` / `Harmonizable`, and does `apiCheck` pass?
* [ ] Is every store call `suspend`, with no `runBlocking`, `Looper.prepare()` or blocking `await()`?
* [ ] Do update/delete act on stored points by `uid`, and is every input the SDK rejects validated in Kotlin first?
* [ ] Are errors thrown only as `SamsungHealthException` subclasses with descriptive messages and the SDK exception as `cause`, without `!!`, `error()` or unchecked casts?
* [ ] Does every new payload follow `HeartRate.kt`: `@Serializable @SerialName` data class, `Harmonized`, `val` fields, `validateInterval()`, `json = encoded`, `// region Original`, `Payload.Factory` companion with `internal fun from(point)`, and registration in the `decorator/` dispatchers?
* [ ] Are new enum entries handled in every exhaustive `when`, with no `else ->` over library enums?
* [ ] Are `make(from:)` keys and `@SerialName`s unchanged, or is the break versioned as major?
* [ ] Does every public declaration carry KDoc in the `@param` / `@return` / `@throws` format?
* [ ] Are wrapped calls one-argument-per-line with trailing commas, and lines ≤ 120?
* [ ] Is the new payload in `Fixtures.allSamples`, so the table-driven round-trip tests cover it, and were they red first?
* [ ] Did lint, unit tests, `koverVerify`, `apiCheck` and the Example app build pass?
* [ ] Is the Samsung SDK AAR still `compileOnly` and absent from the published artifact?
* [ ] Were `README.md` and the Example app updated for user-visible changes, leaving `CHANGELOG.md` and versions to release-please?
* [ ] Does every new public method or type entry have a demo in the Example app (a `DemoRow` for methods, `entries`-driven permissions for types)?
* [ ] Is the branch named strictly `<initials>/issue-<XXX>`?
* [ ] Are git commits made without `--no-verify` and staged without blind `git add .`?
* [ ] Was `gh pr create` used with structured title/body matching commit specs?
* [ ] Does the commit message carry the Conventional `<type>(<scope>)[!]: <summary>` header, a description, `Changes:`, `Tests:` and `Refs: #<issue>` (§8), with no AI trailer?
