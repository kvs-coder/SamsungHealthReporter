package com.kvs.samsunghealthreporter.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kvs.samsunghealthreporter.*
import com.kvs.samsunghealthreporter.manager.SamsungHealthConnectionListener
import com.kvs.samsunghealthreporter.manager.SamsungHealthManager
import com.kvs.samsunghealthreporter.manager.SamsungHealthPermissionListener
import com.kvs.samsunghealthreporter.SamsungHealthType
import com.kvs.samsunghealthreporter.decorator.addMinutes
import com.kvs.samsunghealthreporter.decorator.dayEnd
import com.kvs.samsunghealthreporter.decorator.dayStart
import com.kvs.samsunghealthreporter.model.session.HeartRate
import com.kvs.samsunghealthreporter.model.session.StepCount
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserverListener
import com.kvs.samsunghealthreporter.reader.SamsungHealthReader
import com.kvs.samsunghealthreporter.writer.SamsungHealthWriter
import com.kvs.samsunghealthreporter.writer.SamsungHealthWriterListener
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import java.lang.Exception
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
                    //handleHeartRate(reader)
                    handleStepCount(reader)
                } catch (exception: Exception) {
                    Log.e(TAG, exception.stackTraceToString())
                }
                writer?.write()
                observer?.subscribeOn(SamsungHealthSessionType.STEP_COUNT)
            }.start()
        }

        override fun onPermissionDeclined(types: List<SamsungHealthType>) {
            Log.i(TAG, "onPermissionDeclined $types")
        }

        override fun onException(exception: Exception) {
            Log.e(TAG, "onPermissionDeclined $exception")
        }
    }

    private fun handleStepCount(reader: SamsungHealthReader?) {
        reader?.stepCountResolver?.let { resolver ->
            resolver.read(Date().dayStart, Date().dayEnd, null, null).forEach {
                Log.d(TAG, it.json)
            }
            resolver.aggregate(
                Date().dayStart,
                Date().dayEnd,
                Time.Group.DAILY,
                null,
                null
            ).forEach {
                Log.i(TAG, it.json)
            }
            val stepCount = StepCount(
                StepCount.InsertResult(
                    applicationContext.packageName,
                    Date(),
                    7200000,
                    Date().addMinutes(1),
                    1000,
                    20.0f,
                    2.5f,
                    80.0f
                )
            )
            val insertSuccess = resolver.insert(stepCount)
            Log.w(TAG, "Insert success: $insertSuccess")

            val updateSuccess = resolver.update(
                stepCount,
                HealthDataResolver.Filter.eq(HealthConstants.StepCount.COUNT, 999)
            )
            Log.w(TAG, "Update success: $updateSuccess")

            val deleteSuccess = resolver.delete(
                HealthDataResolver.Filter.eq(
                    HealthConstants.StepCount.COUNT,
                    1000
                )
            )
            Log.w(TAG, "Delete success: $deleteSuccess")
        }
    }

    private fun handleHeartRate(reader: SamsungHealthReader?) {
        reader?.heartRateResolver?.let { resolver ->
            resolver.read(Date().dayStart, Date().dayEnd, null, null).forEach {
                Log.d(TAG, it.json)
            }
            resolver.aggregate(
                Date().dayStart,
                Date().dayEnd,
                Time.Group.DAILY,
                null,
                null
            ).forEach {
                Log.i(TAG, it.json)
            }
            val now = Date()
            val heartRate = HeartRate(
                HeartRate.InsertResult(
                    applicationContext.packageName,
                    now,
                    3600000,
                    now,
                    99.5f,
                    1
                )
            )
            val insertSuccess = resolver.insert(heartRate)
            Log.w(TAG, "Insert success: $insertSuccess")

            val updateSuccess = resolver.update(
                heartRate,
                HealthDataResolver.Filter.eq(HealthConstants.HeartRate.HEART_RATE, 99.5f)
            )
            Log.w(TAG, "Update success: $updateSuccess")

            val deleteSuccess = resolver.delete(
                HealthDataResolver.Filter.eq(
                    HealthConstants.HeartRate.HEART_RATE,
                    100.0f
                )
            )
            Log.w(TAG, "Delete success: $deleteSuccess")
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
        override fun onSubscribed(type: SamsungHealthType) {
            Log.i(TAG, "onChange ${type.string}")
        }

        override fun onDisposed() {
            Log.i(TAG, "onDisposed")
        }

        override fun onChange(type: SamsungHealthType) {
            Log.i(TAG, "onChange ${type.string}")
        }

        override fun onException(exception: Exception) {
            Log.e(TAG, "onChange $exception")
        }
    }
    private lateinit var reporter: SamsungHealthReporter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            reporter = SamsungHealthReporter(
                listOf(SamsungHealthSessionType.STEP_COUNT, SamsungHealthSessionType.HEART_RATE),
                listOf(SamsungHealthSessionType.STEP_COUNT, SamsungHealthSessionType.HEART_RATE),
                this,
                connectionListener = mConnectionListener,
                permissionListener = mPermissionListener,
                writerListener = mWriterListener,
                observerListener = mObserverListener
            )
            reporter.openConnection()
        } catch (exception: SamsungHealthInitializationException) {
            Log.e(TAG, "onCreate $exception")
        }
    }

    override fun onDestroy() {
        reporter.closeConnection()
        super.onDestroy()
    }
}