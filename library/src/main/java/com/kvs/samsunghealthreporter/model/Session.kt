package com.kvs.samsunghealthreporter.model

interface Session<ReadResult, AggregateResult> : Common {
    val endTimestamp: Long
    var readResult: ReadResult?
    var aggregateResult: AggregateResult?
}
