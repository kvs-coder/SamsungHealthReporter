package com.kvs.samsunghealthreporter

import android.util.Log

interface SamsungHealthWriterListener {
    fun onWriteResult()
    fun onWriteException(exception: SamsungHealthWriteException)
}
class SamsungHealthWriter(private val listener: SamsungHealthWriterListener) {
    fun write() {
        Log.i("WRITE", "write")
    }
}