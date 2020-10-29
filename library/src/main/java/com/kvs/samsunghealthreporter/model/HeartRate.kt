package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthDeviceManager
import java.util.*

class HeartRate :
    Session<HeartRate.ReadResult, HeartRate.AggregateResult, HeartRate.InsertRequest> {
    data class Rate(val value: Long, val unit: String)

    data class BeatCount(val value: Long, val unit: String)

    data class Comment(val value: String?)

    data class Min(val value: Long, val unit: String)

    data class Max(val value: Long, val unit: String)

    data class BinningData(val value: Long)

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
//        val beatCount: BeatCount,
//        val comment: Comment,
//        val min: Min,
//        val max: Max,
//        val binningData: BinningData
    ) : Session.ReadResult

    data class AggregateResult(
        override val time: Time,
        //override val packageName: String,
    ) :
        Session.AggregateResult

    data class InsertRequest(
        override val packageName: String,
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        val heartRate: Double,
        val heartBeatCount: Double,
        val comment: String,
        val min: Double,
        val max: Double,
        val binningData: Double
    ) : Session.InsertResult

    companion object : Common.Factory<HeartRate> {
        override fun fromReadData(data: HealthData): HeartRate {
            return HeartRate().apply {
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
                    Rate(data.getLong(HealthConstants.HeartRate.HEART_RATE), "bpm"),
//                    BeatCount(data.getLong(HealthConstants.HeartRate.HEART_BEAT_COUNT), "count"),
//                    Comment(data.getString(HealthConstants.HeartRate.COMMENT)),
//                    Min(data.getLong(HealthConstants.HeartRate.MIN), "count/min"),
//                    Max(data.getLong(HealthConstants.HeartRate.MAX), "count/min"),
//                    BinningData(data.getLong(HealthConstants.HeartRate.BINNING_DATA))
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): HeartRate {
            return HeartRate()
        }
    }

    override val type = HealthConstants.HeartRate.HEALTH_DATA_TYPE
    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
    override var insertResult: InsertRequest? = null

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


            putDouble(HealthConstants.HeartRate.HEART_RATE, insertResult.heartRate)
            putDouble(HealthConstants.HeartRate.HEART_BEAT_COUNT, insertResult.heartBeatCount)
            putString(HealthConstants.HeartRate.COMMENT, insertResult.comment)
            putDouble(HealthConstants.HeartRate.MIN, insertResult.min)
            putDouble(HealthConstants.HeartRate.MAX, insertResult.max)
            putDouble(HealthConstants.HeartRate.BINNING_DATA, insertResult.binningData)
        }
    }
}