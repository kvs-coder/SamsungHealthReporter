package com.kvs.samsunghealthreporter.manager

import com.kvs.samsunghealthreporter.HealthType
import com.kvs.samsunghealthreporter.observer.Observer
import com.kvs.samsunghealthreporter.resolver.Resolver
import java.lang.Exception

interface PermissionListener {
    fun onPermissionAcquired(
        types: Set<HealthType>,
        resolver: Resolver,
        observer: Observer,
    )

    fun onException(exception: Exception)
}