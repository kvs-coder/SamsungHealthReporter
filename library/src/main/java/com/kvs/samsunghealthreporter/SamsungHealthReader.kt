package com.kvs.samsunghealthreporter

import android.util.Log
import com.samsung.android.sdk.healthdata.HealthDataStore

interface SamsungHealthReaderListener {
    fun onReadResult()
    fun onReadException(exception: SamsungHealthReadException)
}
class SamsungHealthReader(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthReaderListener
) {
    fun read() {
        Log.i("READ", "read")
    }
}
