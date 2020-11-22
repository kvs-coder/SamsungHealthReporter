package com.kvs.samsunghealthreporter.manager

import com.kvs.samsunghealthreporter.SamsungHealthType
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.resolver.SamsungHealthResolver
import java.lang.Exception

interface SamsungHealthPermissionListener {
    fun onPermissionAcquired(
        types: List<SamsungHealthType>,
        resolver: SamsungHealthResolver?,
        observer: SamsungHealthObserver?,
    )

    fun onPermissionDeclined(types: List<SamsungHealthType>)

    fun onException(exception: Exception)
}