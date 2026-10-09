package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.model.Aggregate
import com.kvs.samsunghealthreporter.model.type.Aggregation
import com.samsung.android.sdk.health.data.data.AggregatedData
import java.time.Duration
import java.time.LocalTime

/** Durations become milliseconds and times of day seconds since midnight. */
private val Any?.aggregateValue: Double?
    get() =
        when (this) {
            null -> null
            is Number -> toDouble()
            is Duration -> toMillis().toDouble()
            is LocalTime -> toSecondOfDay().toDouble()
            else -> throw SamsungHealthException.InvalidValue("Unsupported aggregate value: $this")
        }

internal fun <T : Any> AggregatedData<T>.harmonize(aggregation: Aggregation): Aggregate =
    Aggregate(
        aggregation = aggregation,
        startTimestamp = startTime.toEpochMilli(),
        endTimestamp = endTime.toEpochMilli(),
        value = value.aggregateValue,
    )
