package com.kvs.samsunghealthreporter

import com.samsung.android.sdk.healthdata.HealthConstants

enum class SamsungHealthMeasurementType(val string: String) {
    DISCRETE("discrete"),
    SESSION("session")
}

enum class SamsungHealthType(val stringValue: String, val measurementType: SamsungHealthMeasurementType) {
    SLEEP(HealthConstants.Sleep.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.SESSION),
    SLEEP_STAGE(HealthConstants.SleepStage.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.SESSION),
    STEP_COUNT(HealthConstants.StepCount.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.SESSION),
    HEART_RATE(HealthConstants.HeartRate.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.SESSION),
    WEIGHT(HealthConstants.Weight.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    HEIGHT(HealthConstants.Height.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    FLOORS_CLIMBED(HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.SESSION),
    WAIST_CIRCUMFERENCE(HealthConstants.WaistCircumference.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    TOTAL_PROTEIN(HealthConstants.TotalProtein.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    BODY_MUSCLE(HealthConstants.BodyMuscle.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    BODY_FAT(HealthConstants.BodyFat.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    FOOD_INTAKE(HealthConstants.FoodIntake.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    FOOD_INFO(HealthConstants.FoodInfo.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    EXERCISE(HealthConstants.Exercise.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.SESSION),
    OXYGEN_SATURATION(HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.SESSION),
    ELECTROCARDIOGRAM(HealthConstants.Electrocardiogram.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.SESSION),
    BODY_TEMPERATURE(HealthConstants.BodyTemperature.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    BLOOD_PRESSURE(HealthConstants.BloodPressure.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    HBA1C(HealthConstants.HbA1c.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE),
    BLOOD_GLUCOSE(HealthConstants.BloodGlucose.HEALTH_DATA_TYPE, SamsungHealthMeasurementType.DISCRETE);

    companion object {
        fun initWith(string: String): SamsungHealthType? {
            return when (string) {
                SLEEP_STAGE.stringValue -> SLEEP_STAGE
                STEP_COUNT.stringValue -> STEP_COUNT
                HEART_RATE.stringValue -> HEART_RATE
                WEIGHT.stringValue -> WEIGHT
                HEIGHT.stringValue -> HEIGHT
                FLOORS_CLIMBED.stringValue -> FLOORS_CLIMBED
                SLEEP.stringValue -> SLEEP
                WAIST_CIRCUMFERENCE.stringValue -> WAIST_CIRCUMFERENCE
                TOTAL_PROTEIN.stringValue -> TOTAL_PROTEIN
                BODY_MUSCLE.stringValue -> BODY_MUSCLE
                BODY_FAT.stringValue -> BODY_FAT
                FOOD_INTAKE.stringValue -> FOOD_INTAKE
                FOOD_INFO.stringValue -> FOOD_INFO
                EXERCISE.stringValue -> EXERCISE
                OXYGEN_SATURATION.stringValue -> OXYGEN_SATURATION
                ELECTROCARDIOGRAM.stringValue -> ELECTROCARDIOGRAM
                BODY_TEMPERATURE.stringValue -> BODY_TEMPERATURE
                BLOOD_PRESSURE.stringValue -> BLOOD_PRESSURE
                HBA1C.stringValue -> HBA1C
                BLOOD_GLUCOSE.stringValue -> BLOOD_GLUCOSE
                else -> null
            }
        }
    }
}