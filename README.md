# SamsungHealthReporter

## About

A Kotlin wrapper above the [Samsung Health Data SDK](https://developer.samsung.com/health/data/overview.html) for reading, writing and observing Samsung Health data.
The library turns Samsung Health data points into plain, `@Serializable` Kotlin data classes, so every result encodes to a simple JSON payload, and turns them back into Samsung Health data points when you write.
SDK types never appear in the public API: you work with `HealthType`, `HeartRate`, `TimeRange` and friends.

It is the Android sibling of [HealthKitReporter](https://github.com/kvs-coder/HealthKitReporter) and follows the same design: one `SamsungHealthReporter` with a **reader**, a **writer**, an **observer** and a **manager**.

> Version 1.0.0 moves from the deprecated Samsung Health SDK for Android (end of service 2028) to the Samsung Health Data SDK. See [Migrating from 0.0.x](#migrating-from-00x).

## Start

### Preparation

1. Download the **Samsung Health Data SDK** (1.1.0 or later) from the [Samsung Developer site](https://developer.samsung.com/health/data/overview.html#SDK-download). It needs a Samsung account, and Samsung's license doesn't allow redistributing it, so this library can't ship it.
2. Put `samsung-health-data-api.aar` into your app's `libs/` and add it with the plugins and dependencies the SDK needs:

```kotlin
// app/build.gradle.kts
plugins {
    id("org.jetbrains.kotlin.plugin.parcelize")
}

dependencies {
    implementation(files("libs/samsung-health-data-api.aar"))
    implementation("com.google.code.gson:gson:2.13.2")
}
```

3. To test, enable [developer mode](https://developer.samsung.com/health/data/guide/developer-mode.html) in the Samsung Health app. Reading works in developer mode; **writing, and distributing your app, needs Samsung partner approval** ([partner request](https://developer.samsung.com/health/data/overview.html)). Until you enter the access code Samsung issues with the partnership on the developer mode page, every write permission request fails with `SamsungHealthException.NotAuthorized` (SDK error 2003, `ERR_ACCESS_CONTROL`: "Permission is not allowed due to SDK policy"), so request read and write permissions separately. The Example app's *Request each permission alone* row lists which permissions your setup allows.

Samsung Health 6.30.2 or later must be installed. The SDK works on Samsung and non-Samsung phones with Android 10 or later, but not on emulators.

### Common usage

Create one `SamsungHealthReporter` and keep it. Every call is a `suspend` function that runs on your coroutine context, and every error is a `SamsungHealthException`.

```kotlin
if (!SamsungHealthReporter.isAvailable(context)) {
    // Samsung Health is not installed
}

val reporter = SamsungHealthReporter(context)

lifecycleScope.launch {
    try {
        val granted = reporter.manager.requestPermissions(
            activity,
            read = setOf(HealthType.HEART_RATE, HealthType.STEPS, HealthType.SLEEP),
            write = setOf(HealthType.HEART_RATE),
        )
        val heartRates = reporter.reader.read(HealthType.HEART_RATE, TimeRange.lastDays(7))
        heartRates.forEach { println(it.json) }
    } catch (exception: SamsungHealthException.Resolvable) {
        // Samsung Health must be installed, updated or set up: let it fix that
        reporter.manager.resolve(activity, exception)
    } catch (exception: SamsungHealthException) {
        println(exception.message)
    }
}
```

| Exception | Meaning |
| :--- | :--- |
| `NotAvailable` | Samsung Health or the feature isn't available |
| `NotAuthorized` | a permission is missing, or the data belongs to another app |
| `InvalidType` | the type doesn't support the operation (e.g. writing steps) |
| `InvalidValue` | an input is invalid, or Samsung Health rejected it |
| `Resolvable` | the user can fix it; pass it to `manager.resolve` |
| `Platform` | any other Samsung Health error, with its `errorCode` |

## Reading Data

`read` returns `Sample`s; check their type or filter with `filterIsInstance`. A `TimeRange` is in epoch milliseconds and is read in local time, the way Samsung Health shows data.

```kotlin
val reader = reporter.reader

val today = reader.read(HealthType.HEART_RATE, TimeRange.day())
    .filterIsInstance<HeartRate>()
    .map { it.harmonized.heartRate }

val latestTen = reader.read(
    HealthType.BLOOD_PRESSURE,
    TimeRange.lastDays(30),
    ordering = Ordering.DESCENDING,
    limit = 10,
    source = SourceFilter.SamsungHealth, // or SourceFilter.App("com.example"), SourceFilter.LocalDevice
)

val profile: UserProfile? = reader.userProfile()
```

Page by page:

```kotlin
var page = reader.readPage(HealthType.EXERCISE, TimeRange.lastDays(30), pageSize = 20)
while (true) {
    page.items.forEach { println(it.json) }
    val token = page.nextPageToken ?: break
    page = reader.readPage(HealthType.EXERCISE, TimeRange.lastDays(30), pageSize = 20, pageToken = token)
}
```

### Aggregates

Samsung Health computes totals, minimums, maximums and goals. Steps and the activity summary are only available as aggregates.

```kotlin
val stepsPerHour: List<Aggregate> = reader.aggregate(
    Aggregation.STEPS_TOTAL,
    TimeRange.day(),
    group = TimeGroup(TimeGroupUnit.HOURLY),
)
stepsPerHour.forEach { println("${it.startTimestamp}: ${it.value} ${it.unit}") }

val sleepPerNight = reader.aggregate(Aggregation.SLEEP_TOTAL_DURATION, TimeRange.lastDays(7), TimeGroup(TimeGroupUnit.DAILY))
val stepGoal = reader.aggregate(Aggregation.STEPS_GOAL_LAST, TimeRange.day()).firstOrNull()?.value
```

Durations are milliseconds and times of day seconds since midnight. Date-based aggregations (`supportsTimeGrouping == false`) group by day, week, month or year only.

## Writing Data

Only types with `isWritable` can be written. Writes of one type are atomic, and an app can only update and delete data it inserted.

```kotlin
val writer = reporter.writer
val now = System.currentTimeMillis()

val inserted = writer.insert(
    HeartRate(
        startTimestamp = now - 60_000,
        endTimestamp = now,
        harmonized = HeartRate.Harmonized(heartRate = 72f, min = 65f, max = 80f),
    ),
) as HeartRate
// A sample without clientDataId gets a random UUID, so you can update or delete it right away.

val updated = inserted.copy(harmonized = inserted.harmonized.copy(heartRate = 75f))
writer.update(updated)                                             // by uid, or clientDataId without one
writer.delete(updated)
writer.delete(HealthType.HEART_RATE, uids = listOf("uid-1", "uid-2"))
writer.deleteByClientDataIds(HealthType.WATER_INTAKE, listOf("my-id"))
```

Several types at once:

```kotlin
writer.insert(
    listOf(
        WaterIntake(now, harmonized = WaterIntake.Harmonized(amount = 250f)),
        BloodPressure(now, harmonized = BloodPressure.Harmonized(systolic = 120f, diastolic = 80f, mean = 93f)),
        Nutrition(now, harmonized = Nutrition.Harmonized(title = "Apple", mealType = MealType.MORNING_SNACK, calories = 95f)),
    ),
)
```

## Observing Data

Samsung Health records every insert, update and delete. Ask for the changes since your last sync and persist `until` for the next call:

```kotlin
val changes: ChangeSet = reporter.observer.changes(HealthType.SLEEP, since = lastSync)
changes.upserted.forEach { println(it.json) }
changes.deletedUids.forEach { println("deleted $it") }
lastSync = changes.until
```

Or poll while your screen is visible; the flow emits only non-empty change sets and stops with its collector:

```kotlin
lifecycleScope.launch {
    reporter.observer
        .observe(HealthType.HEART_RATE, since = System.currentTimeMillis(), interval = 30.seconds)
        .collect { changeSet -> println(changeSet.json) }
}
```

For background sync, schedule `changes` with WorkManager.

## Devices

```kotlin
val phone: Device = reporter.manager.localDevice()
val devices: List<Device> = reporter.manager.devices()           // phone, watch, ring, accessories
val source: Device? = reporter.manager.device(sample.dataSource!!.deviceId)
```

## JSON and maps

Every payload has `json`, and every payload type has `decode(json)`, `make(map)` and `collect(maps)`, so results cross into Flutter, React Native or a backend unchanged. A sample's JSON carries a `type` discriminator, so a list of mixed samples decodes with `Sample.serializer()`.

```kotlin
val json = heartRate.json
val same = HeartRate.decode(json)
val fromFlutter = HeartRate.make(mapOf("startTimestamp" to 0L, "endTimestamp" to 1L, "harmonized" to mapOf("heartRate" to 72)))
```

## Supported types

| `HealthType` | Payload | Read | Write | Observe | Aggregations |
| :--- | :--- | :---: | :---: | :---: | :--- |
| `HEART_RATE` | `HeartRate` | ✅ | ✅ | ✅ | `HEART_RATE_MIN`, `HEART_RATE_MAX` |
| `BLOOD_OXYGEN` | `BloodOxygen` | ✅ | ✅ | ✅ | |
| `BLOOD_PRESSURE` | `BloodPressure` | ✅ | ✅ | ✅ | |
| `BLOOD_GLUCOSE` | `BloodGlucose` | ✅ | ✅ | ✅ | |
| `BODY_COMPOSITION` | `BodyComposition` | ✅ | ✅ | ✅ | |
| `BODY_TEMPERATURE` | `BodyTemperature` | ✅ | ✅ | ✅ | |
| `SKIN_TEMPERATURE` | `SkinTemperature` | ✅ | | ✅ | |
| `EXERCISE` | `Exercise` | ✅ | ✅ | ✅ | `EXERCISE_TOTAL_CALORIES`, `EXERCISE_TOTAL_DURATION` |
| `EXERCISE_LOCATION` | — (permission for `Exercise.Session.route`) | | | | |
| `FLOORS_CLIMBED` | `FloorsClimbed` | ✅ | ✅ | ✅ | `FLOORS_CLIMBED_TOTAL` |
| `SLEEP` | `Sleep` | ✅ | ✅ | ✅ | `SLEEP_TOTAL_DURATION` |
| `NUTRITION` | `Nutrition` | ✅ | ✅ | ✅ | `NUTRITION_TOTAL_CALORIES` |
| `WATER_INTAKE` | `WaterIntake` | ✅ | ✅ | ✅ | `WATER_INTAKE_TOTAL` |
| `ENERGY_SCORE` | `EnergyScore` | ✅ | | ✅ | |
| `IRREGULAR_HEART_RHYTHM_NOTIFICATION` | `IrregularHeartRhythmNotification` | ✅ | | ✅ | |
| `SLEEP_APNEA` | `SleepApnea` | ✅ | | ✅ | |
| `USER_PROFILE` | `UserProfile` (`reader.userProfile()`) | ✅ | | | |
| `STEPS` | — | | | | `STEPS_TOTAL` |
| `ACTIVITY_SUMMARY` | — | | | | `ACTIVITY_SUMMARY_TOTAL_*` (4) |
| `STEPS_GOAL`, `SLEEP_GOAL`, `NUTRITION_GOAL`, `WATER_INTAKE_GOAL`, `ACTIVE_CALORIES_BURNED_GOAL`, `ACTIVE_TIME_GOAL` | — | | | | `*_LAST` |

Not supported, because the Samsung Health Data SDK doesn't offer them: electrocardiogram, albumin, ALP and the other lab values of the old SDK, and sleep score writes (Samsung Health computes it).

## Example

`app/` is a Jetpack Compose demo with one row per public library call: every manager call, a read of every readable type, every aggregation, paging, inserting, updating and deleting every writable type, one-shot changes for every observable type and a live observer. Tap a row to run it; tap a running observer to stop it.

Put the SDK AAR into `library/libs/` and run:

```bash
./gradlew :app:installDebug
```

## Migrating from 0.0.x

1.0.0 is a rewrite on the Samsung Health Data SDK:

| 0.0.x | 1.0.0 |
| :--- | :--- |
| `SamsungHealthReporter(context)` threw when Samsung Health was missing; `openConnection()` | `SamsungHealthReporter.isAvailable(context)`, then `SamsungHealthReporter(context)`; no explicit connection |
| `resolver.stepCount.read(start, end, filter, sort)` (blocking) | `reader.read(type, TimeRange, ordering, limit, source)` (`suspend`) |
| `resolver.stepCount.aggregate(..., Time.Group.DAILY, ...)` | `reader.aggregate(Aggregation.STEPS_TOTAL, range, TimeGroup(TimeGroupUnit.DAILY))` |
| `insert(StepCount(InsertResult(...)))` | `writer.insert(sample)`; steps are read-only in the Data SDK |
| `update(value, Filter)`, `delete(Filter)` | `writer.update(sample)`, `writer.delete(sample)` by uid / client data id |
| `observer.observe(type).subscribe(onNext, onError)` | `observer.changes(type, since)` / `observer.observe(type, since)` (`Flow`) |
| `manager.authorize(activity, read, write)` + listener | `manager.requestPermissions(activity, read, write)` returns the granted set |
| `readResult` / `aggregateResult` / `insertResult` with Gson `json` | one data class per type with `harmonized` values and `kotlinx.serialization` `json` |
| `SessionType`, `DiscreteType`, `HealthConstants` | `HealthType` |

Samsung's approvals for the old SDK don't carry over: request partner access for the Data SDK.

## Requirements

Android 10 (API 29) or later, Samsung Health 6.30.2 or later, Samsung Health Data SDK 1.1.0 or later, Java 17.

## Installation

Project's **settings.gradle.kts**

```kotlin
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}
```

App's **build.gradle.kts**

```kotlin
dependencies {
    implementation("com.github.kvs-coder:SamsungHealthReporter:0.0.6") // x-release-please-version
    implementation(files("libs/samsung-health-data-api.aar"))
}
```

## Development

The library compiles against the real SDK when `library/libs/samsung-health-data-api*.aar` exists, and otherwise against `samsung-health-data-stub/`, a stand-in written from the SDK's public API reference that is never published. Build, test and lint as described in [CONTRIBUTING.md](CONTRIBUTING.md) and [AGENTS.md](AGENTS.md); the architecture is documented with arc42 in [docs/arc42](docs/arc42/README.md).

## Releasing

Releases are automated by [release-please](https://github.com/googleapis/release-please): Conventional Commits on `master` keep a release PR open, and merging it tags `X.Y.Z`, which JitPack builds.

## Author

Victor Kachalov, victorkachalov@gmail.com

## License

SamsungHealthReporter is available under the MIT license. See the LICENSE file for more info.

## Sponsorship

If you think that my repo helped you to solve the issues you struggle with, please don't be shy and sponsor :-)
