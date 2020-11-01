package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.*
import com.samsung.android.sdk.healthdata.*
import java.util.*

class FloorsClimbed: Session<FloorsClimbed.ReadResult, FloorsClimbed.AggregateResult, FloorsClimbed.InsertResult> {
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
        val floor: Floor
    ) : Session.ReadResult {
        data class Floor(val value: Float, val unit: String)
    }

    data class AggregateResult(
        override val time: Time,
        val floor: Floor
    ) : Session.AggregateResult {
        data class Floor(
            val min: Float,
            val max: Float,
            val avg: Float,
            val total: Float,
            val unit: String
        )
    }

    data class InsertResult(
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        override val packageName: String,
        val floor: Float
    ) : Session.InsertResult

    companion object : Common.Factory<FloorsClimbed> {
        private const val COUNT_UNIT = "count"
        private const val ALIAS_MIN_FLOOR = "floor_min"
        private const val ALIAS_MAX_FLOOR = "floor_max"
        private const val ALIAS_AVG_FLOOR = "floor_avg"
        private const val ALIAS_TOTAL_FLOOR = "floor_avg"

        override fun fromReadData(data: HealthData): FloorsClimbed {
            return FloorsClimbed().apply {
                readResult = ReadResult(
                    data.getString(HealthConstants.FloorsClimbed.UUID),
                    data.getString(HealthConstants.FloorsClimbed.PACKAGE_NAME),
                    data.getString(HealthConstants.FloorsClimbed.DEVICE_UUID),
                    data.getString(HealthConstants.FloorsClimbed.CUSTOM),
                    data.getLong(HealthConstants.FloorsClimbed.CREATE_TIME),
                    data.getLong(HealthConstants.FloorsClimbed.UPDATE_TIME),
                    data.getLong(HealthConstants.FloorsClimbed.START_TIME),
                    data.getLong(HealthConstants.FloorsClimbed.TIME_OFFSET),
                    data.getLong(HealthConstants.FloorsClimbed.END_TIME),
                    ReadResult.Floor(data.getFloat(HealthConstants.FloorsClimbed.FLOOR), COUNT_UNIT)
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): FloorsClimbed {
            return FloorsClimbed().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Floor(
                        data.getFloat(ALIAS_MIN_FLOOR),
                        data.getFloat(ALIAS_MAX_FLOOR),
                        data.getFloat(ALIAS_AVG_FLOOR),
                        data.getFloat(ALIAS_TOTAL_FLOOR),
                        COUNT_UNIT
                    )
                )
            }
        }

    }

    override val type = HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE
    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
    override var insertResult: InsertResult? = null

    private constructor()

    constructor(insertResult: InsertResult) {
        this.insertResult = insertResult
    }

    override fun asOriginal(healthDataStore: HealthDataStore): HealthData {
        val insertResult = this.insertResult ?: throw SamsungHealthWriteException(
            "Insert result was null, nothing to write in Samsung Health"
        )
        val deviceUuid = HealthDeviceManager(healthDataStore).localDevice.uuid
        return HealthData().apply {
            sourceDevice = deviceUuid
            putString(HealthConstants.FloorsClimbed.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.FloorsClimbed.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.FloorsClimbed.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.FloorsClimbed.TIME_OFFSET, insertResult.timeOffset)
            putLong(HealthConstants.FloorsClimbed.END_TIME, insertResult.endDate.time)
            putFloat(HealthConstants.FloorsClimbed.FLOOR, insertResult.floor)
        }
    }
}