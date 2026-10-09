# 3. Compile against the SDK AAR, or a documented-API stub

Date: 09.10.2026

## Status

Accepted

## Context

Samsung distributes the Samsung Health Data SDK only as an AAR behind a Samsung account login, and its license doesn't allow redistributing it. It isn't on Maven Central. CI, JitPack and contributors without the AAR still have to build and test the library.

## Decision

- The library declares the AAR `compileOnly`; apps add it themselves. It is git-ignored and never published.
- When `library/libs/samsung-health-data-api*.aar` is missing, the build uses the `samsung-health-data-stub` module instead: the subset of the SDK's public API the library uses, copied from the 1.1.0 API reference, with just enough behavior for JVM unit tests. It is never published either.
- Tests use only documented SDK API (builders, `of` factories, MockK for responses), so they run against the real AAR unchanged.
- CI builds against the real AAR, cloned from the private repository `kvs-coder/samsung-health-sdk` with the read-only deploy key in the `SAMSUNG_HEALTH_SDK_DEPLOY_KEY` secret (a base64 secret would exceed GitHub's 48 KB secret limit), and against the stub when the key is unavailable (fork PRs).

## Verification (09.10.2026)

Built and tested against the real `samsung-health-data-api-1.1.0.aar`: every library call compiled unchanged. The real SDK differed from the reference in ways the stub now mirrors:

- `DataResponse<T : Parcelable>`; data points, aggregates and changes are `Parcelable`.
- `PredefinedExerciseType` starts with `UNDEFINED`, which the reference omits.
- `HealthDataPoint.Builder.build()` rejects a point without field data (`InvalidRequestException` 1001), and builders throw SDK exceptions, so services build requests inside `sdkCall { }`.
- A point built without a zone offset gets the device's offset.
- Write request builders parcel their data points, so `SamsungHealthWriterTest` runs under Robolectric; the SDK also needs `kotlin-parcelize-runtime` and Gson at runtime.

## Consequences

- A stub signature that differs from the real SDK would compile here but fail at runtime in apps. CI runs the tests against the real AAR (private repository) and the stub (`-PsamsungHealthDataStub`); releases are also checked on a device through the Example app before tagging.
- Using a new SDK API means adding it to the stub, verbatim from the reference.
