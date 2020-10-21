package com.kvs.samsunghealthreporter.reader.resolver

import android.os.Looper
import android.util.Log
import com.kvs.samsunghealthreporter.model.StepCount
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.util.*

class StepCountResolver(private val healthDataStore: HealthDataStore) {
    companion object {
        const val TAG = "StepCountResolver"
    }
    init {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }

    @Throws(IllegalArgumentException::class)
    fun readSteps(
        startTime: Date,
        endTime: Date,
    ): List<StepCount> {
        val list = mutableListOf<StepCount>()
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
        val result = resolver.read(request).await()
        val iterator = result.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            val stepCount = StepCount(data)
            list.add(stepCount)
        }
        return list
    }
}