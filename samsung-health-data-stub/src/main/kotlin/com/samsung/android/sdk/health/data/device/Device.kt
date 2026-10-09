package com.samsung.android.sdk.health.data.device

sealed interface DeviceType

enum class DeviceGroup : DeviceType { OTHER, MOBILE, WATCH, RING, BAND, ACCESSORY }

enum class AccessoryType : DeviceType {
    UNKNOWN,
    BLOOD_GLUCOSE_METER,
    BLOOD_PRESSURE_MONITOR,
    HEART_RATE_MONITOR,
    WEIGHT_SCALE,
    BIKE_SPEED_SENSOR,
    BIKE_CADENCE_SENSOR,
    BIKE_POWER_METER,
    TREADMILL,
    ELLIPTICAL,
    STEP_CLIMBER,
    STAIR_CLIMBER,
    INDOOR_BIKE,
    THERMOMETER,
}

class Device(
    val id: String,
    val deviceType: DeviceType,
    val manufacturer: String?,
    val model: String?,
    val name: String?,
)
