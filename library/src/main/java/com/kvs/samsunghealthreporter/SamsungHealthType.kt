package com.kvs.samsunghealthreporter

import com.samsung.android.sdk.healthdata.HealthPermissionManager

interface SamsungHealthType {
    val string: String

    companion object {
        fun initWith(string: String): SamsungHealthType {
            return when (string) {
                SamsungHealthDiscreteType.WEIGHT.string -> SamsungHealthDiscreteType.WEIGHT
                SamsungHealthDiscreteType.HEIGHT.string -> SamsungHealthDiscreteType.HEIGHT
                SamsungHealthDiscreteType.WAIST_CIRCUMFERENCE.string -> SamsungHealthDiscreteType.WAIST_CIRCUMFERENCE
                SamsungHealthDiscreteType.TOTAL_PROTEIN.string -> SamsungHealthDiscreteType.TOTAL_PROTEIN
                SamsungHealthDiscreteType.BODY_MUSCLE.string -> SamsungHealthDiscreteType.BODY_MUSCLE
                SamsungHealthDiscreteType.BODY_FAT.string -> SamsungHealthDiscreteType.BODY_FAT
                SamsungHealthDiscreteType.FOOD_INTAKE.string -> SamsungHealthDiscreteType.FOOD_INTAKE
                SamsungHealthDiscreteType.FOOD_INFO.string -> SamsungHealthDiscreteType.FOOD_INFO
                SamsungHealthDiscreteType.BODY_TEMPERATURE.string -> SamsungHealthDiscreteType.BODY_TEMPERATURE
                SamsungHealthDiscreteType.BLOOD_PRESSURE.string -> SamsungHealthDiscreteType.BLOOD_PRESSURE
                SamsungHealthDiscreteType.HBA1C.string -> SamsungHealthDiscreteType.HBA1C
                SamsungHealthDiscreteType.BLOOD_GLUCOSE.string -> SamsungHealthDiscreteType.BLOOD_GLUCOSE
                SamsungHealthSessionType.SLEEP_STAGE.string -> SamsungHealthSessionType.SLEEP_STAGE
                SamsungHealthSessionType.STEP_COUNT.string -> SamsungHealthSessionType.STEP_COUNT
                SamsungHealthSessionType.HEART_RATE.string -> SamsungHealthSessionType.HEART_RATE
                SamsungHealthSessionType.FLOORS_CLIMBED.string -> SamsungHealthSessionType.FLOORS_CLIMBED
                SamsungHealthSessionType.SLEEP.string -> SamsungHealthSessionType.SLEEP
                SamsungHealthSessionType.EXERCISE.string -> SamsungHealthSessionType.EXERCISE
                SamsungHealthSessionType.OXYGEN_SATURATION.string -> SamsungHealthSessionType.OXYGEN_SATURATION
                SamsungHealthSessionType.ELECTROCARDIOGRAM.string -> SamsungHealthSessionType.ELECTROCARDIOGRAM
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
