package com.kvs.samsunghealthreporter

import android.app.Activity
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthPermissionManager
import com.samsung.android.sdk.healthdata.HealthResultHolder
import java.util.HashSet

interface SamsungHealthPermissionListener {
    val reader: SamsungHealthReader
    val writer: SamsungHealthWriter
    fun onPermissionAcquired(types: List<SamsungHealthType>)
    fun onPermissionDeclined(types: List<SamsungHealthType>)
}

class SamsungHealthManager(
    private val activity: Activity,
    store: HealthDataStore,
    private val toReadTypes: List<SamsungHealthType>,
    private val toWriteTypes: List<SamsungHealthType>,
    private val permissionListener: SamsungHealthPermissionListener
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

    private val mPermissionManager = HealthPermissionManager(store)

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
            permissionListener.onPermissionAcquired(permissionList)
        }
    }

    fun authorize() {
        val isNotAllowed =
            mPermissionManager.isPermissionAcquired(permissionHashSet).containsValue(false)
        if (isNotAllowed) {
            mPermissionManager.requestPermissions(permissionHashSet, activity)
                .setResultListener(mPermissionListener)
        } else {
            permissionListener.onPermissionAcquired(permissionList)
        }
    }
}