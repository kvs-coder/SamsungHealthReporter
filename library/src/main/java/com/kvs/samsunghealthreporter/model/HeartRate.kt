package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthDeviceManager
import java.util.*

class HeartRate :
    Session<HeartRate.ReadResult, HeartRate.AggregateResult, HeartRate.InsertRequest> {
    data class ReadResult(
        override val startTime: Long,
        override val timeOffset: Long,
        override val endTime: Long,
        override val uuid: String,
        override val createTime: Long,
        override val updateTime: Long,
        override val deviceUuid: String?,
        override val custom: String?,
        override val packageName: String
    ) : Session.ReadResult

    data class AggregateResult(
        override val time: Time,
        override val packageName: String,
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

    override val type: String
        get() = HealthConstants.HeartRate.HEALTH_DATA_TYPE
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