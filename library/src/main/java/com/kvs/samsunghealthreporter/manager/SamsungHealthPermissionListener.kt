package com.kvs.samsunghealthreporter.manager

import com.kvs.samsunghealthreporter.HealthType
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.resolver.SamsungHealthResolver
import java.lang.Exception

interface SamsungHealthPermissionListener {
    fun onAcquired(
        types: Set<HealthType>,
        resolver: SamsungHealthResolver,
        observer: SamsungHealthObserver,
    )

    fun onException(exception: Exception)
}