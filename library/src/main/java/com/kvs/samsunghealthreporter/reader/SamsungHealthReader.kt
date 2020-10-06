package com.kvs.samsunghealthreporter.reader

import android.util.Log
import com.samsung.android.sdk.healthdata.HealthDataStore

class SamsungHealthReader(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthReaderListener
) {
    fun read() {
        Log.i("READ", "read")
    }
}