package com.kvs.samsunghealthreporter.reader

import com.kvs.samsunghealthreporter.SamsungHealthReadException

interface SamsungHealthReaderListener {
    fun onReadResult()
    fun onReadException(exception: SamsungHealthReadException)
}