package com.kvs.samsunghealthreporter

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthPermissionManager

class SamsungHealthReporter(
    private val activity: Activity,
    private val connectionListener: SamsungHealthConnectionListener,
    private val permissionListener: SamsungHealthPermissionListener,
    toReadTypes: List<SamsungHealthType>,
    toWriteTypes: List<SamsungHealthType>
) {
    companion object {
        private const val SAMSUNG_HEALTH_PACKAGE = "com.sec.android.app.shealth"
    }

    private var mManager: SamsungHealthManager? = null
    private val mStore: HealthDataStore
    private val mPermissionManager: HealthPermissionManager
    private val mIsSHealthAvailable: Boolean
        get() {
            try {
                val packageInfo = activity.packageManager.getPackageInfo(
                    SAMSUNG_HEALTH_PACKAGE,
                    PackageManager.GET_ACTIVITIES
                )
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    private val mConnectionListener = object : HealthDataStore.ConnectionListener {
        override fun onConnected() {
            connectionListener.onConnected(mManager)
        }

        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            connectionListener.onConnectionFailed(
                SamsungHealthConnectionException(error.errorCode.toString())
            )
        }

        override fun onDisconnected() {
            connectionListener.onDisconnected()
        }
    }

    init {
        if (mIsSHealthAvailable) {
            mStore = HealthDataStore(activity, mConnectionListener)
            mPermissionManager = HealthPermissionManager(mStore)
            mManager = SamsungHealthManager(
                activity,
                mStore,
                toReadTypes,
                toWriteTypes,
                permissionListener
            )
        } else {
            throw SamsungHealthInitializationException()
        }
    }

    fun openConnection() {
        try {
            mStore.connectService()
        } catch (exception: IllegalStateException) {
            exception.printStackTrace()
        }
    }

    fun closeConnection() {
        try {
            mStore.disconnectService()
        } catch (exception: NullPointerException) {
            exception.printStackTrace()
        } catch (exception: IllegalArgumentException) {
            exception.printStackTrace()
        }
    }

}