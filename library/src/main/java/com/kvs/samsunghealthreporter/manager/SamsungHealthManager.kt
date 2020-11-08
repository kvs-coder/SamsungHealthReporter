package com.kvs.samsunghealthreporter.manager

import android.app.Activity
import com.kvs.samsunghealthreporter.SamsungHealthType
import com.kvs.samsunghealthreporter.SamsungHealthTypeException
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.reader.SamsungHealthReader
import com.kvs.samsunghealthreporter.writer.SamsungHealthWriter
import com.kvs.samsunghealthreporter.writer.SamsungHealthWriterListener
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthPermissionManager
import com.samsung.android.sdk.healthdata.HealthResultHolder
import java.util.HashSet

class SamsungHealthManager(
    private val activity: Activity,
    private val healthDataStore: HealthDataStore,
    private val toReadTypes: List<SamsungHealthType>,
    private val toWriteTypes: List<SamsungHealthType>,
    private val permissionListener: SamsungHealthPermissionListener,
    private val writerListener: SamsungHealthWriterListener?
) {
    private val permissionList: List<SamsungHealthType> get() {
        val permissions = mutableListOf<SamsungHealthType>()
        toReadTypes.forEach {
            if (!permissions.contains(it)) {
                permissions.add(it)
            }
        }
        toWriteTypes.forEach {
            if (!permissions.contains(it)) {
                permissions.add(it)
            }
        }
        return permissions
    }
    private val permissionHashSet: HashSet<HealthPermissionManager.PermissionKey>
        get() {
        val keySetToRead = toReadTypes.map {
            HealthPermissionManager.PermissionKey(
                it.string,
                HealthPermissionManager.PermissionType.READ
            )
        }
        val keySetToWrite = toWriteTypes.map {
            HealthPermissionManager.PermissionKey(
                it.string,
                HealthPermissionManager.PermissionType.WRITE
            )
        }
        val permissions = mutableListOf<HealthPermissionManager.PermissionKey>()
        permissions.addAll(keySetToRead)
        permissions.addAll(keySetToWrite)
        return permissions.toHashSet()
    }

    private val mPermissionManager = HealthPermissionManager(healthDataStore)

    private val mPermissionListener = HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> { result ->
        val resultMap = result.resultMap
        if (resultMap.containsValue(false)) {
            val declinedTypes = mutableListOf<SamsungHealthType>()
            resultMap.forEach { entry ->
                try {
                    val type = SamsungHealthType.initWith(entry.key.dataType)
                    declinedTypes.add(type)
                }
                catch (exception: SamsungHealthTypeException) {
                    permissionListener.onException(exception)
                }
            }
            permissionListener.onPermissionDeclined(declinedTypes)
        } else {
            setPermissionListener()
        }
    }

    fun authorize() {
        val isNotAllowed =
            mPermissionManager.isPermissionAcquired(permissionHashSet).containsValue(false)
        if (isNotAllowed) {
            mPermissionManager.requestPermissions(permissionHashSet, activity)
                .setResultListener(mPermissionListener)
        } else {
            setPermissionListener()
        }
    }

    private fun setPermissionListener() {
        val writer = if (writerListener != null) SamsungHealthWriter(
            healthDataStore,
            writerListener
        ) else null
        permissionListener.onPermissionAcquired(
            SamsungHealthReader(
                healthDataStore,
            ),
            writer,
            SamsungHealthObserver(healthDataStore),
            permissionList
        )
    }
}