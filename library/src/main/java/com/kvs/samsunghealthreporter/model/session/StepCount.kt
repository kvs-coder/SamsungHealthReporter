package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.decorator.roundedDecimal
import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.*
import java.util.*

class StepCount : Session<StepCount.ReadResult, StepCount.AggregateResult, StepCount.InsertResult> {
    data class Count(val value: Int, val unit: String)

    data class Calorie(val value: Float, val unit: String)

    data class Speed(val value: Float, val unit: String)

    data class Distance(val value: Float, val unit: String)

    data class Position(val id: Int) {
        val type: String = when (id) {
            HealthConstants.StepCount.SAMPLE_POSITION_TYPE_UNKNOWN -> "unknown"
            HealthConstants.StepCount.SAMPLE_POSITION_TYPE_WRIST -> "wrist"
            HealthConstants.StepCount.SAMPLE_POSITION_TYPE_ANKLE -> "ankle"
            HealthConstants.StepCount.SAMPLE_POSITION_TYPE_ARM -> "arm"
            else -> "na"
        }
    }

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
        val distance: Distance,
        val position: Position
    ) : Session.ReadResult

    data class AggregateResult(
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
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        val count: Int,
        val calorie: Float,
        val speed: Float,
        val distance: Float
    ) : Session.InsertResult

    companion object : Common.Factory<StepCount> {
        private const val COUNT_UNIT = "count"
        private const val CALORIE_UNIT = "kcal"
        private const val SPEED_UNIT = "km/h"
        private const val ALIAS_TOTAL_COUNT = "count_sum"
        private const val ALIAS_TOTAL_CALORIE = "calorie_sum"
        private const val ALIAS_AVERAGE_SPEED = "speed_avg"
        private const val ALIAS_MAX_SPEED = "speed_max"
        private const val ALIAS_MIN_SPEED = "speed_min"
        private const val ALIAS_TOTAL_DISTANCE = "distance_sum"

        override fun fromReadData(data: HealthData): StepCount {
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
                    Count(data.getInt(HealthConstants.StepCount.COUNT), COUNT_UNIT),
                    Calorie(
                        data.getFloat(HealthConstants.StepCount.CALORIE).roundedDecimal.toFloat(),
                        CALORIE_UNIT
                    ),
                    Speed(
                        data.getFloat(HealthConstants.StepCount.SPEED).roundedDecimal.toFloat(),
                        SPEED_UNIT
                    ),
                    Distance(
                        data.getFloat(HealthConstants.StepCount.DISTANCE).roundedDecimal.toFloat(),
                        HealthDataUnit.METER.unitName
                    ),
                    Position(data.getInt(HealthConstants.StepCount.SAMPLE_POSITION_TYPE))
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): StepCount {
            return StepCount().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    Count(data.getInt(ALIAS_TOTAL_COUNT), COUNT_UNIT),
                    Calorie(data.getFloat(ALIAS_TOTAL_CALORIE).roundedDecimal.toFloat(), CALORIE_UNIT),
                    Speed(data.getFloat(ALIAS_AVERAGE_SPEED).roundedDecimal.toFloat(), SPEED_UNIT),
                    Speed(data.getFloat(ALIAS_MAX_SPEED).roundedDecimal.toFloat(), SPEED_UNIT),
                    Speed(data.getFloat(ALIAS_MIN_SPEED).roundedDecimal.toFloat(), SPEED_UNIT),
                    Distance(
                        data.getFloat(ALIAS_TOTAL_DISTANCE).roundedDecimal.toFloat(),
                        HealthDataUnit.METER.unitName
                    )
                )
            }
        }
    }

    override val type = HealthConstants.StepCount.HEALTH_DATA_TYPE
    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
    override var insertResult: InsertResult? = null

    private constructor()

    constructor(insertResult: InsertResult) {
        this.insertResult = insertResult
    }

    @Throws(SamsungHealthWriteException::class)
    override fun asOriginal(healthDataStore: HealthDataStore): HealthData {
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
            putInt(HealthConstants.StepCount.COUNT, insertResult.count)
            putFloat(HealthConstants.StepCount.CALORIE, insertResult.calorie)
            putFloat(HealthConstants.StepCount.SPEED, insertResult.speed)
            putFloat(HealthConstants.StepCount.DISTANCE, insertResult.distance)
        }
    }
}