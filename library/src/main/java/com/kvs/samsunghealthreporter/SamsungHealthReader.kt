package com.kvs.samsunghealthreporter

interface SamsungHealthReaderListener {
    fun onReadResult()
}
class SamsungHealthReader(private val listener: SamsungHealthReaderListener) {
    fun read() {}
}
