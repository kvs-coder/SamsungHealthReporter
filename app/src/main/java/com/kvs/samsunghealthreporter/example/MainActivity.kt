package com.kvs.samsunghealthreporter.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kvs.samsunghealthreporter.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SAMSUNG_HEALTH"
    }

    private val mConnectionListener = object : SamsungHealthConnectionListener {
        override fun onConnected(manager: SamsungHealthManager) {
            Log.i(TAG, "onConnected")
            manager.authorize()
        }

        override fun onDisconnected() {
            Log.i(TAG, "onDisconnected")
        }

        override fun onConnectionFailed(
            exception: SamsungHealthConnectionException
        ) {
            Log.i(TAG, "onConnectionFailed $exception")
        }
    }

    private val mPermissionListener = object : SamsungHealthPermissionListener {
        override fun onPermissionAcquired(
            reader: SamsungHealthReader?,
            writer: SamsungHealthWriter?,
            observer: SamsungHealthObserver?,
            types: List<SamsungHealthType>
        ) {
            Log.i(TAG, "onPermissionAcquired $types")
            reader?.read()
            writer?.write()
            observer?.observe()
        }

        override fun onPermissionDeclined(types: List<SamsungHealthType>) {
            Log.i(TAG, "onPermissionDeclined $types")
        }
    }

    private val mReaderListener = object : SamsungHealthReaderListener {
        override fun onReadResult() {
            Log.i(TAG, "onReadResult")
        }

        override fun onReadException(exception: SamsungHealthReadException) {
            Log.e(TAG, "onReadException $exception")
        }
    }
    private val mWriterListener = object : SamsungHealthWriterListener {
        override fun onWriteResult() {
            Log.i(TAG, "onWriteResult")
        }

        override fun onWriteException(exception: SamsungHealthWriteException) {
            Log.e(TAG, "onWriteException $exception")
        }
    }
    private val mObserverListener = object : SamsungHealthObserverListener {
        override fun onChange(dataTypeName: String) {
            Log.i(TAG, "onChange $dataTypeName")
        }
    }
    private lateinit var reporter: SamsungHealthReporter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reporter = SamsungHealthReporter(
            listOf(SamsungHealthType.STEP_COUNT),
            listOf(SamsungHealthType.STEP_COUNT),
            this,
            connectionListener = mConnectionListener,
            permissionListener = mPermissionListener,
            readerListener = mReaderListener,
            writerListener = mWriterListener,
            observerListener = mObserverListener
        )
        reporter.openConnection()
    }

    override fun onDestroy() {
        reporter.closeConnection()
        super.onDestroy()
    }
}