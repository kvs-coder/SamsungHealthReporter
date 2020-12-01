package com.kvs.samsunghealthreporter.manager

import com.kvs.samsunghealthreporter.SamsungHealthType
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.resolver.SamsungHealthResolver
import java.lang.Exception

interface SamsungHealthPermissionListener {
    fun onPermissionAcquired(
        types: Set<SamsungHealthType>,
        resolver: SamsungHealthResolver?,
        observer: SamsungHealthObserver?,
    )

    fun onPermissionDeclined(types: Set<SamsungHealthType>)

    fun onException(exception: Exception)
}