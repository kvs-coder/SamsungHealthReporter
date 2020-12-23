package com.kvs.samsunghealthreporter.model.discrete

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.*
import java.util.*

class Albumin: Discrete<Albumin.ReadResult, Albumin.AggregateResult, Albumin.InsertResult> {
    data class ReadResult(
        override val uuid: String,
        override val packageName: String,
        override val deviceUuid: String,
        override val custom: String?,
        override val createTime: Long,
        override val updateTime: Long,
        override val startTime: Long,
        override val timeOffset: Long,
        val albumin: Albumin,
    ) : Discrete.ReadResult {
        data class Albumin(val value: Float, val unit: String)
    }

    data class AggregateResult(
        override val time: Time,
        val albumin: Albumin,
    ) : Discrete.AggregateResult {
        data class Albumin(
            val min: Int,
            val max: Int,
            val avg: Int,
            val sum: Int,
            val count: Int,
            val unit: String
        )
    }

    data class InsertResult(
        override val packageName: String,
        override val startDate: Date,
        override val timeOffset: Long,
        val albumin: Float,
    ) : Discrete.InsertResult

    companion object : Common.Factory<Albumin> {
        private const val ALIAS_MIN_ALBUMIN = "albumin_min"
        private const val ALIAS_MAX_ALBUMIN = "albumin_max"
        private const val ALIAS_AVG_ALBUMIN = "albumin_avg"
        private const val ALIAS_SUM_ALBUMIN = "albumin_sum"
        private const val ALIAS_COUNT_ALBUMIN = "albumin_count"

        override fun fromReadData(data: HealthData): Albumin {
            return Albumin().apply {
                readResult = ReadResult(
                    data.getString(HealthConstants.Albumin.UUID),
                    data.getString(HealthConstants.Albumin.PACKAGE_NAME),
                    data.getString(HealthConstants.Albumin.DEVICE_UUID),
                    data.getString(HealthConstants.Albumin.CUSTOM),
                    data.getLong(HealthConstants.Albumin.CREATE_TIME),
                    data.getLong(HealthConstants.Albumin.UPDATE_TIME),
                    data.getLong(HealthConstants.Albumin.START_TIME),
                    data.getLong(HealthConstants.Albumin.TIME_OFFSET),
                    ReadResult.Albumin(
                        data.getFloat(HealthConstants.Albumin.ALBUMIN),
                        HealthDataUnit.GRAMS_PER_DECILITER.unitName
                    ),
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): Albumin {
            return Albumin().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Albumin(
                        data.getInt(ALIAS_MIN_ALBUMIN),
                        data.getInt(ALIAS_MAX_ALBUMIN),
                        data.getInt(ALIAS_AVG_ALBUMIN),
                        data.getInt(ALIAS_SUM_ALBUMIN),
                        data.getInt(ALIAS_COUNT_ALBUMIN),
                        HealthDataUnit.GRAMS_PER_DECILITER.unitName
                    )
                )
            }
        }
    }

    override val type = HealthConstants.Albumin.HEALTH_DATA_TYPE
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
            putString(HealthConstants.Albumin.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.Albumin.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.Albumin.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.Albumin.TIME_OFFSET, insertResult.timeOffset)
            putFloat(HealthConstants.Albumin.ALBUMIN, insertResult.albumin)
        }
    }
}