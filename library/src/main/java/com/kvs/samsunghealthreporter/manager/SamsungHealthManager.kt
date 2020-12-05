package com.kvs.samsunghealthreporter.manager

import android.app.Activity
import com.kvs.samsunghealthreporter.HealthType
import com.kvs.samsunghealthreporter.SamsungHealthTypeException
import com.kvs.samsunghealthreporter.decorator.parsed
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.resolver.SamsungHealthResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthPermissionManager
import com.samsung.android.sdk.healthdata.HealthResultHolder

class SamsungHealthManager(
    private val healthDataStore: HealthDataStore,
    private val permissionListener: SamsungHealthPermissionListener
) {
    private val mPermissionManager = HealthPermissionManager(healthDataStore)
    private val mPermissionListener = HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> { result ->
        val resultMap = result.resultMap
        if (!resultMap.containsValue(false)) {
            val permissions = mutableSetOf<HealthType>()
            resultMap.forEach { entry ->
                try {
                    val type = entry.key.parsed
                    permissions.add(type)
                }
                catch (exception: SamsungHealthTypeException) {
                    permissionListener.onException(exception)
                }
            }
            permissionListener.onPermissionAcquired(
                permissions,
                SamsungHealthResolver(healthDataStore),
                SamsungHealthObserver(healthDataStore),
            )
        } else {
            permissionListener.onException(
                SamsungHealthTypeException(
                    "User declined authorization, ${
                        resultMap.map {
                            "${it.key.dataType} - ${it.key.permissionType} - ${it.value}"
                        }
                    }"
                )
            )
        }
    }

    fun authorize(
        activity: Activity,
        toReadTypes: Set<HealthType>,
        toWriteTypes: Set<HealthType>
    ) {
        val keySetToRead = toReadTypes.map {
            it.asOriginal(HealthPermissionManager.PermissionType.READ)
        }
        val keySetToWrite = toWriteTypes.map {
            it.asOriginal(HealthPermissionManager.PermissionType.WRITE)
        }
        val permissions = mutableSetOf<HealthPermissionManager.PermissionKey>()
        permissions.addAll(keySetToRead)
        permissions.addAll(keySetToWrite)
        val permissionHashSet = permissions.toHashSet()
        val isNotAllowed =
            mPermissionManager.isPermissionAcquired(permissionHashSet).containsValue(false)
        if (isNotAllowed) {
            mPermissionManager.requestPermissions(permissionHashSet, activity)
                .setResultListener(mPermissionListener)
        } else {
            val permissionList = mutableSetOf<HealthType>()
            toReadTypes.forEach {
                permissionList.add(it)
            }
            toWriteTypes.forEach {
                permissionList.add(it)
            }
            permissionListener.onPermissionAcquired(
                permissionList,
                SamsungHealthResolver(healthDataStore),
                SamsungHealthObserver(healthDataStore),
            )
        }
    }
}