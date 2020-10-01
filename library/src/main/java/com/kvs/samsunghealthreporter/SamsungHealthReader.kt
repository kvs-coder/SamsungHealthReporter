package com.kvs.samsunghealthreporter

import android.app.Activity
import android.util.Log
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthPermissionManager
import com.samsung.android.sdk.healthdata.HealthResultHolder
import java.util.HashSet

interface SamsungHealthReaderListener {
    fun onReadingPermissionAcquired(types: List<SamsungHealthType>)
    fun onReadingPermissionDeclined(types: List<SamsungHealthType>)
}
class SamsungHealthReader(
    private val activity: Activity,
    private val permissionManager: HealthPermissionManager,
    private val toReadTypes: List<SamsungHealthType>
) {
    var readerListener: SamsungHealthReaderListener? = null

    private val keySetToRead: HashSet<HealthPermissionManager.PermissionKey> = toReadTypes.map {
        HealthPermissionManager.PermissionKey(
            it.stringValue,
            HealthPermissionManager.PermissionType.READ
        )
    }.toHashSet()

    private val mPermissionListener = HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> { result ->
        val resultMap = result.resultMap
        if (resultMap.containsValue(false)) {
            val declinedTypesToRead = mutableListOf<SamsungHealthType>()
            resultMap.forEach { entry ->
                SamsungHealthType.initWith(entry.key.dataType)?.let {
                    declinedTypesToRead.add(it)
                }
            }
            readerListener?.onReadingPermissionDeclined(declinedTypesToRead)
        } else {
            readerListener?.onReadingPermissionAcquired(toReadTypes)
        }
    }

    fun authorize() {
        val isNotAllowedToRead =
            permissionManager.isPermissionAcquired(keySetToRead).containsValue(false)
        if (isNotAllowedToRead) {
            permissionManager.requestPermissions(keySetToRead, activity)
                .setResultListener(mPermissionListener)
        } else {
            readerListener?.onReadingPermissionAcquired(toReadTypes)
        }
    }

}

