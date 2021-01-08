package com.kvs.samsunghealthreporter.model.session

import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.model.Time
import java.util.*

internal interface Session<ReadResult, AggregateResult, InsertResult> :
    Common where ReadResult : Session.ReadResult,
                 InsertResult : Session.InsertResult {
    interface ReadResult : Common.ReadResult {
        val endTime: Long
    }

    interface AggregateResult : Common.AggregateResult

    interface InsertResult : Common.InsertResult {
        val endDate: Date
    }

    var readResult: ReadResult?
    var aggregateResult: AggregateResult?
    var insertResult: InsertResult?
}
