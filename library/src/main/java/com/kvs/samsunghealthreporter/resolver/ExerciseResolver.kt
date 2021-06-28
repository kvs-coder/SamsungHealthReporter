package com.kvs.samsunghealthreporter.resolver

import com.kvs.samsunghealthreporter.decorator.Filter
import com.kvs.samsunghealthreporter.decorator.SortOrder
import com.kvs.samsunghealthreporter.decorator.addAggregateFunction
import com.kvs.samsunghealthreporter.decorator.setTimeUnit
import com.kvs.samsunghealthreporter.model.AggregateFunction
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.model.session.Exercise
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class ExerciseResolver(healthDataStore: HealthDataStore) : CommonResolver<Exercise>(healthDataStore) {
    override val type: String
        get() = HealthConstants.Exercise.HEALTH_DATA_TYPE

    override fun read(
        startTime: Date,
        endTime: Date,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<Exercise> {
        val list = mutableListOf<Exercise>()
        val requestBuilder = HealthDataResolver.ReadRequest.Builder()
            .setDataType(type)
            .setProperties(
                arrayOf(
                    HealthConstants.Exercise.COUNT,
                    HealthConstants.Exercise.CALORIE,
                    HealthConstants.Exercise.LIVE_DATA,
                    HealthConstants.Exercise.LOCATION_DATA,
                    HealthConstants.Exercise.ADDITIONAL,
                    HealthConstants.Exercise.EXERCISE_TYPE,
                    HealthConstants.Exercise.EXERCISE_CUSTOM_TYPE,
                    HealthConstants.Exercise.MEAN_SPEED,
                    HealthConstants.Exercise.MAX_SPEED,
                    HealthConstants.Exercise.DISTANCE,
                    HealthConstants.Exercise.INCLINE_DISTANCE,
                    HealthConstants.Exercise.DECLINE_DISTANCE,
                    HealthConstants.Exercise.CALORIE,
                    HealthConstants.Exercise.MEAN_CALORICBURN_RATE,
                    HealthConstants.Exercise.MAX_CALORICBURN_RATE,
                    HealthConstants.Exercise.DURATION,
                    HealthConstants.Exercise.ALTITUDE_GAIN,
                    HealthConstants.Exercise.ALTITUDE_LOSS,
                    HealthConstants.Exercise.MIN_ALTITUDE,
                    HealthConstants.Exercise.MAX_ALTITUDE,
                    HealthConstants.Exercise.COUNT,
                    HealthConstants.Exercise.COUNT_TYPE,
                    HealthConstants.Exercise.MEAN_CADENCE,
                    HealthConstants.Exercise.MAX_CADENCE,
                    HealthConstants.Exercise.MIN_HEART_RATE,
                    HealthConstants.Exercise.MEAN_HEART_RATE,
                    HealthConstants.Exercise.MAX_HEART_RATE,
                    HealthConstants.Exercise.MEAN_POWER,
                    HealthConstants.Exercise.MAX_POWER,
                    HealthConstants.Exercise.MEAN_RPM,
                    HealthConstants.Exercise.MAX_RPM,
                    HealthConstants.Exercise.COMMENT,
                    HealthConstants.Exercise.DISTANCE,
                    HealthConstants.Exercise.START_TIME,
                    HealthConstants.Exercise.TIME_OFFSET,
                    HealthConstants.Exercise.END_TIME,
                    HealthConstants.Exercise.UUID,
                    HealthConstants.Exercise.CREATE_TIME,
                    HealthConstants.Exercise.UPDATE_TIME,
                    HealthConstants.Exercise.PACKAGE_NAME,
                    HealthConstants.Exercise.DEVICE_UUID,
                    HealthConstants.Exercise.CUSTOM
                )
            )
            .setLocalTimeRange(
                HealthConstants.Exercise.START_TIME,
                HealthConstants.Exercise.TIME_OFFSET,
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
            val exercise = Exercise.fromReadData(data)
            list.add(exercise)
        }
        return list
    }

    override fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<Exercise> {
        val list = mutableListOf<Exercise>()
        val requestBuilder = HealthDataResolver.AggregateRequest.Builder()
            .setDataType(type)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.Exercise.CALORIE)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.Exercise.CALORIE)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.Exercise.CALORIE)
            .addAggregateFunction(AggregateFunction.SUM, HealthConstants.Exercise.CALORIE)
            .addAggregateFunction(AggregateFunction.COUNT, HealthConstants.Exercise.CALORIE)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.Exercise.MAX_SPEED)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.Exercise.MEAN_SPEED)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.Exercise.DISTANCE)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.Exercise.DISTANCE)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.Exercise.DISTANCE)
            .addAggregateFunction(AggregateFunction.SUM, HealthConstants.Exercise.DISTANCE)
            .addAggregateFunction(AggregateFunction.COUNT, HealthConstants.Exercise.DISTANCE)
            .addAggregateFunction(AggregateFunction.MIN, HealthConstants.Exercise.MIN_HEART_RATE)
            .addAggregateFunction(AggregateFunction.AVG, HealthConstants.Exercise.MEAN_HEART_RATE)
            .addAggregateFunction(AggregateFunction.MAX, HealthConstants.Exercise.MAX_HEART_RATE)
            .setLocalTimeRange(
                HealthConstants.Exercise.START_TIME,
                HealthConstants.Exercise.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
            .setTimeUnit(
                timeGroup,
                HealthConstants.Exercise.START_TIME,
                HealthConstants.Exercise.TIME_OFFSET
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
            val exercise = Exercise.fromAggregateData(data, timeGroup)
            list.add(exercise)
        }
        return list
    }
}