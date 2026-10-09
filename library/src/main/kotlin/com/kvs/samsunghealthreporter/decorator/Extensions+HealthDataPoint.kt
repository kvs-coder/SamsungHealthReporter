package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.payload.Sample
import com.samsung.android.sdk.health.data.data.Field
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import java.time.Instant
import java.time.ZoneOffset

internal val HealthDataPoint.startTimestamp: Long get() = startTime.toEpochMilli()

internal val HealthDataPoint.endTimestamp: Long? get() = endTime?.toEpochMilli()

internal val HealthDataPoint.zoneOffsetSeconds: Int? get() = zoneOffset?.totalSeconds

internal val HealthDataPoint.source: DataSource?
    get() = dataSource?.let { DataSource(appId = it.appId, deviceId = it.deviceId) }

internal val HealthDataPoint.requiredEndTimestamp: Long
    get() = endTimestamp ?: throw SamsungHealthException.InvalidValue("Missing end time in data point $uid")

internal fun <T> HealthDataPoint.require(field: Field<T>): T =
    getValue(field) ?: throw SamsungHealthException.InvalidValue("Missing ${field.name} in data point $uid")

/** Starts a data point with the times, zone offset and client data id of [sample]. */
internal fun Sample.originalBuilder(): HealthDataPoint.Builder {
    val offset = zoneOffsetSeconds?.let { ZoneOffset.ofTotalSeconds(it) }
    val builder =
        HealthDataPoint
            .builder()
            .setStartTime(Instant.ofEpochMilli(startTimestamp), offset)
            .setClientDataId(clientDataId)
    endTimestamp?.let { builder.setEndTime(Instant.ofEpochMilli(it), offset) }
    return builder
}
