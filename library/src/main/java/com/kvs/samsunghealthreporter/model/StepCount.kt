package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.decorator.roundedDecimal
import com.samsung.android.sdk.healthdata.*

data class StepCount(
    override var startTimestamp: Long,
    override var timeOffset: Long,
    override var endTimestamp: Long,
    override var deviceUuid: String,
    override var packageName: String
) : Session<StepCount.ReadResult, StepCount.AggregateResult> {
    data class Count(val value: Long, val unit: String)
    data class Calorie(val value: Double, val unit: String)
    data class Speed(val value: Double, val unit: String)
    data class Distance(val value: Double, val unit: String)
    data class ReadResult(
        val count: Count,
        val calorie: Calorie,
        val speed: Speed,
        val distance: Distance
    )
    data class AggregateResult(
        val totalCount: Count,
        val totalCalories: Calorie,
        val averageSpeed: Speed,
        val maxSpeed: Speed,
        val minSpeed: Speed,
        val totalDistance: Distance
    )

    companion object {
        private const val COUNT_UNIT = "count"
        private const val CALORIE_UNIT = "kcal"
        private const val SPEED_UNIT = "km/h"

        const val ALIAS_TOTAL_COUNT = "count_sum"
        const val ALIAS_TOTAL_CALORIES = "calories_sum"
        const val ALIAS_AVERAGE_SPEED = "speed_avg"
        const val ALIAS_MAX_SPEED = "speed_max"
        const val ALIAS_MIN_SPEED = "speed_min"
        const val ALIAS_TOTAL_DISTANCE = "distance_sum"
        const val ALIAS_START_TIME = "aggregate_start_time"
        const val ALIAS_TIME_OFFSET = "aggregate_time_offset"
        const val ALIAS_END_TIME = "aggregate_end_time"
        const val ALIAS_PACKAGE_NAME = "aggregate_package_name"
        const val ALIAS_DEVICE_UUID = "aggregate_device_uuid"

        fun fromReadData(data: HealthData): StepCount {
            val startTimestamp = data.getLong(HealthConstants.StepCount.START_TIME)
            val timeOffset = data.getLong(HealthConstants.StepCount.TIME_OFFSET)
            val endTimestamp = data.getLong(HealthConstants.StepCount.END_TIME)
            val deviceUuid = data.getString(HealthConstants.StepCount.DEVICE_UUID)
            val packageName = data.getString(HealthConstants.StepCount.PACKAGE_NAME)
            return StepCount(
                startTimestamp,
                timeOffset,
                endTimestamp,
                deviceUuid,
                packageName
            ).apply {
                readResult = ReadResult(
                    Count(data.getLong(HealthConstants.StepCount.COUNT), COUNT_UNIT),
                    Calorie(
                        data.getDouble(HealthConstants.StepCount.CALORIE).roundedDecimal,
                        CALORIE_UNIT
                    ),
                    Speed(
                        data.getDouble(HealthConstants.StepCount.SPEED).roundedDecimal,
                        SPEED_UNIT
                    ),
                    Distance(
                        data.getDouble(HealthConstants.StepCount.DISTANCE).roundedDecimal,
                        HealthDataUnit.METER.unitName
                    )
                )
            }
        }

        fun fromAggregateData(data: HealthData): StepCount {
            val startTimestamp = data.getLong(ALIAS_START_TIME)
            val timeOffset = data.getLong(ALIAS_TIME_OFFSET)
            val endTimestamp = data.getLong(ALIAS_END_TIME)
            val deviceUuid = data.getString(ALIAS_DEVICE_UUID)
            val packageName = data.getString(ALIAS_PACKAGE_NAME)
            return StepCount(
                startTimestamp,
                timeOffset,
                endTimestamp,
                deviceUuid,
                packageName
            ).apply {
                aggregateResult = AggregateResult(
                    Count(data.getLong(ALIAS_TOTAL_COUNT), COUNT_UNIT),
                    Calorie(data.getDouble(ALIAS_TOTAL_COUNT).roundedDecimal, CALORIE_UNIT),
                    Speed(data.getDouble(ALIAS_AVERAGE_SPEED).roundedDecimal, SPEED_UNIT),
                    Speed(data.getDouble(ALIAS_MAX_SPEED).roundedDecimal, SPEED_UNIT),
                    Speed(data.getDouble(ALIAS_MIN_SPEED).roundedDecimal, SPEED_UNIT),
                    Distance(
                        data.getDouble(ALIAS_TOTAL_DISTANCE).roundedDecimal,
                        HealthDataUnit.METER.unitName
                    ),
                )
            }
        }
    }

    override val type = HealthConstants.StepCount.HEALTH_DATA_TYPE
    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
}