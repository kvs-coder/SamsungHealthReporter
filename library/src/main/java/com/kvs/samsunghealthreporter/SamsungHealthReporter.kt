package com.kvs.samsunghealthreporter

import android.app.Activity
import android.content.pm.PackageManager
import com.kvs.samsunghealthreporter.manager.SamsungHealthConnectionListener
import com.kvs.samsunghealthreporter.manager.SamsungHealthManager
import com.kvs.samsunghealthreporter.manager.SamsungHealthPermissionListener
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult
import com.samsung.android.sdk.healthdata.HealthDataStore

class SamsungHealthReporter(
    private val activity: Activity,
    private val connectionListener: SamsungHealthConnectionListener,
    private val permissionListener: SamsungHealthPermissionListener
) {
    companion object {
        private const val SAMSUNG_HEALTH_PACKAGE = "com.sec.android.app.shealth"
    }

    private lateinit var mStore: HealthDataStore
    private val mIsSHealthAvailable: Boolean
        get() {
            try {
                activity.packageManager.getPackageInfo(
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
            val manager = SamsungHealthManager(
                activity,
                mStore,
                permissionListener
            )
            connectionListener.onConnected(manager)
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
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
        }
    }

}