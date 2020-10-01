package com.kvs.samsunghealthreporter.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kvs.samsunghealthreporter.*

class MainActivity : AppCompatActivity() {
    private val mConnectionListener = object: SamsungHealthConnectionListener {
        override fun onConnected(reader: SamsungHealthReader?, writer: SamsungHealthWriter?) {
            reader?.authorize()
        }

        override fun onDisconnected() {
            TODO("Not yet implemented")
        }

        override fun onConnectionFailed(
            exception: SamsungHealthConnectionException
        ) {
            TODO("Not yet implemented")
        }

    }

    private val mReaderListener = object: SamsungHealthReaderListener {
        override fun onReadingPermissionAcquired(types: List<SamsungHealthType>) {
            TODO("Not yet implemented")
        }

        override fun onReadingPermissionDeclined(types: List<SamsungHealthType>) {
            TODO("Not yet implemented")
        }
    }

    private val mWriterListener = object: SamsungHealthWriterListener {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reporter = SamsungHealthReporter.Builder(this, mConnectionListener, listOf(), listOf())
            .setReaderListener(mReaderListener)
            .setWriterListener(mWriterListener)
            .build()
    }
}