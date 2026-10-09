package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.model.TimeGroup
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.type.TimeGroupUnit
import com.samsung.android.sdk.health.data.request.InstantTimeFilter
import com.samsung.android.sdk.health.data.request.LocalDateFilter
import com.samsung.android.sdk.health.data.request.LocalDateGroup
import com.samsung.android.sdk.health.data.request.LocalDateGroupUnit
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.LocalTimeGroup
import com.samsung.android.sdk.health.data.request.LocalTimeGroupUnit
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

internal val Long.asLocalDateTime: LocalDateTime
    get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

internal val Long.asLocalDate: LocalDate get() = asLocalDateTime.toLocalDate()

internal val TimeRange.asLocalTimeFilter: LocalTimeFilter
    get() = LocalTimeFilter.of(start.asLocalDateTime, end.asLocalDateTime)

internal val TimeRange.asInstantTimeFilter: InstantTimeFilter
    get() = InstantTimeFilter.of(Instant.ofEpochMilli(start), Instant.ofEpochMilli(end))

/** The local dates the range touches; the end date is the date of the range's last millisecond. */
internal val TimeRange.asLocalDateFilter: LocalDateFilter
    get() = LocalDateFilter.of(start.asLocalDate, (end - 1).asLocalDate.plusDays(1))

internal val TimeGroup.asLocalTimeGroup: LocalTimeGroup
    get() =
        LocalTimeGroup.of(
            when (unit) {
                TimeGroupUnit.MINUTELY -> LocalTimeGroupUnit.MINUTELY
                TimeGroupUnit.HOURLY -> LocalTimeGroupUnit.HOURLY
                TimeGroupUnit.DAILY -> LocalTimeGroupUnit.DAILY
                TimeGroupUnit.WEEKLY -> LocalTimeGroupUnit.WEEKLY
                TimeGroupUnit.MONTHLY -> LocalTimeGroupUnit.MONTHLY
                TimeGroupUnit.YEARLY -> LocalTimeGroupUnit.YEARLY
            },
            multiplier,
        )

internal val TimeGroup.asLocalDateGroup: LocalDateGroup
    get() =
        LocalDateGroup.of(
            when (unit) {
                TimeGroupUnit.DAILY -> LocalDateGroupUnit.DAILY
                TimeGroupUnit.WEEKLY -> LocalDateGroupUnit.WEEKLY
                TimeGroupUnit.MONTHLY -> LocalDateGroupUnit.MONTHLY
                TimeGroupUnit.YEARLY -> LocalDateGroupUnit.YEARLY
                TimeGroupUnit.MINUTELY,
                TimeGroupUnit.HOURLY,
                -> throw SamsungHealthException.InvalidValue("Date-based aggregations can't be grouped by $unit")
            },
            multiplier,
        )
