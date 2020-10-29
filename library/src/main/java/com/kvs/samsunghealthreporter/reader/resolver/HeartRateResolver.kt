package com.kvs.samsunghealthreporter.reader.resolver

import android.util.Log
import com.kvs.samsunghealthreporter.decorator.addAggregateFunction
import com.kvs.samsunghealthreporter.decorator.setTimeUnit
import com.kvs.samsunghealthreporter.model.AggregateFunction
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
        val list = mutableListOf<HeartRate>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
            .setProperties(
                arrayOf(
                    HealthConstants.HeartRate.HEART_RATE,
                    HealthConstants.HeartRate.HEART_BEAT_COUNT,
                    HealthConstants.HeartRate.COMMENT,
                    HealthConstants.HeartRate.MIN,
                    HealthConstants.HeartRate.MAX,
                    HealthConstants.HeartRate.START_TIME,
                    HealthConstants.HeartRate.TIME_OFFSET,
                    HealthConstants.HeartRate.END_TIME,
                    HealthConstants.HeartRate.UUID,
                    HealthConstants.HeartRate.CREATE_TIME,
                    HealthConstants.HeartRate.UPDATE_TIME,
                    HealthConstants.HeartRate.PACKAGE_NAME,
                    HealthConstants.HeartRate.DEVICE_UUID,
                    HealthConstants.HeartRate.CUSTOM
                )
            )
            .setLocalTimeRange(
                HealthConstants.StepCount.START_TIME,
                HealthConstants.StepCount.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
        filter?.let {
            requestBuilder.setFilter(filter)
        }
        sort?.let {
            requestBuilder.setSort(it.first, it.second)
        }
        val request = requestBuilder.build()
        val resolver = HealthDataResolver(healthDataStore, null)
        val result = resolver.read(request).await()
        val iterator = result.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            val heartRate = HeartRate.fromReadData(data)
            list.add(heartRate)
        }
        return list
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: HealthDataResolver.Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<HeartRate> {
        Log.d("FFF", startTime.toString())
        val list = mutableListOf<HeartRate>()
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.HeartRate.HEART_RATE)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.HeartRate.HEART_RATE)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.HeartRate.HEART_RATE)
            .setLocalTimeRange(
                HealthConstants.StepCount.START_TIME,
                HealthConstants.StepCount.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
            .setTimeUnit(
                timeGroup,
                HealthConstants.StepCount.START_TIME,
                HealthConstants.StepCount.TIME_OFFSET
            )
        filter?.let {
            requestBuilder.setFilter(filter)
        }
        sort?.let {
            requestBuilder.setSort(it.first, it.second)
        }
        val request = requestBuilder.build()
        val resolver = HealthDataResolver(healthDataStore, null)
        val result = resolver.aggregate(request).await()
        val iterator = result.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            val stepCount = HeartRate.fromAggregateData(data, timeGroup)
            list.add(stepCount)
        }
        return list
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