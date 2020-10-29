package com.kvs.samsunghealthreporter.reader.resolver

import android.os.Looper
import android.util.Log
import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.*
import com.samsung.android.sdk.healthdata.*
import java.util.*

abstract class SessionResolver<SessionResult>(protected val healthDataStore: HealthDataStore) where SessionResult : Common {
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
        filter: HealthDataResolver.Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<SessionResult>

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    abstract fun aggregate(
        startTime: Date,
        endTime: Date,
        timeGroup: Time.Group,
        filter: HealthDataResolver.Filter?,
        sort: Pair<String, HealthDataResolver.SortOrder>?
    ): List<SessionResult>

    @Throws(
        IllegalArgumentException::class,
        IllegalStateException::class,
        SamsungHealthWriteException::class
    )
    fun insert(value: SessionResult): Boolean {
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
    fun update(value: SessionResult, filter: HealthDataResolver.Filter): Boolean {
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
    fun delete(filter: HealthDataResolver.Filter): Boolean {
        val request = HealthDataResolver.DeleteRequest.Builder()
            .setDataType(type)
            .setFilter(filter)
            .build()
        val resolver = HealthDataResolver(healthDataStore, null)
        val result = resolver.delete(request).await()
        return result.status == HealthResultHolder.BaseResult.STATUS_SUCCESSFUL
    }
}