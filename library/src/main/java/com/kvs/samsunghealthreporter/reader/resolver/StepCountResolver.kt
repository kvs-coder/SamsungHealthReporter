package com.kvs.samsunghealthreporter.reader.resolver

import android.os.Looper
import android.util.Log
import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.decorator.roundedDecimal
import com.kvs.samsunghealthreporter.decorator.string
import com.kvs.samsunghealthreporter.model.StepCount
import com.samsung.android.sdk.healthdata.*
import java.util.*

class StepCountResolver(private val healthDataStore: HealthDataStore) {
    init {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun read(
        startTime: Date,
        endTime: Date,
        filter: HealthDataResolver.Filter? = null,
        sort: Pair<String, HealthDataResolver.SortOrder>? = null
    ): List<StepCount> {
        val list = mutableListOf<StepCount>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
            .setProperties(
                arrayOf(
                    HealthConstants.StepCount.COUNT,
                    HealthConstants.StepCount.CALORIE,
                    HealthConstants.StepCount.SPEED,
                    HealthConstants.StepCount.DISTANCE,
                    HealthConstants.StepCount.START_TIME,
                    HealthConstants.StepCount.TIME_OFFSET,
                    HealthConstants.StepCount.END_TIME,
                    HealthConstants.StepCount.DEVICE_UUID,
                    HealthConstants.StepCount.PACKAGE_NAME
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
        Log.i("MMM", list.size.toString())
        result.close()
        return list
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: HealthDataResolver.AggregateRequest.TimeGroupUnit = HealthDataResolver.AggregateRequest.TimeGroupUnit.DAILY,
        filter: HealthDataResolver.Filter? = null,
        sort: Pair<String, HealthDataResolver.SortOrder>? = null
    ): StepCount? {
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
            .addFunction(
                HealthDataResolver.AggregateRequest.AggregateFunction.SUM,
                HealthConstants.StepCount.COUNT,
                StepCount.ALIAS_TOTAL_COUNT
            )
            .addFunction(
                HealthDataResolver.AggregateRequest.AggregateFunction.SUM,
                HealthConstants.StepCount.CALORIE,
                StepCount.ALIAS_TOTAL_CALORIES
            )
            .addFunction(
                HealthDataResolver.AggregateRequest.AggregateFunction.AVG,
                HealthConstants.StepCount.SPEED,
                StepCount.ALIAS_AVERAGE_SPEED
            )
            .addFunction(
                HealthDataResolver.AggregateRequest.AggregateFunction.MAX,
                HealthConstants.StepCount.SPEED,
                StepCount.ALIAS_MAX_SPEED
            )
            .addFunction(
                HealthDataResolver.AggregateRequest.AggregateFunction.MIN,
                HealthConstants.StepCount.SPEED,
                StepCount.ALIAS_MIN_SPEED
            )
            .addFunction(
                HealthDataResolver.AggregateRequest.AggregateFunction.SUM,
                HealthConstants.StepCount.DISTANCE,
                StepCount.ALIAS_TOTAL_DISTANCE
            )
            .addGroup(
                HealthConstants.StepCount.PACKAGE_NAME, StepCount.ALIAS_PACKAGE_NAME
            )
            .addGroup(
                HealthConstants.StepCount.DEVICE_UUID, StepCount.ALIAS_DEVICE_UUID
            )
            .setTimeGroup(
                timeGroup,
                1,
                HealthConstants.StepCount.START_TIME,
                HealthConstants.StepCount.TIME_OFFSET,
                StepCount.ALIAS_DAY
            )
            .setLocalTimeRange(
                HealthConstants.StepCount.START_TIME,
                HealthConstants.StepCount.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
        Log.i("LLL", startTime.toString())
        Log.i("LLL", endTime.toString())
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
        var i = 0
        while (iterator.hasNext()) {
            val data = iterator.next()
            val step = StepCount.fromAggregateData(data)
            Log.e("HHH", step.json)
            i++
        }
        Log.i("LLL", i.toString())
        return null
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class, SamsungHealthWriteException::class)
    fun insert(value: StepCount): Boolean {
        val request = HealthDataResolver.InsertRequest.Builder()
            .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
            .build()
        val data = value.asOriginal(healthDataStore)
        request.addHealthData(data)
        val resolver = HealthDataResolver(healthDataStore, null)
        val result = resolver.insert(request).await()
        return result.status == HealthResultHolder.BaseResult.STATUS_SUCCESSFUL
    }
}