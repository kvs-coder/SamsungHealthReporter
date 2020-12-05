package com.kvs.samsunghealthreporter

import com.samsung.android.sdk.healthdata.HealthConstants

enum class HealthSessionType(override val string: String): HealthType {
    SLEEP(HealthConstants.Sleep.HEALTH_DATA_TYPE),
    SLEEP_STAGE(HealthConstants.SleepStage.HEALTH_DATA_TYPE),
    STEP_COUNT(HealthConstants.StepCount.HEALTH_DATA_TYPE),
    HEART_RATE(HealthConstants.HeartRate.HEALTH_DATA_TYPE),
    FLOORS_CLIMBED(HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE),
    EXERCISE(HealthConstants.Exercise.HEALTH_DATA_TYPE),
    OXYGEN_SATURATION(HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE),
    ELECTROCARDIOGRAM(HealthConstants.Electrocardiogram.HEALTH_DATA_TYPE)
}
