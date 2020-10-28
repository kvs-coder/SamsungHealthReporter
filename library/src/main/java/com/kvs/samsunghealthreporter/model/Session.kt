package com.kvs.samsunghealthreporter.model

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

    interface AggregateResult : Common.CommonResult {
        val time: Time
    }

    interface InsertResult : Common.CommonResult

    var readResult: ReadResult?
    var aggregateResult: AggregateResult?
    var insertResult: InsertResult?
}
