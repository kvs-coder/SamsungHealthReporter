package com.kvs.samsunghealthreporter.model

enum class AggregateFunction(private val suffix: String) {
    MIN("min"),
    MAX("max"),
    AVG("avg"),
    SUM("sum"),
    COUNT("count");

    fun asAlias(type: String) = "${type}_$suffix"
}