package com.kvs.samsunghealthreporter

import com.samsung.android.sdk.healthdata.HealthConstants

enum class HealthDiscreteType(override val string: String): HealthType {
    WEIGHT(HealthConstants.Weight.HEALTH_DATA_TYPE),
    HEIGHT(HealthConstants.Height.HEALTH_DATA_TYPE),
    WAIST_CIRCUMFERENCE(HealthConstants.WaistCircumference.HEALTH_DATA_TYPE),
    TOTAL_PROTEIN(HealthConstants.TotalProtein.HEALTH_DATA_TYPE),
    BODY_MUSCLE(HealthConstants.BodyMuscle.HEALTH_DATA_TYPE),
    BODY_FAT(HealthConstants.BodyFat.HEALTH_DATA_TYPE),
    FOOD_INTAKE(HealthConstants.FoodIntake.HEALTH_DATA_TYPE),
    FOOD_INFO(HealthConstants.FoodInfo.HEALTH_DATA_TYPE),
    BODY_TEMPERATURE(HealthConstants.BodyTemperature.HEALTH_DATA_TYPE),
    BLOOD_PRESSURE(HealthConstants.BloodPressure.HEALTH_DATA_TYPE),
    HBA1C(HealthConstants.HbA1c.HEALTH_DATA_TYPE),
    BLOOD_GLUCOSE(HealthConstants.BloodGlucose.HEALTH_DATA_TYPE);
}