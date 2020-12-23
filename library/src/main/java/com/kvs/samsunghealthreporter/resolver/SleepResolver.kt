package com.kvs.samsunghealthreporter.resolver

import com.kvs.samsunghealthreporter.decorator.addAggregateFunction
import com.kvs.samsunghealthreporter.decorator.setTimeUnit
import com.kvs.samsunghealthreporter.model.AggregateFunction
import com.kvs.samsunghealthreporter.model.Filter
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.model.session.Sleep
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class SleepResolver(healthDataStore: HealthDataStore) : CommonResolver<Sleep>(healthDataStore) {
    override val type: String
        get() = HealthConstants.Sleep.HEALTH_DATA_TYPE

    override fun read(
        startTime: Date,
        endTime: Date,
        filter: Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<Sleep> {
        val list = mutableListOf<Sleep>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(type)
            .setProperties(
                arrayOf(
                    HealthConstants.Sleep.START_TIME,
                    HealthConstants.Sleep.TIME_OFFSET,
                    HealthConstants.Sleep.END_TIME,
                    HealthConstants.Sleep.UUID,
                    HealthConstants.Sleep.CREATE_TIME,
                    HealthConstants.Sleep.UPDATE_TIME,
                    HealthConstants.Sleep.PACKAGE_NAME,
                    HealthConstants.Sleep.DEVICE_UUID,
                    HealthConstants.Sleep.CUSTOM
                )
            )
            .setLocalTimeRange(
                HealthConstants.Sleep.START_TIME,
                HealthConstants.Sleep.TIME_OFFSET,
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
            val sleep = Sleep.fromReadData(data)
            list.add(sleep)
        }
        return list
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<Sleep> {
        val list = mutableListOf<Sleep>()
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(type)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.Sleep.END_TIME)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.Sleep.START_TIME)
            .setLocalTimeRange(
                HealthConstants.Sleep.START_TIME,
                HealthConstants.Sleep.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
            .setTimeUnit(
                timeGroup,
                HealthConstants.Sleep.START_TIME,
                HealthConstants.Sleep.TIME_OFFSET
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
            val sleep = Sleep.fromAggregateData(data, timeGroup)
            list.add(sleep)
        }
        return list
    }
}