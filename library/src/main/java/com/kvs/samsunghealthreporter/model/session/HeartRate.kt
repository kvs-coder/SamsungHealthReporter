package com.kvs.samsunghealthreporter.model.session

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.*
import java.util.*


class HeartRate :
    Session<HeartRate.ReadResult, HeartRate.AggregateResult, HeartRate.InsertResult> {
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
        val rate: Rate,
        val beatCount: BeatCount,
        val binningData: BinningData?,
        val comment: String?,
    ) : Session.ReadResult {
        data class Rate(val value: Float, val unit: String)

        data class BeatCount(val value: Int, val unit: String)

        data class BinningData(
            val raw: List<Raw>,
            val min: Min,
            val max: Max
        ) {
            class Raw {
                @SerializedName("heart_rate")
                @Expose
                var heartRate: Float? = null

                @SerializedName("heart_rate_min")
                @Expose
                var heartRateMin: Float? = null

                @SerializedName("heart_rate_max")
                @Expose
                var heartRateMax: Float? = null

                @SerializedName("start_time")
                @Expose
                var startTime: Long? = null

                @SerializedName("end_time")
                @Expose
                var endTime: Long? = null
            }

            data class Min(val value: Long, val unit: String)
            data class Max(val value: Long, val unit: String)
        }
    }

    data class AggregateResult(
        override val time: Time,
        val rate: Rate
    ) :
        Session.AggregateResult {
        data class Rate(
            val min: Float,
            val max: Float,
            val avg: Float,
            val unit: String
        )
    }

    data class InsertResult(
        override val packageName: String,
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        val heartRate: Float,
        val beatCount: Int
    ) : Session.InsertResult

    companion object : Common.Factory<HeartRate> {
        private const val BPM_UNIT = "bpm"
        private const val COUNT_UNIT = "count"
        private const val ALIAS_MIN_HEART_RATE = "heart_rate_min"
        private const val ALIAS_MAX_HEART_RATE = "heart_rate_max"
        private const val ALIAS_AVG_HEART_RATE = "heart_rate_avg"

        override fun fromReadData(data: HealthData): HeartRate {
            return HeartRate().apply {
                val binningData =
                    if (data.getBlob(HealthConstants.HeartRate.BINNING_DATA) != null) ReadResult.BinningData(
                        HealthDataUtil.getStructuredDataList(
                            data.getBlob(HealthConstants.HeartRate.BINNING_DATA),
                            ReadResult.BinningData.Raw::class.java
                        ),
                        ReadResult.BinningData.Min(
                            data.getLong(HealthConstants.HeartRate.MIN),
                            BPM_UNIT
                        ),
                        ReadResult.BinningData.Max(
                            data.getLong(HealthConstants.HeartRate.MAX),
                            BPM_UNIT
                        )
                    ) else null
                readResult = ReadResult(
                    data.getString(HealthConstants.HeartRate.UUID),
                    data.getString(HealthConstants.HeartRate.PACKAGE_NAME),
                    data.getString(HealthConstants.HeartRate.DEVICE_UUID),
                    data.getString(HealthConstants.HeartRate.CUSTOM),
                    data.getLong(HealthConstants.HeartRate.CREATE_TIME),
                    data.getLong(HealthConstants.HeartRate.UPDATE_TIME),
                    data.getLong(HealthConstants.HeartRate.START_TIME),
                    data.getLong(HealthConstants.HeartRate.TIME_OFFSET),
                    data.getLong(HealthConstants.HeartRate.END_TIME),
                    ReadResult.Rate(data.getFloat(HealthConstants.HeartRate.HEART_RATE), BPM_UNIT),
                    ReadResult.BeatCount(
                        data.getInt(HealthConstants.HeartRate.HEART_BEAT_COUNT),
                        COUNT_UNIT
                    ),
                    binningData,
                    data.getString(HealthConstants.HeartRate.COMMENT)
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): HeartRate {
            return HeartRate().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Rate(
                        data.getFloat(ALIAS_MIN_HEART_RATE),
                        data.getFloat(ALIAS_MAX_HEART_RATE),
                        data.getFloat(ALIAS_AVG_HEART_RATE),
                        BPM_UNIT
                    )
                )
            }
        }
    }

    override val type = HealthConstants.HeartRate.HEALTH_DATA_TYPE
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
            putString(HealthConstants.HeartRate.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.HeartRate.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.HeartRate.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.HeartRate.TIME_OFFSET, insertResult.timeOffset)
            putLong(HealthConstants.HeartRate.END_TIME, insertResult.endDate.time)
            putFloat(HealthConstants.HeartRate.HEART_RATE, insertResult.heartRate)
            putInt(HealthConstants.HeartRate.HEART_BEAT_COUNT, insertResult.beatCount)
        }
    }
}