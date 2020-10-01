package com.kvs.samsunghealthreporter

import android.app.Activity
import android.content.pm.PackageManager
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthPermissionManager

class SamsungHealthReporter(
    private val activity: Activity,
    private val connectionListener: SamsungHealthConnectionListener,
    toReadTypes: List<SamsungHealthType>,
    toWriteTypes: List<SamsungHealthType>
) {
    class Builder(
        val activity: Activity,
        val connectionListener: SamsungHealthConnectionListener,
        val toReadTypes: List<SamsungHealthType>,
        val toWriteTypes: List<SamsungHealthType>
    ) {
        private var mReaderListener: SamsungHealthReaderListener? = null
        private var mWriterListener: SamsungHealthWriterListener? = null

        fun setReaderListener(listener: SamsungHealthReaderListener) =
            apply {
                mReaderListener = listener
            }

        fun setWriterListener(listener: SamsungHealthWriterListener) =
            apply {
                mWriterListener = listener
            }

        fun build() = SamsungHealthReporter(this)
    }

    companion object {
        private const val SAMSUNG_HEALTH_PACKAGE = "com.sec.android.app.shealth"
    }

    private constructor(builder: Builder) : this(
        builder.activity,
        builder.connectionListener,
        builder.toReadTypes,
        builder.toWriteTypes
    )

    private var mReader: SamsungHealthReader? = null
    private var mWriter: SamsungHealthWriter? = null
    private val mStore: HealthDataStore
    private val mPermissionManager: HealthPermissionManager
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
            connectionListener.onConnected(mReader, mWriter)
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
            mReader = SamsungHealthReader(activity, mPermissionManager, toReadTypes)
            mWriter = SamsungHealthWriter()
            mStore.connectService()
        } else {
            throw SamsungHealthInitializationException()
        }
    }
}