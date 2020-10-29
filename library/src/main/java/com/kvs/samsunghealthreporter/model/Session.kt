package com.kvs.samsunghealthreporter.model

import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

internal interface Session<ReadResult, AggregateResult, InsertResult> :
    Common where ReadResult : Session.ReadResult,
                 InsertResult : Session.InsertResult {
    interface ReadResult : Common.CommonResult {
        val startTime: Long
        val timeOffset: Long
        val endTime: Long
        val uuid: String
        val createTime: Long
        val updateTime: Long
        val deviceUuid: String?
        val custom: String?
    }

    interface AggregateResult {
        val time: Time
    }

    interface InsertResult : Common.CommonResult {
        val startDate: Date
        val timeOffset: Long
        val endDate: Date
    }

    var readResult: ReadResult?
    var aggregateResult: AggregateResult?
    var insertResult: InsertResult?

    fun asOriginal(healthDataStore: HealthDataStore): HealthData
}
