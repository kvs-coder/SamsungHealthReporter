# 2. Suspend-first API

Date: 09.10.2026

## Status

Accepted

## Context

0.0.x blocked the calling thread on `HealthResultHolder.await()` and called `Looper.prepare()` in constructors, which froze or crashed on the main thread. HealthKitReporter uses completion handlers because HealthKit is callback-based; the Samsung Health Data SDK is suspend-based.

## Decision

Every call that reaches Samsung Health is a `suspend fun` that runs on the caller's coroutine context; the library never blocks a thread or picks a dispatcher. Continuous observation is a cold `Flow<ChangeSet>`. Errors are thrown as the sealed `SamsungHealthException`, with the SDK exception as `cause`.

## Consequences

- Kotlin callers use coroutines directly; Java or Flutter bridges wrap calls in a coroutine scope. A callback façade can be added later on top of the suspend API without changing it.
