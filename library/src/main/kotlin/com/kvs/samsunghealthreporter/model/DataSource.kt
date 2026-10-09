package com.kvs.samsunghealthreporter.model

import kotlinx.serialization.Serializable

/**
 * **DataSource** the app and device that wrote a data point.
 *
 * @param appId **String** package name of the writing app; `com.sec.android.app.shealth` for Samsung Health
 * @param deviceId **String** Samsung Health device id
 */
@Serializable
public data class DataSource(
    val appId: String,
    val deviceId: String,
)
