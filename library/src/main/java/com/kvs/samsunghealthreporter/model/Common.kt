package com.kvs.samsunghealthreporter.model

import com.google.gson.Gson
import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataStore

interface Common {
    interface CommonResult {
        val packageName: String
    }

    interface Factory<Result> where Result : Common {
        fun fromReadData(data: HealthData): Result
        fun fromAggregateData(data: HealthData, timeGroup: Time.Group): Result
    }

    val type: String
    val json: String get() = Gson().toJson(this)

    fun asOriginal(healthDataStore: HealthDataStore): HealthData
}