package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.decorator.roundedDecimal
import com.samsung.android.sdk.healthdata.*
import java.util.*

data class StepCount(
    override val startTimestamp: Long,
    override val timeOffset: Long,
    override val endTimestamp: Long,
    override val packageName: String,
) : Session<StepCount.ReadResult, StepCount.AggregateResult, StepCount.InsertResult> {
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

    data class InsertResult(
        val count: Long,
        val calorie: Double,
        val speed: Double,
        val distance: Double
    )

    companion object {
        private const val COUNT_UNIT = "count"
        private const val CALORIE_UNIT = "kcal"
        private const val SPEED_UNIT = "km/h"

        const val ALIAS_HOUR = "hour"
        const val ALIAS_DAY = "day"
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
            return StepCount(
                data.getLong(HealthConstants.StepCount.START_TIME),
                data.getLong(HealthConstants.StepCount.TIME_OFFSET),
                data.getLong(HealthConstants.StepCount.END_TIME),
                data.getString(HealthConstants.StepCount.DEVICE_UUID),
                data.getString(HealthConstants.StepCount.PACKAGE_NAME)
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
            return StepCount(
                data.getLong(ALIAS_DAY),
                data.getLong(ALIAS_TIME_OFFSET),
                data.getLong(ALIAS_END_TIME),
                data.getString(ALIAS_DEVICE_UUID),
                data.getString(ALIAS_PACKAGE_NAME)
            ).apply {
                aggregateResult = AggregateResult(
                    Count(data.getLong(ALIAS_TOTAL_COUNT), COUNT_UNIT),
                    Calorie(data.getDouble(ALIAS_TOTAL_CALORIES).roundedDecimal, CALORIE_UNIT),
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
    override var deviceUuid: String? = null
    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
    override var insertResult: InsertResult? = null

    constructor(
        startTimestamp: Date,
        timeOffset: Long,
        endTimestamp: Date,
        packageName: String,
        insertResult: InsertResult
    ) : this(
        startTimestamp.time,
        timeOffset,
        endTimestamp.time,
        packageName
    ) {
        this.insertResult = insertResult
    }

    private constructor(
        startTimestamp: Long,
        timeOffset: Long,
        endTimestamp: Long,
        deviceUuid: String,
        packageName: String
    ) : this(
        startTimestamp,
        timeOffset,
        endTimestamp,
        packageName
    ) {
        this.deviceUuid = deviceUuid
    }

    @Throws(SamsungHealthWriteException::class)
    fun asOriginal(healthDataStore: HealthDataStore): HealthData {
        val insertResult = this.insertResult ?: throw SamsungHealthWriteException(
            "Insert result was null, nothing to write in Samsung Health"
        )
        val deviceUuid = HealthDeviceManager(healthDataStore).localDevice.uuid
        return HealthData().apply {
            sourceDevice = deviceUuid
            putLong(HealthConstants.StepCount.COUNT, insertResult.count)
            putDouble(HealthConstants.StepCount.CALORIE, insertResult.calorie)
            putDouble(HealthConstants.StepCount.SPEED, insertResult.speed)
            putDouble(HealthConstants.StepCount.DISTANCE, insertResult.distance)
            putString(HealthConstants.StepCount.PACKAGE_NAME, packageName)
            putString(HealthConstants.StepCount.DEVICE_UUID, deviceUuid)
            putLong(HealthConstants.StepCount.START_TIME, startTimestamp)
            putLong(HealthConstants.StepCount.TIME_OFFSET, timeOffset)
            putLong(HealthConstants.StepCount.END_TIME, endTimestamp)
        }
    }
}