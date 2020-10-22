package com.kvs.samsunghealthreporter.model

import com.google.gson.Gson

interface Common {
    val type: String
    val startTimestamp: Long
    val timeOffset: Long
    val deviceUuid: String?
    val packageName: String
    val json: String get() = Gson().toJson(this)
}