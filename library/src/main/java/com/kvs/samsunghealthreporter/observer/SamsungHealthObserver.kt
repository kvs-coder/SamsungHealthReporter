package com.kvs.samsunghealthreporter.observer

import com.kvs.samsunghealthreporter.*
import com.samsung.android.sdk.healthdata.*

class SamsungHealthObserver(
    private val healthDataStore: HealthDataStore,
    private val listener: SamsungHealthObserverListener
) {
    private val mDataObserver: HealthDataObserver = object : HealthDataObserver(null) {
        override fun onChange(dataTypeName: String) {
            try {
                val type = SamsungHealthType.initWith(dataTypeName)
                listener.onChange(type)
            } catch (exception: SamsungHealthTypeException) {
                listener.onException(exception)
            }
        }
    }

    fun subscribeOn(type: SamsungHealthType) {
        try {
            HealthDataObserver.addObserver(healthDataStore, type.string, mDataObserver)
            listener.onSubscribed(type)
        } catch (exception: RuntimeException) {
            listener.onException(exception)
        }
    }

    fun dispose() {
        try {
            HealthDataObserver.removeObserver(healthDataStore, mDataObserver)
            listener.onDisposed()
        } catch (exception: IllegalStateException) {
            listener.onException(exception)
        }
    }
}