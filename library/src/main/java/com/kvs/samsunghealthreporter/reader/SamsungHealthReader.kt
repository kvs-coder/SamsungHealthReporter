package com.kvs.samsunghealthreporter.reader

import com.kvs.samsunghealthreporter.SamsungHealthReadException
import com.kvs.samsunghealthreporter.decorator.dayEnd
import com.kvs.samsunghealthreporter.decorator.dayStart
import com.kvs.samsunghealthreporter.reader.resolver.StepCountResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.lang.IllegalStateException
import java.util.*

class SamsungHealthReader(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthReaderListener
) {
    fun read() {
        val stepsResolver = StepCountResolver(healthDataStore)
        try {
            val stepCountList = stepsResolver.readSteps(Date().dayStart, Date().dayEnd)
            listener.onReadResult(stepCountList)
        } catch (exception: IllegalStateException) {
            listener.onReadException(SamsungHealthReadException(exception.stackTraceToString()))
        }


    }


}