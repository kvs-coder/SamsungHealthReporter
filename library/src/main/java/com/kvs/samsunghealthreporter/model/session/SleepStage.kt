package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class SleepStage :
    Session<SleepStage.ReadResult, SleepStage.AggregateResult, SleepStage.InsertResult> {
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

    data class AggregateResult(override val time: Time) : Session.AggregateResult

    data class InsertResult(
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        override val packageName: String
    ) : Session.InsertResult

    companion object : Common.Factory<SleepStage> {
        override fun fromReadData(data: HealthData): SleepStage {
            TODO("Not yet implemented")
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): SleepStage {
            TODO("Not yet implemented")
        }
    }

    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
    override var insertResult: InsertResult? = null
    override val type = HealthConstants.Sleep.HEALTH_DATA_TYPE

    override fun asOriginal(healthDataStore: HealthDataStore): HealthData {
        TODO("Not yet implemented")
    }
}