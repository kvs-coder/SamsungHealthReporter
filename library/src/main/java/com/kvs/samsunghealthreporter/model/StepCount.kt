package com.kvs.samsunghealthreporter.model

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthData

class StepCount(data: HealthData) {
    companion object {
        fun fromJson(string: String): StepCount? {
            return try {
                Gson().fromJson(string, StepCount::class.java)
            } catch (exception: JsonSyntaxException) {
                null
            }
        }
    }
    val type = HealthConstants.StepCount.HEALTH_DATA_TYPE
    val startTimestamp = data.getLong(HealthConstants.StepCount.START_TIME)
    val endTimestamp = data.getLong(HealthConstants.StepCount.END_TIME)
    val offsetTimestamp = data.getLong(HealthConstants.StepCount.TIME_OFFSET)
    val count = data.getDouble(HealthConstants.StepCount.COUNT)
    val calorie = data.getDouble(HealthConstants.StepCount.CALORIE)
    val speed = data.getDouble(HealthConstants.StepCount.SPEED)
    val distance = data.getDouble(HealthConstants.StepCount.DISTANCE)
    val deviceUuid = data.getString(HealthConstants.StepCount.DEVICE_UUID)

    val asJson get() = Gson().toJson(this)
}