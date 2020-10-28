package com.kvs.samsunghealthreporter.model

import com.google.gson.Gson
import com.samsung.android.sdk.healthdata.HealthData

interface Common {
    interface CommonResult {
        val packageName: String
    }

    interface Factory {
        fun fromReadData(data: HealthData): Common
        fun fromAggregateData(data: HealthData, timeGroup: Time.Group): Common
    }

    val type: String
    val json: String get() = Gson().toJson(this)
}