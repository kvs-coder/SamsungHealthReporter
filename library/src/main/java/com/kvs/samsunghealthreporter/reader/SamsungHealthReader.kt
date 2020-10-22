package com.kvs.samsunghealthreporter.reader

import com.kvs.samsunghealthreporter.reader.resolver.StepCountResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthDeviceManager

class SamsungHealthReader(
    private val healthDataStore: HealthDataStore
) {
    val stepCountResolver
        get() = StepCountResolver(healthDataStore)
}