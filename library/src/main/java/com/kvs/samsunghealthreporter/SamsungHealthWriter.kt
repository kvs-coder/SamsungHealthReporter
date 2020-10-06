package com.kvs.samsunghealthreporter

import android.util.Log
import com.samsung.android.sdk.healthdata.HealthDataStore

interface SamsungHealthWriterListener {
    fun onWriteResult()
    fun onWriteException(exception: SamsungHealthWriteException)
}
class SamsungHealthWriter(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthWriterListener
) {
    fun write() {
        Log.i("WRITE", "write")
    }
}