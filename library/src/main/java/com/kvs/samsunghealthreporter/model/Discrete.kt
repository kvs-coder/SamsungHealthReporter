package com.kvs.samsunghealthreporter.model

interface Discrete<ReadResult> : Common {
    var readResult: ReadResult?
}