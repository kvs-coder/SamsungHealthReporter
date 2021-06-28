package com.kvs.samsunghealthreporter.resolver

import com.kvs.samsunghealthreporter.decorator.Filter
import com.kvs.samsunghealthreporter.decorator.SortOrder
import com.kvs.samsunghealthreporter.decorator.addAggregateFunction
import com.kvs.samsunghealthreporter.decorator.setTimeUnit
import com.kvs.samsunghealthreporter.model.AggregateFunction
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.model.session.OxygenSaturation
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class OxygenSaturationResolver(healthDataStore: HealthDataStore) : CommonResolver<OxygenSaturation>(healthDataStore) {
    override val type: String
        get() = HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE

    override fun read(
        startTime: Date,
        endTime: Date,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<OxygenSaturation> {
        val list = mutableListOf<OxygenSaturation>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(type)
            .setProperties(
                arrayOf(
                    HealthConstants.OxygenSaturation.SPO2,
                    HealthConstants.OxygenSaturation.HEART_RATE,
                    HealthConstants.OxygenSaturation.COMMENT,
                    HealthConstants.OxygenSaturation.START_TIME,
                    HealthConstants.OxygenSaturation.TIME_OFFSET,
                    HealthConstants.OxygenSaturation.END_TIME,
                    HealthConstants.OxygenSaturation.UUID,
                    HealthConstants.OxygenSaturation.CREATE_TIME,
                    HealthConstants.OxygenSaturation.UPDATE_TIME,
                    HealthConstants.OxygenSaturation.PACKAGE_NAME,
                    HealthConstants.OxygenSaturation.DEVICE_UUID,
                    HealthConstants.OxygenSaturation.CUSTOM
                )
            )
            .setLocalTimeRange(
                HealthConstants.OxygenSaturation.START_TIME,
                HealthConstants.OxygenSaturation.TIME_OFFSET,
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
            val oxygenSaturation = OxygenSaturation.fromReadData(data)
            list.add(oxygenSaturation)
        }
        return list
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<OxygenSaturation> {
        val list = mutableListOf<OxygenSaturation>()
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(type)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.OxygenSaturation.HEART_RATE)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.OxygenSaturation.HEART_RATE)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.OxygenSaturation.HEART_RATE)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.OxygenSaturation.SPO2)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.OxygenSaturation.SPO2)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.OxygenSaturation.SPO2)
            .setLocalTimeRange(
                HealthConstants.OxygenSaturation.START_TIME,
                HealthConstants.OxygenSaturation.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
            .setTimeUnit(
                timeGroup,
                HealthConstants.OxygenSaturation.START_TIME,
                HealthConstants.OxygenSaturation.TIME_OFFSET
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
            val oxygenSaturation = OxygenSaturation.fromAggregateData(data, timeGroup)
            list.add(oxygenSaturation)
        }
        return list
    }
}