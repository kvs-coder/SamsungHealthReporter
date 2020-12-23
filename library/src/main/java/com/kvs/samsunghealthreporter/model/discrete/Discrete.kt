package com.kvs.samsunghealthreporter.model.discrete

import com.kvs.samsunghealthreporter.model.Common

internal interface Discrete<ReadResult, AggregateResult, InsertResult> :
    Common where ReadResult : Discrete.ReadResult,
                 InsertResult : Discrete.InsertResult {
    interface ReadResult : Common.ReadResult

    interface AggregateResult : Common.AggregateResult

    interface InsertResult : Common.InsertResult

    var readResult: ReadResult?
    var aggregateResult: AggregateResult?
    var insertResult: InsertResult?
}
