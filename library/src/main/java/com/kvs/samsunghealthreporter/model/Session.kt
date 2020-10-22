package com.kvs.samsunghealthreporter.model

interface Session<ReadResult, AggregateResult, InsertResult> : Common {
    val endTimestamp: Long
    var readResult: ReadResult?
    var aggregateResult: AggregateResult?
    var insertResult: InsertResult?
}
