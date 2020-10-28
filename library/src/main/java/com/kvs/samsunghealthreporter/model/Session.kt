package com.kvs.samsunghealthreporter.model

interface Session<ReadResult, AggregateResult, InsertResult> : Common {
    //val endTime: Long
    var readResult: ReadResult?
    var aggregateResult: AggregateResult?
    var insertResult: InsertResult?
}
