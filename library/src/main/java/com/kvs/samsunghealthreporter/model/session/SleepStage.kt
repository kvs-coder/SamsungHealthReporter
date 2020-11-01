package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class SleepStage :
    Session<SleepStage.ReadResult, SleepStage.AggregateResult, SleepStage.InsertResult> {
    data class Stage(val id: Int) {
        val description = when (id) {
            HealthConstants.SleepStage.STAGE_AWAKE -> "awake"
            HealthConstants.SleepStage.STAGE_LIGHT -> "light"
            HealthConstants.SleepStage.STAGE_DEEP -> "deep"
            HealthConstants.SleepStage.STAGE_REM -> "rem"
            else -> "unknown"
        }
    }

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
        val id: String,
        val stage: Stage
    ) : Session.ReadResult

    data class AggregateResult(
        override val time: Time,
        val description: String,
        val sleep: Sleep
    ) : Session.AggregateResult {
        data class Sleep(
            var awake: Long? = null,
            var light: Long? = null,
            var deep: Long? = null,
            var rem: Long? = null,
            val unit: String
        )
    }

    data class InsertResult(
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        override val packageName: String
    ) : Session.InsertResult

    companion object : Common.Factory<SleepStage> {
        private const val MINUTE_UNIT = "min"
        const val ALIAS_END_TIME = "alias_end_time"
        const val ALIAS_START_TIME = "alias_start_time"
        const val ALIAS_STAGE = "alias_stage"

        override fun fromReadData(data: HealthData): SleepStage {
            return SleepStage().apply {
                readResult = ReadResult(
                    data.getString(HealthConstants.SleepStage.UUID),
                    data.getString(HealthConstants.SleepStage.PACKAGE_NAME),
                    data.getString(HealthConstants.SleepStage.DEVICE_UUID),
                    data.getString(HealthConstants.SleepStage.CUSTOM),
                    data.getLong(HealthConstants.SleepStage.CREATE_TIME),
                    data.getLong(HealthConstants.SleepStage.UPDATE_TIME),
                    data.getLong(HealthConstants.SleepStage.START_TIME),
                    data.getLong(HealthConstants.SleepStage.TIME_OFFSET),
                    data.getLong(HealthConstants.SleepStage.END_TIME),
                    data.getString(HealthConstants.SleepStage.SLEEP_ID),
                    Stage(data.getInt(HealthConstants.SleepStage.STAGE))
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): SleepStage {
            return SleepStage().apply {
                val endTime = data.getLong(ALIAS_END_TIME)
                val startTime = data.getLong(ALIAS_START_TIME)
                val totalMinutes = endTime.minus(startTime)
                val stage = Stage(data.getInt(ALIAS_STAGE))
                val sleep = when (stage.id) {
                    HealthConstants.SleepStage.STAGE_AWAKE -> AggregateResult.Sleep(
                        awake = totalMinutes,
                        unit = MINUTE_UNIT
                    )
                    HealthConstants.SleepStage.STAGE_LIGHT -> AggregateResult.Sleep(
                        light = totalMinutes,
                        unit = MINUTE_UNIT
                    )
                    HealthConstants.SleepStage.STAGE_DEEP -> AggregateResult.Sleep(
                        deep = totalMinutes,
                        unit = MINUTE_UNIT
                    )
                    HealthConstants.SleepStage.STAGE_REM -> AggregateResult.Sleep(
                        rem = totalMinutes,
                        unit = MINUTE_UNIT
                    )
                    else -> AggregateResult.Sleep(0, 0, 0, 0, MINUTE_UNIT)
                }
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    stage.description,
                    sleep
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
        TODO("Not yet implemented")
    }
}