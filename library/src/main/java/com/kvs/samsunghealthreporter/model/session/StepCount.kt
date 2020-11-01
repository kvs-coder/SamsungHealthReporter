package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.decorator.roundedDecimal
import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.*
import java.util.*

class StepCount : Session<StepCount.ReadResult, StepCount.AggregateResult, StepCount.InsertResult> {
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
    ) : Session.ReadResult {
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
    }

    data class AggregateResult(
        override val time: Time,
        val count: Count,
        val calorie: Calorie,
        val speed: Speed,
        val distance: Distance
    ) : Session.AggregateResult {
        data class Count(
            val min: Int,
            val max: Int,
            val avg: Int,
            val sum: Int,
            val count: Int,
            val unit: String
        )

        data class Calorie(
            val min: Float,
            val max: Float,
            val avg: Float,
            val sum: Float,
            val count: Float,
            val unit: String
        )

        data class Speed(
            val min: Float,
            val max: Float,
            val avg: Float,
            val unit: String
        )

        data class Distance(
            val min: Float,
            val max: Float,
            val avg: Float,
            val sum: Float,
            val count: Float,
            val unit: String
        )
    }

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
        private const val ALIAS_MIN_COUNT = "count_min"
        private const val ALIAS_MAX_COUNT = "count_max"
        private const val ALIAS_AVG_COUNT = "count_avg"
        private const val ALIAS_SUM_COUNT = "count_sum"
        private const val ALIAS_COUNT_COUNT = "count_count"
        private const val ALIAS_MIN_CALORIE = "calorie_min"
        private const val ALIAS_MAX_CALORIE = "calorie_max"
        private const val ALIAS_AVG_CALORIE = "calorie_avg"
        private const val ALIAS_SUM_CALORIE = "calorie_sum"
        private const val ALIAS_COUNT_CALORIE = "calorie_count"
        private const val ALIAS_MIN_SPEED = "speed_min"
        private const val ALIAS_MAX_SPEED = "speed_max"
        private const val ALIAS_AVG_SPEED = "speed_avg"
        private const val ALIAS_MIN_DISTANCE = "distance_min"
        private const val ALIAS_MAX_DISTANCE = "distance_max"
        private const val ALIAS_AVG_DISTANCE = "distance_avg"
        private const val ALIAS_SUM_DISTANCE = "distance_sum"
        private const val ALIAS_COUNT_DISTANCE = "distance_count"

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
                    ReadResult.Count(data.getInt(HealthConstants.StepCount.COUNT), COUNT_UNIT),
                    ReadResult.Calorie(
                        data.getFloat(HealthConstants.StepCount.CALORIE).roundedDecimal.toFloat(),
                        CALORIE_UNIT
                    ),
                    ReadResult.Speed(
                        data.getFloat(HealthConstants.StepCount.SPEED).roundedDecimal.toFloat(),
                        SPEED_UNIT
                    ),
                    ReadResult.Distance(
                        data.getFloat(HealthConstants.StepCount.DISTANCE).roundedDecimal.toFloat(),
                        HealthDataUnit.METER.unitName
                    ),
                    ReadResult.Position(data.getInt(HealthConstants.StepCount.SAMPLE_POSITION_TYPE))
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): StepCount {
            return StepCount().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Count(
                        data.getInt(ALIAS_MIN_COUNT),
                        data.getInt(ALIAS_MAX_COUNT),
                        data.getInt(ALIAS_AVG_COUNT),
                        data.getInt(ALIAS_SUM_COUNT),
                        data.getInt(ALIAS_COUNT_COUNT),
                        COUNT_UNIT),
                    AggregateResult.Calorie(
                        data.getFloat(ALIAS_MIN_CALORIE),
                        data.getFloat(ALIAS_MAX_CALORIE),
                        data.getFloat(ALIAS_AVG_CALORIE),
                        data.getFloat(ALIAS_SUM_CALORIE),
                        data.getFloat(ALIAS_COUNT_CALORIE),
                        CALORIE_UNIT),
                    AggregateResult.Speed(
                        data.getFloat(ALIAS_MIN_SPEED),
                        data.getFloat(ALIAS_MAX_SPEED),
                        data.getFloat(ALIAS_AVG_SPEED),
                        SPEED_UNIT),
                    AggregateResult.Distance(
                        data.getFloat(ALIAS_MIN_DISTANCE),
                        data.getFloat(ALIAS_MAX_DISTANCE),
                        data.getFloat(ALIAS_AVG_DISTANCE),
                        data.getFloat(ALIAS_SUM_DISTANCE),
                        data.getFloat(ALIAS_COUNT_DISTANCE),
                        HealthDataUnit.METER.unitName),
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