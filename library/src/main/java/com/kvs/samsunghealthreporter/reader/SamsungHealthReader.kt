package com.kvs.samsunghealthreporter.reader

import android.util.Log
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class SamsungHealthReader(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthReaderListener
) {
    fun read() {
        Log.i("READ", "read")
    }

    fun readSteps(startTime: Date, endTime: Date) {
        val request = HealthDataResolver.ReadRequest.Builder()
            .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
            .setProperties(
                arrayOf(
                    HealthConstants.StepCount.COUNT,
                    HealthConstants.StepCount.CALORIE,
                    HealthConstants.StepCount.SPEED,
                    HealthConstants.StepCount.DISTANCE,
                    HealthConstants.StepCount.START_TIME,
                    HealthConstants.StepCount.TIME_OFFSET,
                    HealthConstants.StepCount.END_TIME,
                    HealthConstants.StepCount.DEVICE_UUID
                )
            )
            .setLocalTimeRange(
                HealthConstants.StepCount.START_TIME,
                HealthConstants.StepCount.TIME_OFFSET,
                startTime.time,
                endTime.time
            )
            .build()
        val resolver = HealthDataResolver(healthDataStore, null)
        try {
            val result = resolver.read(request).await()
            val iterator = result.iterator()
            while (iterator.hasNext()) {
                val data = iterator.next()
                val startTimestamp = data.getLong(HealthConstants.StepCount.START_TIME)
                val endTimestamp = data.getLong(HealthConstants.StepCount.END_TIME)
                val offsetTimestamp = data.getLong(HealthConstants.StepCount.TIME_OFFSET)
                val count = data.getDouble(HealthConstants.StepCount.COUNT)
                val calorie = data.getDouble(HealthConstants.StepCount.CALORIE)
                val speed = data.getDouble(HealthConstants.StepCount.SPEED)
                val distance = data.getDouble(HealthConstants.StepCount.DISTANCE)
                val deviceUuid = data.getString(HealthConstants.StepCount.DEVICE_UUID)
            }
        } catch (exception: IllegalArgumentException) {

        }
    }
}