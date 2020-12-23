package com.kvs.samsunghealthreporter.resolver

import com.kvs.samsunghealthreporter.decorator.addAggregateFunction
import com.kvs.samsunghealthreporter.decorator.setTimeUnit
import com.kvs.samsunghealthreporter.model.AggregateFunction
import com.kvs.samsunghealthreporter.model.Filter
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.model.session.Electrocardiogram
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class ElectrocardiogramResolver(healthDataStore: HealthDataStore) : CommonResolver<Electrocardiogram>(healthDataStore) {
    override val type: String
        get() = HealthConstants.Electrocardiogram.HEALTH_DATA_TYPE

    override fun read(
        startTime: Date,
        endTime: Date,
        filter: Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<Electrocardiogram> {
        val list = mutableListOf<Electrocardiogram>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(type)
            .setProperties(
                arrayOf(
                    HealthConstants.Electrocardiogram.SAMPLE_FREQUENCY,
                    HealthConstants.Electrocardiogram.SAMPLE_COUNT,
                    HealthConstants.Electrocardiogram.MIN_HEART_RATE,
                    HealthConstants.Electrocardiogram.MEAN_HEART_RATE,
                    HealthConstants.Electrocardiogram.MAX_HEART_RATE,
                    HealthConstants.Electrocardiogram.COMMENT,
                    HealthConstants.Electrocardiogram.START_TIME,
                    HealthConstants.Electrocardiogram.TIME_OFFSET,
                    HealthConstants.Electrocardiogram.END_TIME,
                    HealthConstants.Electrocardiogram.UUID,
                    HealthConstants.Electrocardiogram.CREATE_TIME,
                    HealthConstants.Electrocardiogram.UPDATE_TIME,
                    HealthConstants.Electrocardiogram.PACKAGE_NAME,
                    HealthConstants.Electrocardiogram.DEVICE_UUID,
                    HealthConstants.Electrocardiogram.CUSTOM
                )
            )
            .setLocalTimeRange(
                HealthConstants.Electrocardiogram.START_TIME,
                HealthConstants.Electrocardiogram.TIME_OFFSET,
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
            val electrocardiogram = Electrocardiogram.fromReadData(data)
            list.add(electrocardiogram)
        }
        return list
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<Electrocardiogram> {
        val list = mutableListOf<Electrocardiogram>()
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(type)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.Electrocardiogram.SAMPLE_FREQUENCY)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.Electrocardiogram.SAMPLE_FREQUENCY)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.Electrocardiogram.SAMPLE_FREQUENCY)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.Electrocardiogram.SAMPLE_COUNT)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.Electrocardiogram.SAMPLE_COUNT)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.Electrocardiogram.SAMPLE_COUNT)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.Electrocardiogram.MAX_HEART_RATE)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.Electrocardiogram.MIN_HEART_RATE)
            .setLocalTimeRange(
                HealthConstants.Electrocardiogram.START_TIME,
                HealthConstants.Electrocardiogram.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
            .setTimeUnit(
                timeGroup,
                HealthConstants.Electrocardiogram.START_TIME,
                HealthConstants.Electrocardiogram.TIME_OFFSET
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
            val electrocardiogram = Electrocardiogram.fromAggregateData(data, timeGroup)
            list.add(electrocardiogram)
        }
        return list
    }
}