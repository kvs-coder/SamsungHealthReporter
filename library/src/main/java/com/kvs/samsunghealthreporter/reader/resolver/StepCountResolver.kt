package com.kvs.samsunghealthreporter.reader.resolver

import com.kvs.samsunghealthreporter.model.StepCount
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class StepCountResolver(private val healthDataStore: HealthDataStore) {
    fun readSteps(
        startTime: Date,
        endTime: Date,
        onSuccess: (List<StepCount>) -> Unit,
        onError: (IllegalArgumentException) -> Unit
    ) {
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
            val list = mutableListOf<StepCount>()
            while (iterator.hasNext()) {
                val data = iterator.next()
                val stepCount = StepCount(data)
                list.add(stepCount)
            }
            onSuccess(list)
        } catch (exception: IllegalArgumentException) {
            onError(exception)
        }
    }
}