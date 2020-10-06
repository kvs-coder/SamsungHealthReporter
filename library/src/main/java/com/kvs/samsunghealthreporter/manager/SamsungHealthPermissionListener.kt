package com.kvs.samsunghealthreporter.manager

import com.kvs.samsunghealthreporter.SamsungHealthType
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.reader.SamsungHealthReader
import com.kvs.samsunghealthreporter.writer.SamsungHealthWriter

interface SamsungHealthPermissionListener {
    fun onPermissionAcquired(
        reader: SamsungHealthReader?,
        writer: SamsungHealthWriter?,
        observer: SamsungHealthObserver?,
        types: List<SamsungHealthType>
    )

    fun onPermissionDeclined(types: List<SamsungHealthType>)
}