package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthDeviceManager
import java.util.*

class Sleep : Session<Sleep.ReadResult, Sleep.AggregateResult, Sleep.InsertResult> {
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
        val comment: String?
    ) : Session.ReadResult

    data class AggregateResult(override val time: Time, val sleep: Sleep) :
        Session.AggregateResult {
        data class Sleep(val total: Int, val unit: String)
    }

    data class InsertResult(
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        override val packageName: String,
        val comment: String?
    ) : Session.InsertResult

    companion object : Common.Factory<Sleep> {
        private const val MINUTE_UNIT = "min"
        const val ALIAS_END_TIME = "alias_end_time"
        const val ALIAS_START_TIME = "alias_start_time"

        override fun fromReadData(data: HealthData): Sleep {
            return Sleep().apply {
                readResult = ReadResult(
                    data.getString(HealthConstants.Sleep.UUID),
                    data.getString(HealthConstants.Sleep.PACKAGE_NAME),
                    data.getString(HealthConstants.Sleep.DEVICE_UUID),
                    data.getString(HealthConstants.Sleep.CUSTOM),
                    data.getLong(HealthConstants.Sleep.CREATE_TIME),
                    data.getLong(HealthConstants.Sleep.UPDATE_TIME),
                    data.getLong(HealthConstants.Sleep.START_TIME),
                    data.getLong(HealthConstants.Sleep.TIME_OFFSET),
                    data.getLong(HealthConstants.Sleep.END_TIME),
                    data.getString(HealthConstants.Sleep.COMMENT)
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): Sleep {
            return Sleep().apply {
                val endTime = data.getLong(ALIAS_END_TIME)
                val startTime = data.getLong(ALIAS_START_TIME)
                val totalMinutes = endTime.minus(startTime)
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Sleep(totalMinutes.toInt(), MINUTE_UNIT)
                )
            }
        }
    }

    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
    override var insertResult: InsertResult? = null
    override val type = HealthConstants.Sleep.HEALTH_DATA_TYPE

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
            putString(HealthConstants.Sleep.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.Sleep.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.Sleep.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.Sleep.TIME_OFFSET, insertResult.timeOffset)
            putLong(HealthConstants.Sleep.END_TIME, insertResult.endDate.time)
            putString(HealthConstants.Sleep.COMMENT, insertResult.comment)
        }
    }
}