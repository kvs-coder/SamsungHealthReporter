package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.samsung.android.sdk.health.data.error.AuthorizationException
import com.samsung.android.sdk.health.data.error.HealthDataException
import com.samsung.android.sdk.health.data.error.InvalidRequestException
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException

internal val HealthDataException.wrapped: SamsungHealthException
    get() =
        when (this) {
            is AuthorizationException -> SamsungHealthException.NotAuthorized(errorMessage, this)
            is InvalidRequestException -> SamsungHealthException.InvalidValue(errorMessage, this)
            is ResolvablePlatformException -> SamsungHealthException.Resolvable(errorMessage, this)
            else -> SamsungHealthException.Platform(errorMessage, errorCode, this)
        }

/** Runs a Samsung Health Data SDK call, rethrowing its exceptions as **SamsungHealthException**. */
internal suspend fun <T> sdkCall(block: suspend () -> T): T =
    try {
        block()
    } catch (exception: HealthDataException) {
        throw exception.wrapped
    }
