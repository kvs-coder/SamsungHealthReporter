package com.kvs.samsunghealthreporter.reader.resolver

import android.os.Looper
import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.*
import com.samsung.android.sdk.healthdata.*
import java.util.*

abstract class SessionResolver<SessionResult>(protected val healthDataStore: HealthDataStore) where SessionResult : Common {
    init {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    abstract fun read(
        startTime: Date,
        endTime: Date,
        filter: HealthDataResolver.Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<SessionResult>

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    abstract fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: HealthDataResolver.Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<SessionResult>

    @Throws(
        IllegalArgumentException::class,
        IllegalStateException::class,
        SamsungHealthWriteException::class
    )
    abstract fun insert(value: SessionResult): Boolean

    @Throws(
        IllegalArgumentException::class,
        IllegalStateException::class,
        SamsungHealthWriteException::class
    )
    abstract fun update(value: SessionResult, filter: HealthDataResolver.Filter): Boolean

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    abstract fun delete(filter: HealthDataResolver.Filter): Boolean
}