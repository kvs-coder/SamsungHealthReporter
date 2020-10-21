package com.kvs.samsunghealthreporter.reader

import com.kvs.samsunghealthreporter.SamsungHealthReadException
import com.kvs.samsunghealthreporter.model.Common

interface SamsungHealthReaderListener {
    fun onReadResult(result: List<Common>)
    fun onReadException(exception: SamsungHealthReadException)
}