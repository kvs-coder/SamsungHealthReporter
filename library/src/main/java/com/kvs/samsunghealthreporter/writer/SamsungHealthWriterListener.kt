package com.kvs.samsunghealthreporter.writer

import com.kvs.samsunghealthreporter.SamsungHealthWriteException

interface SamsungHealthWriterListener {
    fun onWriteResult()
    fun onWriteException(exception: SamsungHealthWriteException)
}