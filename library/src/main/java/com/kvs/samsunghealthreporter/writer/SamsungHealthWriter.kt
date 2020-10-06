package com.kvs.samsunghealthreporter.writer

import android.util.Log
import com.samsung.android.sdk.healthdata.HealthDataStore

class SamsungHealthWriter(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthWriterListener
) {
    fun write() {
        Log.i("WRITE", "write")
    }
}