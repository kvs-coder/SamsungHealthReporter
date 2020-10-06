package com.kvs.samsunghealthreporter.observer

import android.util.Log
import com.samsung.android.sdk.healthdata.HealthDataStore

class SamsungHealthObserver(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthObserverListener
) {
    fun observe() {
        Log.i("OBSERVE", "observe")
    }
}