package com.kvs.samsunghealthreporter.model

import com.google.gson.Gson
import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

interface Common {
    interface ReadResult {
        val packageName: String
        val uuid: String
        val deviceUuid: String?
        val custom: String?
        val createTime: Long
        val updateTime: Long
        val startTime: Long
        val timeOffset: Long
    }

    interface AggregateResult {
        val time: Time
    }

    interface InsertResult {
        val packageName: String
        val startDate: Date
        val timeOffset: Long
    }

    interface Factory<Result> where Result : Common {
        fun fromReadData(data: HealthData): Result
        fun fromAggregateData(data: HealthData, timeGroup: Time.Group): Result
    }

    val type: String
    val json: String get() = Gson().toJson(this)

    fun asOriginal(healthDataStore: HealthDataStore): HealthData
}