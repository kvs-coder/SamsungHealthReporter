package com.kvs.samsunghealthreporter

import com.samsung.android.sdk.healthdata.HealthPermissionManager

interface HealthType {
    val string: String

    companion object {
        fun initWith(string: String): HealthType {
            return when (string) {
                HealthDiscreteType.WEIGHT.string -> HealthDiscreteType.WEIGHT
                HealthDiscreteType.HEIGHT.string -> HealthDiscreteType.HEIGHT
                HealthDiscreteType.WAIST_CIRCUMFERENCE.string -> HealthDiscreteType.WAIST_CIRCUMFERENCE
                HealthDiscreteType.TOTAL_PROTEIN.string -> HealthDiscreteType.TOTAL_PROTEIN
                HealthDiscreteType.BODY_MUSCLE.string -> HealthDiscreteType.BODY_MUSCLE
                HealthDiscreteType.BODY_FAT.string -> HealthDiscreteType.BODY_FAT
                HealthDiscreteType.FOOD_INTAKE.string -> HealthDiscreteType.FOOD_INTAKE
                HealthDiscreteType.FOOD_INFO.string -> HealthDiscreteType.FOOD_INFO
                HealthDiscreteType.BODY_TEMPERATURE.string -> HealthDiscreteType.BODY_TEMPERATURE
                HealthDiscreteType.BLOOD_PRESSURE.string -> HealthDiscreteType.BLOOD_PRESSURE
                HealthDiscreteType.HBA1C.string -> HealthDiscreteType.HBA1C
                HealthDiscreteType.BLOOD_GLUCOSE.string -> HealthDiscreteType.BLOOD_GLUCOSE
                HealthSessionType.SLEEP_STAGE.string -> HealthSessionType.SLEEP_STAGE
                HealthSessionType.STEP_COUNT.string -> HealthSessionType.STEP_COUNT
                HealthSessionType.HEART_RATE.string -> HealthSessionType.HEART_RATE
                HealthSessionType.FLOORS_CLIMBED.string -> HealthSessionType.FLOORS_CLIMBED
                HealthSessionType.SLEEP.string -> HealthSessionType.SLEEP
                HealthSessionType.EXERCISE.string -> HealthSessionType.EXERCISE
                HealthSessionType.OXYGEN_SATURATION.string -> HealthSessionType.OXYGEN_SATURATION
                HealthSessionType.ELECTROCARDIOGRAM.string -> HealthSessionType.ELECTROCARDIOGRAM
                else -> throw SamsungHealthTypeException(
                    "The string: $string can not be represented as SamsungHealthType"
                )
            }
        }
    }

    fun asOriginal(permissionType: HealthPermissionManager.PermissionType): HealthPermissionManager.PermissionKey {
        return HealthPermissionManager.PermissionKey(string, permissionType)
    }
}
