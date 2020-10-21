package com.kvs.samsunghealthreporter.decorator

import java.util.*

val Date.dayStart: Date
    get() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = this.time
        cal.configureCalendar(0, 0, 0)
        return cal.time
    }

val Date.dayEnd: Date
    get() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = this.time
        cal.configureCalendar(23, 59, 59)
        return cal.time
    }

internal fun Calendar.configureCalendar(
    hour: Int? = null,
    minute: Int? = null,
    second: Int? = null,
    millis: Int? = null
) {
    hour?.let { this.set(Calendar.HOUR_OF_DAY, it) }
    minute?.let { this.set(Calendar.MINUTE, it) }
    second?.let { this.set(Calendar.SECOND, it) }
    millis?.let { this.set(Calendar.MILLISECOND, it) }
}