package com.kvs.samsunghealthreporter.resolver

import android.os.Looper
import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.decorator.Filter
import com.kvs.samsunghealthreporter.decorator.SortOrder
import com.kvs.samsunghealthreporter.model.*
import com.samsung.android.sdk.healthdata.*
import java.util.*

abstract class CommonResolver<Value : Common>(protected val healthDataStore: HealthDataStore) {
    abstract val type: String

    init {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    abstract fun read(
        startTime: Date,
        endTime: Date,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<Value>

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    abstract fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: Filter?,
        sort: Pair<String, SortOrder>?
    ): List<Value>

    @Throws(
        IllegalArgumentException::class,
        IllegalStateException::class,
        SamsungHealthWriteException::class
    )
    fun insert(value: Value): Boolean {
        val data = value.asOriginal(healthDataStore)
        val request = HealthDataResolver.InsertRequest.Builder()
            .setDataType(type)
            .build()
        request.addHealthData(data)
        val resolver = HealthDataResolver(healthDataStore, null)
        val result = resolver.insert(request).await()
        return result.status == HealthResultHolder.BaseResult.STATUS_SUCCESSFUL
    }

    @Throws(
        IllegalArgumentException::class,
        IllegalStateException::class,
        SamsungHealthWriteException::class
    )
    fun update(value: Value, filter: Filter): Boolean {
        val data = value.asOriginal(healthDataStore)
        val request = HealthDataResolver.UpdateRequest.Builder()
            .setDataType(type)
            .setFilter(filter)
            .setHealthData(data)
            .build()
        val resolver = HealthDataResolver(healthDataStore, null)
        val result = resolver.update(request).await()
        return result.status == HealthResultHolder.BaseResult.STATUS_SUCCESSFUL
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun delete(filter: Filter): Boolean {
        val request = HealthDataResolver.DeleteRequest.Builder()
            .setDataType(type)
            .setFilter(filter)
            .build()
        val resolver = HealthDataResolver(healthDataStore, null)
        val result = resolver.delete(request).await()
        return result.status == HealthResultHolder.BaseResult.STATUS_SUCCESSFUL
    }
}