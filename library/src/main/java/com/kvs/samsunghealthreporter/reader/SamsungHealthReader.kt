package com.kvs.samsunghealthreporter.reader

import android.os.Looper
import android.util.Log
import com.kvs.samsunghealthreporter.decorator.dayEnd
import com.kvs.samsunghealthreporter.decorator.dayStart
import com.kvs.samsunghealthreporter.reader.resolver.StepCountResolver
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SamsungHealthReader(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthReaderListener
) {
    fun read() {
        GlobalScope.launch {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            val stepsResolver = StepCountResolver(healthDataStore)
            stepsResolver.readSteps(Date().dayStart, Date().dayEnd, onSuccess = { list ->
                list.forEach {
                    Log.d("FFF", it.asJson)
                }
            }, onError = {

            })
        }

    }


}