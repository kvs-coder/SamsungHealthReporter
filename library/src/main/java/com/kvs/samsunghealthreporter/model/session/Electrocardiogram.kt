package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.*
import com.samsung.android.sdk.healthdata.*
import java.util.*

class Electrocardiogram :
    Session<Electrocardiogram.ReadResult, Electrocardiogram.AggregateResult, Electrocardiogram.InsertResult> {
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
        val sample: Sample,
        val heartRate: HeartRate,
        val comment: String,
        val data: Data?
    ) : Session.ReadResult {
        data class Data(val raw: Raw, val format: String) {
            class Raw
        }

        data class Sample(
            val frequency: Frequency,
            val count: Count
        ) {
            data class Frequency(val value: Int, val unit: String)
            data class Count(val value: Int, val unit: String)
        }

        data class HeartRate(
            val min: Float,
            val mean: Float,
            val max: Float,
            val unit: String
        )
    }

    data class AggregateResult(
        override val time: Time,
        val sample: Sample,
        val heartRate: HeartRate
    ) : Session.AggregateResult {
        data class Sample(
            val frequency: Frequency,
            val count: Count
        ) {
            data class Frequency(
                val min: Float,
                val max: Float,
                val avg: Float,
                val unit: String
            )

            data class Count(
                val min: Float,
                val max: Float,
                val avg: Float, val unit: String
            )
        }

        data class HeartRate(
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

    companion object : Common.Factory<Electrocardiogram> {
        private const val BPM_UNIT = "bpm"
        private const val COUNT_UNIT = "count"
        private const val HERTZ_UNIT = "Hz"
        private const val ALIAS_MIN_HEART_RATE = "heart_rate_min"
        private const val ALIAS_MAX_HEART_RATE = "heart_rate_max"
        private const val ALIAS_AVG_HEART_RATE = "heart_rate_avg"
        private const val ALIAS_MIN_SAMPLE_FREQUENCY = "sample_frequency_min"
        private const val ALIAS_MAX_SAMPLE_FREQUENCY = "sample_frequency_max"
        private const val ALIAS_AVG_SAMPLE_FREQUENCY = "sample_frequency_avg"
        private const val ALIAS_MIN_SAMPLE_COUNT = "sample_count_min"
        private const val ALIAS_MAX_SAMPLE_COUNT = "sample_count_max"
        private const val ALIAS_AVG_SAMPLE_COUNT = "sample_count_avg"

        override fun fromReadData(data: HealthData): Electrocardiogram {
            return Electrocardiogram().apply {
                val electrocardiogramData =
                    if (data.getBlob(HealthConstants.Electrocardiogram.DATA) != null) HealthDataUtil.getStructuredData(
                        data.getBlob(HealthConstants.Electrocardiogram.DATA),
                        ReadResult.Data::class.java
                    ) else null
                readResult = ReadResult(
                    data.getString(HealthConstants.Electrocardiogram.UUID),
                    data.getString(HealthConstants.Electrocardiogram.PACKAGE_NAME),
                    data.getString(HealthConstants.Electrocardiogram.DEVICE_UUID),
                    data.getString(HealthConstants.Electrocardiogram.CUSTOM),
                    data.getLong(HealthConstants.Electrocardiogram.CREATE_TIME),
                    data.getLong(HealthConstants.Electrocardiogram.UPDATE_TIME),
                    data.getLong(HealthConstants.Electrocardiogram.START_TIME),
                    data.getLong(HealthConstants.Electrocardiogram.TIME_OFFSET),
                    data.getLong(HealthConstants.Electrocardiogram.END_TIME),
                    ReadResult.Sample(
                        ReadResult.Sample.Frequency(
                            data.getInt(HealthConstants.Electrocardiogram.SAMPLE_FREQUENCY),
                            HERTZ_UNIT
                        ),
                        ReadResult.Sample.Count(
                            data.getInt(HealthConstants.Electrocardiogram.SAMPLE_COUNT),
                            COUNT_UNIT
                        )
                    ),
                    ReadResult.HeartRate(
                        data.getFloat(HealthConstants.Electrocardiogram.MIN_HEART_RATE),
                        data.getFloat(HealthConstants.Electrocardiogram.MEAN_HEART_RATE),
                        data.getFloat(HealthConstants.Electrocardiogram.MAX_HEART_RATE),
                        BPM_UNIT
                    ),
                    data.getString(HealthConstants.Electrocardiogram.COMMENT),
                    electrocardiogramData
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): Electrocardiogram {
            return Electrocardiogram().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Sample(
                        AggregateResult.Sample.Frequency(
                            data.getFloat(ALIAS_MIN_SAMPLE_FREQUENCY),
                            data.getFloat(ALIAS_MAX_SAMPLE_FREQUENCY),
                            data.getFloat(ALIAS_AVG_SAMPLE_FREQUENCY),
                            HERTZ_UNIT
                        ),
                        AggregateResult.Sample.Count(
                            data.getFloat(ALIAS_MIN_SAMPLE_COUNT),
                            data.getFloat(ALIAS_MAX_SAMPLE_COUNT),
                            data.getFloat(ALIAS_AVG_SAMPLE_COUNT),
                            COUNT_UNIT
                        )
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

    override val type = HealthConstants.Electrocardiogram.HEALTH_DATA_TYPE
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
            putString(HealthConstants.Electrocardiogram.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.Electrocardiogram.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.Electrocardiogram.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.Electrocardiogram.TIME_OFFSET, insertResult.timeOffset)
            putLong(HealthConstants.Electrocardiogram.END_TIME, insertResult.endDate.time)
        }
    }

}