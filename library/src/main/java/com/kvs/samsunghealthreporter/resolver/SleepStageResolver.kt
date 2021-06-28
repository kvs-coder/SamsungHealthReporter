package com.kvs.samsunghealthreporter.resolver

import com.kvs.samsunghealthreporter.decorator.Filter
import com.kvs.samsunghealthreporter.decorator.SortOrder
import com.kvs.samsunghealthreporter.decorator.addAggregateFunction
import com.kvs.samsunghealthreporter.decorator.addGrouping
import com.kvs.samsunghealthreporter.decorator.setTimeUnit
import com.kvs.samsunghealthreporter.model.AggregateFunction
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.model.session.SleepStage
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class SleepStageResolver(healthDataStore: HealthDataStore) : CommonResolver<SleepStage>(healthDataStore) {
    override val type: String
        get() = HealthConstants.SleepStage.HEALTH_DATA_TYPE

    override fun read(
        startTime: Date,
        endTime: Date,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<SleepStage> {
        val list = mutableListOf<SleepStage>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(type)
            .setProperties(
                arrayOf(
                    HealthConstants.SleepStage.STAGE,
                    HealthConstants.SleepStage.START_TIME,
                    HealthConstants.SleepStage.TIME_OFFSET,
                    HealthConstants.SleepStage.END_TIME,
                    HealthConstants.SleepStage.UUID,
                    HealthConstants.SleepStage.CREATE_TIME,
                    HealthConstants.SleepStage.UPDATE_TIME,
                    HealthConstants.SleepStage.PACKAGE_NAME,
                    HealthConstants.SleepStage.DEVICE_UUID,
                    HealthConstants.SleepStage.CUSTOM
                )
            )
            .setLocalTimeRange(
                HealthConstants.SleepStage.START_TIME,
                HealthConstants.SleepStage.TIME_OFFSET,
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
            val sleepStage = SleepStage.fromReadData(data)
            list.add(sleepStage)
        }
        return list
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<SleepStage> {
        val list = mutableListOf<SleepStage>()
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(type)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.SleepStage.END_TIME)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.SleepStage.START_TIME)
            .setLocalTimeRange(
                HealthConstants.SleepStage.START_TIME,
                HealthConstants.SleepStage.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
            .addGrouping(HealthConstants.SleepStage.STAGE)
            .setTimeUnit(
                timeGroup,
                HealthConstants.SleepStage.START_TIME,
                HealthConstants.SleepStage.TIME_OFFSET
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
            val sleepStage = SleepStage.fromAggregateData(data, timeGroup)
            list.add(sleepStage)
        }
        return list
    }
}