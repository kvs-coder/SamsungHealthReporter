package com.kvs.samsunghealthreporter.model

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthData
import com.samsung.android.sdk.healthdata.HealthDataUnit
import kotlin.math.roundToLong

interface Session: Common {
    val endTimestamp: Long
}

interface Discrete: Common {

}

interface Common {
    val startTimestamp: Long
    val offsetTimestamp: Long
    val deviceUuid: String
    val packageName: String
    val sourceDevice: String
    val asJson get() = Gson().toJson(this)
}

class StepCount(data: HealthData) : Session {
    class Count(val value: Double, val unit: String)
    class Calorie(val value: Double, val unit: String)
    class Speed(val value: Double, val unit: String)
    class Distance(val value: Double, val unit: String)

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
    override val startTimestamp = data.getLong(HealthConstants.StepCount.START_TIME)
    override val endTimestamp = data.getLong(HealthConstants.StepCount.END_TIME)
    override val offsetTimestamp = data.getLong(HealthConstants.StepCount.TIME_OFFSET)
    override val deviceUuid = data.getString(HealthConstants.StepCount.DEVICE_UUID)
    override val packageName = data.getString(HealthConstants.StepCount.PACKAGE_NAME)
    override val sourceDevice = data.sourceDevice
    val count = Count(data.getDouble(HealthConstants.StepCount.COUNT), "count")
    val calorie = Calorie(data.getDouble(HealthConstants.StepCount.CALORIE), "kcal")
    val speed = Speed(data.getDouble(HealthConstants.StepCount.SPEED), "km/h")
    val distance =
        Distance(data.getDouble(HealthConstants.StepCount.DISTANCE), HealthDataUnit.METER.unitName)
}