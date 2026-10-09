package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.model.type.TimeGroupUnit
import kotlinx.serialization.Serializable

/**
 * **TimeGroup** groups aggregate results into buckets of [multiplier] × [unit] in local time.
 *
 * @param unit **TimeGroupUnit** the bucket unit
 * @param multiplier **Int** units per bucket, at least 1. 1 by default
 * @throws SamsungHealthException.InvalidValue when [multiplier] is less than 1
 */
@Serializable
public data class TimeGroup(
    val unit: TimeGroupUnit,
    val multiplier: Int = 1,
) {
    init {
        if (multiplier < 1) throw SamsungHealthException.InvalidValue("Invalid TimeGroup multiplier: $multiplier")
    }
}
