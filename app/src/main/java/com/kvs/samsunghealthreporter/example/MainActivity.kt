package com.kvs.samsunghealthreporter.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.kvs.samsunghealthreporter.*
import com.kvs.samsunghealthreporter.manager.SamsungHealthConnectionListener
import com.kvs.samsunghealthreporter.manager.SamsungHealthManager
import com.kvs.samsunghealthreporter.manager.SamsungHealthPermissionListener
import com.kvs.samsunghealthreporter.SamsungHealthType
import com.kvs.samsunghealthreporter.decorator.dayEnd
import com.kvs.samsunghealthreporter.decorator.dayStart
import com.kvs.samsunghealthreporter.model.Common
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserverListener
import com.kvs.samsunghealthreporter.reader.SamsungHealthReader
import com.kvs.samsunghealthreporter.reader.SamsungHealthReaderListener
import com.kvs.samsunghealthreporter.writer.SamsungHealthWriter
import com.kvs.samsunghealthreporter.writer.SamsungHealthWriterListener
import java.util.*

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
            Thread {
                try {
                    reader?.stepCountResolver?.let { resolver ->
                        resolver.read(Date().dayStart, Date().dayEnd).forEach {
                            Log.d(TAG, it.json)
                        }
                        resolver.aggregate(Date().dayStart, Date().dayEnd)?.let {
                            Log.i(TAG, it.json)
                        }
                    }
                } catch (exception: SamsungHealthReadException) {
                    Log.e(TAG, exception.stackTraceToString())
                }
                writer?.write()
                observer?.observe()
            }.start()
        }

        override fun onPermissionDeclined(types: List<SamsungHealthType>) {
            Log.i(TAG, "onPermissionDeclined $types")
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