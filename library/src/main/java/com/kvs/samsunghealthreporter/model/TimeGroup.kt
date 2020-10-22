package com.kvs.samsunghealthreporter.model

enum class TimeGroup(val alias: String) {
    MINUTELY("minute"), // 1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30, 60
    HOURLY("hour"), //  1, 2, 3, 4, 6, 8, 12, 24
    DAILY("day"), // 1
    WEEKLY("week"), // 1
    MONTHLY("month"); // 1, 3, 6
}