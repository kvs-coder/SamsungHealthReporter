package com.kvs.samsunghealthreporter

import com.samsung.android.sdk.healthdata.HealthPermissionManager

interface HealthType {
    val string: String

    companion object {
        fun initWith(string: String): HealthType {
            return when (string) {
                DiscreteType.WEIGHT.string -> DiscreteType.WEIGHT
                DiscreteType.HEIGHT.string -> DiscreteType.HEIGHT
                DiscreteType.WAIST_CIRCUMFERENCE.string -> DiscreteType.WAIST_CIRCUMFERENCE
                DiscreteType.TOTAL_PROTEIN.string -> DiscreteType.TOTAL_PROTEIN
                DiscreteType.BODY_MUSCLE.string -> DiscreteType.BODY_MUSCLE
                DiscreteType.BODY_FAT.string -> DiscreteType.BODY_FAT
                DiscreteType.FOOD_INTAKE.string -> DiscreteType.FOOD_INTAKE
                DiscreteType.FOOD_INFO.string -> DiscreteType.FOOD_INFO
                DiscreteType.BODY_TEMPERATURE.string -> DiscreteType.BODY_TEMPERATURE
                DiscreteType.BLOOD_PRESSURE.string -> DiscreteType.BLOOD_PRESSURE
                DiscreteType.HBA1C.string -> DiscreteType.HBA1C
                DiscreteType.BLOOD_GLUCOSE.string -> DiscreteType.BLOOD_GLUCOSE
                SessionType.SLEEP_STAGE.string -> SessionType.SLEEP_STAGE
                SessionType.STEP_COUNT.string -> SessionType.STEP_COUNT
                SessionType.HEART_RATE.string -> SessionType.HEART_RATE
                SessionType.FLOORS_CLIMBED.string -> SessionType.FLOORS_CLIMBED
                SessionType.SLEEP.string -> SessionType.SLEEP
                SessionType.EXERCISE.string -> SessionType.EXERCISE
                SessionType.OXYGEN_SATURATION.string -> SessionType.OXYGEN_SATURATION
                SessionType.ELECTROCARDIOGRAM.string -> SessionType.ELECTROCARDIOGRAM
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
