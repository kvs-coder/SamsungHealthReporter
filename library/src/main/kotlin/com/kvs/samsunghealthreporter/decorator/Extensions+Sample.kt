package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.model.payload.Sample
import com.kvs.samsunghealthreporter.model.payloadJson

/** Throws when the sample ends before it starts, which Samsung Health rejects. */
internal fun Sample.validateInterval() {
    val end = endTimestamp ?: return
    if (end < startTimestamp) {
        throw SamsungHealthException.InvalidValue(
            "Invalid ${healthType.identifier}: endTimestamp $end is before startTimestamp $startTimestamp",
        )
    }
}

internal fun Long.validateInterval(end: Long) {
    if (end < this) throw SamsungHealthException.InvalidValue("Invalid interval: end $end is before start $this")
}

/** JSON of a sample, with its `type` discriminator so it can be decoded as any **Sample**. */
internal val Sample.encoded: String
    get() = payloadJson.encodeToString(Sample.serializer(), this)
