package com.samsung.android.sdk.health.data.error

import android.app.Activity

open class HealthDataException
    @JvmOverloads
    constructor(
        val errorCode: Int?,
        val errorMessage: String,
        cause: Throwable? = null,
    ) : RuntimeException(errorMessage, cause)

class AuthorizationException(
    errorCode: Int?,
    errorMessage: String,
) : HealthDataException(errorCode, errorMessage)

class InvalidRequestException(
    errorCode: Int,
    errorMessage: String,
) : HealthDataException(errorCode, errorMessage)

class PlatformInternalException
    @JvmOverloads
    constructor(
        errorCode: Int?,
        errorMessage: String,
        cause: Throwable? = null,
    ) : HealthDataException(errorCode, errorMessage, cause)

class ResolvablePlatformException(
    errorCode: Int?,
    errorMessage: String,
    val hasResolution: Boolean,
) : HealthDataException(errorCode, errorMessage) {
    fun resolve(activity: Activity): Unit = throw UnsupportedOperationException("SDK stub")
}
