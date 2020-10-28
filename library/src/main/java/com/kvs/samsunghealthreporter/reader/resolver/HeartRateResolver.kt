package com.kvs.samsunghealthreporter.reader.resolver

import com.kvs.samsunghealthreporter.model.HeartRate
import com.kvs.samsunghealthreporter.model.StepCount
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class HeartRateResolver(healthDataStore: HealthDataStore) : SessionResolver<HeartRate>(healthDataStore) {
    override fun read(
        startTime: Date,
        endTime: Date,
        filter: HealthDataResolver.Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<HeartRate> {
        return listOf()
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: HealthDataResolver.Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<HeartRate> {
        TODO("Not yet implemented")
    }

    override fun insert(value: HeartRate): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(value: HeartRate, filter: HealthDataResolver.Filter): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(filter: HealthDataResolver.Filter): Boolean {
        TODO("Not yet implemented")
    }
}