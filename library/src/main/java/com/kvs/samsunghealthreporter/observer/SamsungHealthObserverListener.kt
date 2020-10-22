package com.kvs.samsunghealthreporter.observer

import com.kvs.samsunghealthreporter.SamsungHealthType
import java.lang.Exception

interface SamsungHealthObserverListener {
    fun onSubscribed(type: SamsungHealthType)
    fun onDisposed()
    fun onChange(type: SamsungHealthType)
    fun onException(exception: Exception)
}