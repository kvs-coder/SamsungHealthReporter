package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/** **TimeGroupUnit** the unit aggregate results are grouped by, in local time */
@Serializable
public enum class TimeGroupUnit {
    MINUTELY,
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
}
