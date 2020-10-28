package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.decorator.roundedDecimal
import com.samsung.android.sdk.healthdata.*
import java.util.*

class StepCount: Session<StepCount.ReadResult, StepCount.AggregateResult, StepCount.InsertResult> {
    data class Count(val value: Long, val unit: String)

    data class Calorie(val value: Double, val unit: String)

    data class Speed(val value: Double, val unit: String)

    data class Distance(val value: Double, val unit: String)

    data class ReadResult(
        override val uuid: String,
        override val packageName: String,
        override val deviceUuid: String,
        override val custom: String?,
        override val createTime: Long,
        override val updateTime: Long,
        override val startTime: Long,
        override val timeOffset: Long,
        override val endTime: Long,
        val count: Count,
        val calorie: Calorie,
        val speed: Speed,
        val distance: Distance
    ) : Session.ReadResult

    data class AggregateResult(
        override val packageName: String,
        override val time: Time,
        val totalCount: Count,
        val totalCalories: Calorie,
        val averageSpeed: Speed,
        val maxSpeed: Speed,
        val minSpeed: Speed,
        val totalDistance: Distance
    ) : Session.AggregateResult

    data class InsertResult(
        override val packageName: String,
        val startDate: Date,
        val timeOffset: Long,
        val endDate: Date,
        val count: Long,
        val calorie: Double,
        val speed: Double,
        val distance: Double
    ) : Session.InsertResult

    companion object {
        private const val COUNT_UNIT = "count"
        private const val CALORIE_UNIT = "kcal"
        private const val SPEED_UNIT = "km/h"
        private const val ALIAS_TOTAL_COUNT = "count_sum"
        private const val ALIAS_TOTAL_CALORIES = "calories_sum"
        private const val ALIAS_AVERAGE_SPEED = "speed_avg"
        private const val ALIAS_MAX_SPEED = "speed_max"
        private const val ALIAS_MIN_SPEED = "speed_min"
        private const val ALIAS_TOTAL_DISTANCE = "distance_sum"

        const val ALIAS_PACKAGE_NAME = "alias_package_name"

        internal fun fromReadData(data: HealthData): StepCount {
            return StepCount().apply {
                readResult = ReadResult(
                    data.getString(HealthConstants.StepCount.UUID),
                    data.getString(HealthConstants.StepCount.PACKAGE_NAME),
                    data.getString(HealthConstants.StepCount.DEVICE_UUID),
                    data.getString(HealthConstants.StepCount.CUSTOM),
                    data.getLong(HealthConstants.StepCount.CREATE_TIME),
                    data.getLong(HealthConstants.StepCount.UPDATE_TIME),
                    data.getLong(HealthConstants.StepCount.START_TIME),
                    data.getLong(HealthConstants.StepCount.TIME_OFFSET),
                    data.getLong(HealthConstants.StepCount.END_TIME),
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

        internal fun fromAggregateData(data: HealthData, timeGroup: Time.Group): StepCount {
            return StepCount().apply {
                aggregateResult = AggregateResult(
                    data.getString(ALIAS_PACKAGE_NAME),
                    Time(data.getString(timeGroup.alias), timeGroup),
                    Count(data.getLong(ALIAS_TOTAL_COUNT), COUNT_UNIT),
                    Calorie(data.getDouble(ALIAS_TOTAL_CALORIES).roundedDecimal, CALORIE_UNIT),
                    Speed(data.getDouble(ALIAS_AVERAGE_SPEED).roundedDecimal, SPEED_UNIT),
                    Speed(data.getDouble(ALIAS_MAX_SPEED).roundedDecimal, SPEED_UNIT),
                    Speed(data.getDouble(ALIAS_MIN_SPEED).roundedDecimal, SPEED_UNIT),
                    Distance(
                        data.getDouble(ALIAS_TOTAL_DISTANCE).roundedDecimal,
                        HealthDataUnit.METER.unitName
                    )
                )
            }
        }
    }

    override val type: String = HealthConstants.StepCount.HEALTH_DATA_TYPE
    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
    override var insertResult: InsertResult? = null

    private constructor()

    constructor(insertResult: InsertResult) {
        this.insertResult = insertResult
    }

    @Throws(SamsungHealthWriteException::class)
    internal fun asOriginal(healthDataStore: HealthDataStore): HealthData {
        val insertResult = this.insertResult ?: throw SamsungHealthWriteException(
            "Insert result was null, nothing to write in Samsung Health"
        )
        val deviceUuid = HealthDeviceManager(healthDataStore).localDevice.uuid
        return HealthData().apply {
            sourceDevice = deviceUuid
            putString(HealthConstants.StepCount.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.StepCount.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.StepCount.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.StepCount.TIME_OFFSET, insertResult.timeOffset)
            putLong(HealthConstants.StepCount.END_TIME, insertResult.endDate.time)
            putLong(HealthConstants.StepCount.COUNT, insertResult.count)
            putDouble(HealthConstants.StepCount.CALORIE, insertResult.calorie)
            putDouble(HealthConstants.StepCount.SPEED, insertResult.speed)
            putDouble(HealthConstants.StepCount.DISTANCE, insertResult.distance)
        }
    }
}