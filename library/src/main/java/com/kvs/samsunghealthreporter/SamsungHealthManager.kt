package com.kvs.samsunghealthreporter

import android.app.Activity
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthPermissionManager
import com.samsung.android.sdk.healthdata.HealthResultHolder
import java.util.HashSet

interface SamsungHealthPermissionListener {
    fun onPermissionAcquired(
        reader: SamsungHealthReader?,
        writer: SamsungHealthWriter?,
        observer: SamsungHealthObserver?,
        types: List<SamsungHealthType>
    )

    fun onPermissionDeclined(types: List<SamsungHealthType>)
}

class SamsungHealthManager(
    private val activity: Activity,
    private val healthDataStore: HealthDataStore,
    private val toReadTypes: List<SamsungHealthType>,
    private val toWriteTypes: List<SamsungHealthType>,
    private val permissionListener: SamsungHealthPermissionListener,
    private val readerListener: SamsungHealthReaderListener?,
    private val writerListener: SamsungHealthWriterListener?,
    private val observerListener: SamsungHealthObserverListener?
) {
    private val permissionList: List<SamsungHealthType> get() {
        val permissions = mutableListOf<SamsungHealthType>()
        permissions.addAll(toReadTypes)
        permissions.addAll(toWriteTypes)
        return permissions
    }
    private val permissionHashSet: HashSet<HealthPermissionManager.PermissionKey> get() {
        val keySetToRead = toReadTypes.map {
            HealthPermissionManager.PermissionKey(
                it.stringValue,
                HealthPermissionManager.PermissionType.READ
            )
        }
        val keySetToWrite = toWriteTypes.map {
            HealthPermissionManager.PermissionKey(
                it.stringValue,
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
                SamsungHealthType.initWith(entry.key.dataType)?.let {
                    declinedTypes.add(it)
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
        permissionListener.onPermissionAcquired(
            reader = if (readerListener != null) SamsungHealthReader(
                healthDataStore,
                readerListener
            ) else null,
            writer = if (writerListener != null) SamsungHealthWriter(
                healthDataStore,
                writerListener
            ) else null,
            observer = if (observerListener != null) SamsungHealthObserver(
                healthDataStore,
                observerListener
            ) else null,
            permissionList
        )
    }
}