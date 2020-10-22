package com.kvs.samsunghealthreporter.manager

import com.kvs.samsunghealthreporter.SamsungHealthType
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.reader.SamsungHealthReader
import com.kvs.samsunghealthreporter.writer.SamsungHealthWriter
import java.lang.Exception

interface SamsungHealthPermissionListener {
    fun onPermissionAcquired(
        reader: SamsungHealthReader?,
        writer: SamsungHealthWriter?,
        observer: SamsungHealthObserver?,
        types: List<SamsungHealthType>
    )

    fun onPermissionDeclined(types: List<SamsungHealthType>)

    fun onException(exception: Exception)
}