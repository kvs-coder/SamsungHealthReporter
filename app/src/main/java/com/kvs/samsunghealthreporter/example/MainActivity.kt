package com.kvs.samsunghealthreporter.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kvs.samsunghealthreporter.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SAMSUNG_HEALTH"
    }
    private val mConnectionListener = object: SamsungHealthConnectionListener {
        override fun onConnected(
            manager: SamsungHealthManager?
        ) {
            Log.i(TAG, "onConnected")
            manager?.authorize()
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
        override val reader: SamsungHealthReader
            get() = SamsungHealthReader(object: SamsungHealthReaderListener {
                override fun onReadResult() {
                    Log.i(TAG, "onReadResult")
                }
            })
        override val writer: SamsungHealthWriter
            get() = SamsungHealthWriter(object: SamsungHealthWriterListener {
                override fun onWriteResult() {
                    Log.i(TAG, "onWriteResult")
                }
            })

        override fun onPermissionAcquired(types: List<SamsungHealthType>) {
            Log.i(TAG, "onPermissionAcquired $types")
            reader.read()
            writer.write()
        }

        override fun onPermissionDeclined(types: List<SamsungHealthType>) {
            Log.i(TAG, "onPermissionDeclined $types")
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reporter = SamsungHealthReporter(
            this,
            mConnectionListener,
            mPermissionListener,
            listOf(SamsungHealthType.STEP_COUNT),
            listOf(SamsungHealthType.STEP_COUNT)
        )
    }
}