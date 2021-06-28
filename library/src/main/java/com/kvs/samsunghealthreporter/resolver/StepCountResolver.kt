package com.kvs.samsunghealthreporter.resolver

import com.kvs.samsunghealthreporter.decorator.*
import com.kvs.samsunghealthreporter.model.*
import com.kvs.samsunghealthreporter.model.session.StepCount
import com.samsung.android.sdk.healthdata.*
import java.util.*

class StepCountResolver(healthDataStore: HealthDataStore) : CommonResolver<StepCount>(healthDataStore) {
    override val type: String
        get() = HealthConstants.StepCount.HEALTH_DATA_TYPE

    override fun read(
        startTime: Date,
        endTime: Date,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<StepCount> {
        val list = mutableListOf<StepCount>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(type)
            .setProperties(
                arrayOf(
                    HealthConstants.StepCount.COUNT,
                    HealthConstants.StepCount.CALORIE,
                    HealthConstants.StepCount.SPEED,
                    HealthConstants.StepCount.DISTANCE,
                    HealthConstants.StepCount.START_TIME,
                    HealthConstants.StepCount.TIME_OFFSET,
                    HealthConstants.StepCount.END_TIME,
                    HealthConstants.StepCount.UUID,
                    HealthConstants.StepCount.CREATE_TIME,
                    HealthConstants.StepCount.UPDATE_TIME,
                    HealthConstants.StepCount.PACKAGE_NAME,
                    HealthConstants.StepCount.DEVICE_UUID,
                    HealthConstants.StepCount.CUSTOM
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
            val stepCount = StepCount.fromReadData(data)
            list.add(stepCount)
        }
        return list
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<StepCount> {
        val list = mutableListOf<StepCount>()
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(type)
            .addAggregateFunction(AggregateFunction.SUM, HealthConstants.StepCount.COUNT)
            .addAggregateFunction(AggregateFunction.SUM, HealthConstants.StepCount.CALORIE)
            .addAggregateFunction(AggregateFunction.SUM, HealthConstants.StepCount.DISTANCE)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.StepCount.SPEED)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.StepCount.SPEED)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.StepCount.SPEED)
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
            val stepCount = StepCount.fromAggregateData(data, timeGroup)
            list.add(stepCount)
        }
        return list
    }
}