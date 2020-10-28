package com.kvs.samsunghealthreporter.model

import com.google.gson.Gson

interface Common {
    interface CommonResult {
        val packageName: String
    }

    val type: String
    val json: String get() = Gson().toJson(this)
}