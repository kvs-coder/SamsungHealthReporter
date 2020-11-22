package com.kvs.samsunghealthreporter.resolver

import com.samsung.android.sdk.healthdata.HealthDataStore

class SamsungHealthResolver(
    private val healthDataStore: HealthDataStore
) {
    val stepCountResolver
        get() = StepCountResolver(healthDataStore)
    val heartRateResolver
        get() = HeartRateResolver(healthDataStore)
}