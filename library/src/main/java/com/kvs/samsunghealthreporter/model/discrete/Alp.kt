package com.kvs.samsunghealthreporter.model.discrete

import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.*
import java.util.*

class Alp: Discrete<Alp.ReadResult, Alp.AggregateResult, Alp.InsertResult> {
    data class ReadResult(
        override val uuid: String,
        override val packageName: String,
        override val deviceUuid: String,
        override val custom: String?,
        override val createTime: Long,
        override val updateTime: Long,
        override val startTime: Long,
        override val timeOffset: Long,
        val alp: Alp,
    ) : Discrete.ReadResult {
        data class Alp(val value: Float, val unit: String)
    }

    data class AggregateResult(
        override val time: Time,
        val alp: Alp,
    ) : Discrete.AggregateResult {
        data class Alp(
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
        val alp: Float,
    ) : Discrete.InsertResult

    companion object : Common.Factory<Alp> {
        private const val INTERNATIONAL_UNIT_PER_LITER = "IU/L"
        private const val ALIAS_MIN_ALBUMIN = "albumin_min"
        private const val ALIAS_MAX_ALBUMIN = "albumin_max"
        private const val ALIAS_AVG_ALBUMIN = "albumin_avg"
        private const val ALIAS_SUM_ALBUMIN = "albumin_sum"
        private const val ALIAS_COUNT_ALBUMIN = "albumin_count"

        override fun fromReadData(data: HealthData): Alp {
            return Alp().apply {
                readResult = ReadResult(
                    data.getString(HealthConstants.Alp.UUID),
                    data.getString(HealthConstants.Alp.PACKAGE_NAME),
                    data.getString(HealthConstants.Alp.DEVICE_UUID),
                    data.getString(HealthConstants.Alp.CUSTOM),
                    data.getLong(HealthConstants.Alp.CREATE_TIME),
                    data.getLong(HealthConstants.Alp.UPDATE_TIME),
                    data.getLong(HealthConstants.Alp.START_TIME),
                    data.getLong(HealthConstants.Alp.TIME_OFFSET),
                    ReadResult.Alp(
                        data.getFloat(HealthConstants.Alp.ALP),
                        INTERNATIONAL_UNIT_PER_LITER
                    ),
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): Alp {
            return Alp().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Alp(
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

    override val type = HealthConstants.Alp.HEALTH_DATA_TYPE
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
            putString(HealthConstants.Alp.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.Alp.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.Alp.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.Alp.TIME_OFFSET, insertResult.timeOffset)
            putFloat(HealthConstants.Alp.ALP, insertResult.alp)
        }
    }
}