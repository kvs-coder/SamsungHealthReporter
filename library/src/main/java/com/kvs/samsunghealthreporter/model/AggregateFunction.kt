package com.kvs.samsunghealthreporter.model

enum class AggregateFunction(private val suffix: String) {
    SUM("sum"),
    MIN("min"),
    MAX("max"),
    AVG("avg"),
    COUNT("count");

    fun asAlias(type: String) = "${type}_$suffix"
}