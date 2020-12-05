package com.kvs.samsunghealthreporter.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kvs.samsunghealthreporter.*
import com.kvs.samsunghealthreporter.SamsungHealthConnectionListener
import com.kvs.samsunghealthreporter.manager.SamsungHealthManager
import com.kvs.samsunghealthreporter.manager.SamsungHealthPermissionListener
import com.kvs.samsunghealthreporter.HealthType
import com.kvs.samsunghealthreporter.decorator.addMinutes
import com.kvs.samsunghealthreporter.decorator.dayEnd
import com.kvs.samsunghealthreporter.decorator.dayStart
import com.kvs.samsunghealthreporter.model.Filter
import com.kvs.samsunghealthreporter.model.session.HeartRate
import com.kvs.samsunghealthreporter.model.session.StepCount
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.observer.SamsungHealthObserver
import com.kvs.samsunghealthreporter.resolver.SamsungHealthResolver
import com.samsung.android.sdk.healthdata.HealthConstants

import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SAMSUNG_HEALTH"
    }

    private val mConnectionListener = object : SamsungHealthConnectionListener {
        override fun onConnected() {
            Log.i(TAG, "onConnected")
            reporter.manager.authorize(
                this@MainActivity,
                toReadTypes = setOf(
                    SessionType.STEP_COUNT,
                    SessionType.HEART_RATE
                ),
                toWriteTypes = setOf(
                    SessionType.STEP_COUNT,
                    SessionType.HEART_RATE
                )
            )
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
        override fun onAcquired(success: Boolean) {
            Log.i(TAG, "onPermissionAcquired: $success")
            if (success) {
                Thread {
                    try {
                        handleHeartRate(reporter.resolver)
                        handleStepCount(reporter.resolver)
                    } catch (exception: Exception) {
                        Log.e(TAG, exception.stackTraceToString())
                    }
                    reporter.observer.apply {
                        observe(SessionType.STEP_COUNT)
                            .subscribe(
                                onNext = {
                                    Log.d(TAG, "onNext: ${it.string}")
                                },
                                onError = {
                                    Log.e(TAG, "onError: $it")
                                }
                            )
                    }
                }.start()
            }
        }

        override fun onException(exception: Exception) {
            Log.e(TAG, "onPermissionDeclined $exception")
        }
    }

    private fun handleStepCount(resolver: SamsungHealthResolver) {
        resolver.stepCountResolver.let { r ->
            r.read(Date().dayStart, Date().dayEnd, null, null).forEach {
                Log.d(TAG, it.json)
            }
            r.aggregate(
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
            val insertSuccess = r.insert(stepCount)
            Log.w(TAG, "Insert success: $insertSuccess")

            val updateSuccess = r.update(
                stepCount,
                Filter.eq(HealthConstants.StepCount.COUNT, 999)
            )
            Log.w(TAG, "Update success: $updateSuccess")

            val deleteSuccess = r.delete(
                Filter.eq(
                    HealthConstants.StepCount.COUNT,
                    1000
                )
            )
            Log.w(TAG, "Delete success: $deleteSuccess")
        }
    }

    private fun handleHeartRate(resolver: SamsungHealthResolver) {
        resolver.heartRateResolver.let { r ->
            r.read(Date().dayStart.dayStart, Date().dayEnd, null, null).forEach {
                Log.d(TAG, it.json)
            }
            r.aggregate(
                Date().dayStart.dayStart,
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
            val insertSuccess = r.insert(heartRate)
            Log.w(TAG, "Insert success: $insertSuccess")

            val updateSuccess = r.update(
                heartRate,
                Filter.eq(HealthConstants.HeartRate.HEART_RATE, 99.5f)
            )
            Log.w(TAG, "Update success: $updateSuccess")

            val deleteSuccess = r.delete(
                Filter.eq(
                    HealthConstants.HeartRate.HEART_RATE,
                    100.0f
                )
            )
            Log.w(TAG, "Delete success: $deleteSuccess")
        }
    }

    private lateinit var reporter: SamsungHealthReporter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            reporter = SamsungHealthReporter(this)
            reporter.connectionListener = mConnectionListener
            reporter.manager.permissionListener = mPermissionListener
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