package com.kvs.samsunghealthreporter.resolver

import com.samsung.android.sdk.healthdata.HealthDataStore

class SamsungHealthResolver(
    private val healthDataStore: HealthDataStore
) {
    val stepCount
        get() = StepCountResolver(healthDataStore)
    val heartRate
        get() = HeartRateResolver(healthDataStore)
    val electrocardiogram
        get() = ElectrocardiogramResolver(healthDataStore)
    val oxygenSaturation
        get() = OxygenSaturationResolver(healthDataStore)
    val sleep
        get() = SleepResolver(healthDataStore)
    val sleepStage
        get() = SleepStageResolver(healthDataStore)
    val exercise
        get() = ExerciseResolver(healthDataStore)
}