package com.kvs.samsunghealthreporter

import android.util.Log

interface SamsungHealthReaderListener {
    fun onReadResult()
    fun onReadException(exception: SamsungHealthReadException)
}
class SamsungHealthReader(private val listener: SamsungHealthReaderListener) {
    fun read() {
        Log.i("READ", "read")
    }
}
