package com.kvs.samsunghealthreporter

import android.util.Log
import com.samsung.android.sdk.healthdata.HealthDataStore

interface SamsungHealthObserverListener {
    fun onChange(dataTypeName: String)
}

class SamsungHealthObserver(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthObserverListener
) {
    fun observe() {
        Log.i("OBSERVE", "observe")
    }
}