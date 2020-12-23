package com.kvs.samsunghealthreporter.resolver

import com.kvs.samsunghealthreporter.decorator.addAggregateFunction
import com.kvs.samsunghealthreporter.decorator.setTimeUnit
import com.kvs.samsunghealthreporter.model.AggregateFunction
import com.kvs.samsunghealthreporter.model.Filter
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.model.session.FloorsClimbed
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class FloorsClimbedResolver(healthDataStore: HealthDataStore) : CommonResolver<FloorsClimbed>(healthDataStore) {
    override val type: String
        get() = HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE

    override fun read(
        startTime: Date,
        endTime: Date,
        filter: Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<FloorsClimbed> {
        val list = mutableListOf<FloorsClimbed>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(type)
            .setProperties(
                arrayOf(
                    HealthConstants.FloorsClimbed.FLOOR,
                    HealthConstants.FloorsClimbed.START_TIME,
                    HealthConstants.FloorsClimbed.TIME_OFFSET,
                    HealthConstants.FloorsClimbed.END_TIME,
                    HealthConstants.FloorsClimbed.UUID,
                    HealthConstants.FloorsClimbed.CREATE_TIME,
                    HealthConstants.FloorsClimbed.UPDATE_TIME,
                    HealthConstants.FloorsClimbed.PACKAGE_NAME,
                    HealthConstants.FloorsClimbed.DEVICE_UUID,
                    HealthConstants.FloorsClimbed.CUSTOM
                )
            )
            .setLocalTimeRange(
                HealthConstants.FloorsClimbed.START_TIME,
                HealthConstants.FloorsClimbed.TIME_OFFSET,
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
            val floorsClimbed = FloorsClimbed.fromReadData(data)
            list.add(floorsClimbed)
        }
        return list
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<FloorsClimbed> {
        val list = mutableListOf<FloorsClimbed>()
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(type)
            .addAggregateFunction(AggregateFunction.SUM, HealthConstants.FloorsClimbed.FLOOR)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.FloorsClimbed.FLOOR)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.FloorsClimbed.FLOOR)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.FloorsClimbed.FLOOR)
            .setLocalTimeRange(
                HealthConstants.FloorsClimbed.START_TIME,
                HealthConstants.FloorsClimbed.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
            .setTimeUnit(
                timeGroup,
                HealthConstants.FloorsClimbed.START_TIME,
                HealthConstants.FloorsClimbed.TIME_OFFSET
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
            val floorsClimbed = FloorsClimbed.fromAggregateData(data, timeGroup)
            list.add(floorsClimbed)
        }
        return list
    }
}