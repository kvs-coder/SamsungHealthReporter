package com.kvs.samsunghealthreporter

interface SamsungHealthWriterListener {
    fun onWriteResult()
}
class SamsungHealthWriter(private val listener: SamsungHealthWriterListener) {
    fun write() {}
}