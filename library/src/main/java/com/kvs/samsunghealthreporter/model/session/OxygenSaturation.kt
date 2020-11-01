package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.*
import java.util.*

class OxygenSaturation :
    Session<OxygenSaturation.ReadResult, OxygenSaturation.AggregateResult, OxygenSaturation.InsertResult> {
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
        val spo2: Spo2,
        val heartRate: HeartRate,
        val comment: String?
    ) : Session.ReadResult {
        data class Spo2(val value: Float, val unit: String)
        data class HeartRate(val value: Float, val unit: String)
    }

    data class AggregateResult(
        override val time: Time,
        val spo2: Spo2,
        val heartRate: HeartRate
    ) : Session.AggregateResult {
        data class Spo2(
            val min: Float,
            val max: Float,
            val avg: Float,
            val unit: String
        )

        data class HeartRate(
            val min: Float,
            val max: Float,
            val avg: Float,
            val unit: String
        )
    }

    data class InsertResult(
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        override val packageName: String,
        val spo2: Float,
        val heartRate: Float
    ) : Session.InsertResult

    companion object : Common.Factory<OxygenSaturation> {
        private const val PERCENT_UNIT = "%"
        private const val BPM_UNIT = "bpm"
        private const val ALIAS_MAX_SPO2 = "spo2_max"
        private const val ALIAS_MIN_SPO2 = "spo2_min"
        private const val ALIAS_AVG_SPO2 = "spo2_avg"
        private const val ALIAS_MAX_HEART_RATE = "heart_rate_max"
        private const val ALIAS_MIN_HEART_RATE = "heart_rate_min"
        private const val ALIAS_AVG_HEART_RATE = "heart_rate_avg"

        override fun fromReadData(data: HealthData): OxygenSaturation {
            return OxygenSaturation().apply {
                readResult = ReadResult(
                    data.getString(HealthConstants.OxygenSaturation.UUID),
                    data.getString(HealthConstants.OxygenSaturation.PACKAGE_NAME),
                    data.getString(HealthConstants.OxygenSaturation.DEVICE_UUID),
                    data.getString(HealthConstants.OxygenSaturation.CUSTOM),
                    data.getLong(HealthConstants.OxygenSaturation.CREATE_TIME),
                    data.getLong(HealthConstants.OxygenSaturation.UPDATE_TIME),
                    data.getLong(HealthConstants.OxygenSaturation.START_TIME),
                    data.getLong(HealthConstants.OxygenSaturation.TIME_OFFSET),
                    data.getLong(HealthConstants.OxygenSaturation.END_TIME),
                    ReadResult.Spo2(
                        data.getFloat(HealthConstants.OxygenSaturation.SPO2),
                        PERCENT_UNIT
                    ),
                    ReadResult.HeartRate(
                        data.getFloat(HealthConstants.OxygenSaturation.HEART_RATE),
                        BPM_UNIT
                    ),
                    data.getString(HealthConstants.OxygenSaturation.COMMENT),
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): OxygenSaturation {
            return OxygenSaturation().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Spo2(
                        data.getFloat(ALIAS_MIN_SPO2),
                        data.getFloat(ALIAS_MAX_SPO2),
                        data.getFloat(ALIAS_AVG_SPO2),
                        PERCENT_UNIT
                    ),
                    AggregateResult.HeartRate(
                        data.getFloat(ALIAS_MIN_HEART_RATE),
                        data.getFloat(ALIAS_MAX_HEART_RATE),
                        data.getFloat(ALIAS_AVG_HEART_RATE),
                        BPM_UNIT
                    )
                )
            }
        }

    }

    override val type = HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE
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
            putString(HealthConstants.OxygenSaturation.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.OxygenSaturation.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.OxygenSaturation.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.OxygenSaturation.TIME_OFFSET, insertResult.timeOffset)
            putLong(HealthConstants.OxygenSaturation.END_TIME, insertResult.endDate.time)
            putFloat(HealthConstants.OxygenSaturation.SPO2, insertResult.spo2)
            putFloat(HealthConstants.OxygenSaturation.HEART_RATE, insertResult.heartRate)
        }
    }
}