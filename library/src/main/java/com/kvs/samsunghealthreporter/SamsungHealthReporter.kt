package com.kvs.samsunghealthreporter

import android.content.Context
import android.content.pm.PackageManager
import com.kvs.samsunghealthreporter.manager.SamsungHealthManager
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.resolver.SamsungHealthResolver
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult
import com.samsung.android.sdk.healthdata.HealthDataStore
import java.lang.IllegalStateException
import kotlin.jvm.Throws

class SamsungHealthReporter(
    private val context: Context
) {
    companion object {
        private const val SAMSUNG_HEALTH_PACKAGE = "com.sec.android.app.shealth"
    }

    var connectionListener: SamsungHealthConnectionListener? = null
    val manager: SamsungHealthManager
    val resolver: SamsungHealthResolver
    val observer: SamsungHealthObserver

    private val mStore: HealthDataStore
    private val mIsSHealthAvailable: Boolean
        get() {
            try {
                context.packageManager.getPackageInfo(
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
            connectionListener?.onConnected()
        }

        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            connectionListener?.onConnectionFailed(
                SamsungHealthConnectionException(error.errorCode.toString())
            )
        }

        override fun onDisconnected() {
            connectionListener?.onDisconnected()
        }
    }

    init {
        if (mIsSHealthAvailable) {
            mStore = HealthDataStore(context, mConnectionListener)
            manager = SamsungHealthManager(mStore)
            resolver = SamsungHealthResolver(mStore)
            observer = SamsungHealthObserver(mStore)
        } else {
            throw SamsungHealthInitializationException()
        }
    }

    @Throws(IllegalStateException::class)
    fun openConnection() {
        mStore.connectService()
    }

    @Throws(IllegalStateException::class)
    fun closeConnection() {
        mStore.disconnectService()
    }

}