package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import java.util.*

internal interface Session<ReadResult, AggregateResult, InsertResult> :
    Common where ReadResult : Session.ReadResult,
                 InsertResult : Session.InsertResult {
    interface ReadResult : Common.CommonResult {
        val uuid: String
        val deviceUuid: String?
        val custom: String?
        val createTime: Long
        val updateTime: Long
        val startTime: Long
        val timeOffset: Long
        val endTime: Long
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
}
