package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.SamsungHealthException
import kotlinx.serialization.Serializable

/**
 * A time range in epoch milliseconds, end exclusive.
 *
 * Reads and aggregates interpret it in the device's local time, the way Samsung Health shows data.
 *
 * @param start **Long** epoch milliseconds, inclusive
 * @param end **Long** epoch milliseconds, exclusive
 * @throws SamsungHealthException.InvalidValue when [end] is not after [start]
 */
@Serializable
public data class TimeRange(
    val start: Long,
    val end: Long,
) : Payload {
    init {
        if (end <= start) {
            throw SamsungHealthException.InvalidValue("Invalid TimeRange: end $end is not after start $start")
        }
    }

    override val json: String get() = Companion.encode(this)

    /** Factory of **TimeRange** */
    public companion object : Payload.Factory<TimeRange>({ TimeRange.serializer() }) {
        /**
         * The local calendar day that contains [time].
         *
         * @param time **Long** epoch milliseconds. Now by default
         * @return **TimeRange** from local midnight to the next local midnight
         */
        public fun day(time: Long = System.currentTimeMillis()): TimeRange {
            val zone = java.time.ZoneId.systemDefault()
            val date =
                java.time.Instant
                    .ofEpochMilli(time)
                    .atZone(zone)
                    .toLocalDate()
            val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
            val end =
                date
                    .plusDays(1)
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli()
            return TimeRange(start, end)
        }

        /**
         * The last [days] local calendar days, including today.
         *
         * @param days **Int** number of days, at least 1
         * @param time **Long** epoch milliseconds inside the last day. Now by default
         * @return **TimeRange** from local midnight [days] - 1 days ago to the next local midnight
         * @throws SamsungHealthException.InvalidValue when [days] is less than 1
         */
        public fun lastDays(
            days: Int,
            time: Long = System.currentTimeMillis(),
        ): TimeRange {
            if (days < 1) throw SamsungHealthException.InvalidValue("Invalid days: $days")
            val today = day(time)
            val zone = java.time.ZoneId.systemDefault()
            val start =
                java.time.Instant
                    .ofEpochMilli(today.start)
                    .atZone(zone)
                    .minusDays(days - 1L)
                    .toInstant()
                    .toEpochMilli()
            return TimeRange(start, today.end)
        }
    }
}
