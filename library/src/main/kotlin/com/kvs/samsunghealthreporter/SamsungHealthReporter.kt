package com.kvs.samsunghealthreporter

import android.content.Context
import android.content.pm.PackageManager
import com.kvs.samsunghealthreporter.decorator.wrapped
import com.kvs.samsunghealthreporter.service.SamsungHealthManager
import com.kvs.samsunghealthreporter.service.SamsungHealthObserver
import com.kvs.samsunghealthreporter.service.SamsungHealthReader
import com.kvs.samsunghealthreporter.service.SamsungHealthWriter
import com.samsung.android.sdk.health.data.HealthDataService
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.error.HealthDataException

/**
 * **SamsungHealthReporter** entry point to Samsung Health.
 *
 * Create one per app (or per screen) and keep it; every service shares one connection to Samsung Health.
 * All calls are `suspend` and run on the caller's coroutine context.
 */
public class SamsungHealthReporter internal constructor(
    store: HealthDataStore,
) {
    /**
     * Connects to Samsung Health.
     *
     * @param context **Context** any context; the application context is used
     * @throws SamsungHealthException.Resolvable when Samsung Health must be installed, updated or set up first
     */
    public constructor(context: Context) : this(connect(context.applicationContext))

    /** **SamsungHealthReader** reads and aggregates data */
    public val reader: SamsungHealthReader = SamsungHealthReader(store)

    /** **SamsungHealthWriter** inserts, updates and deletes data */
    public val writer: SamsungHealthWriter = SamsungHealthWriter(store)

    /** **SamsungHealthObserver** reads changes */
    public val observer: SamsungHealthObserver = SamsungHealthObserver(store)

    /** **SamsungHealthManager** permissions, error resolution and devices */
    public val manager: SamsungHealthManager = SamsungHealthManager(store)

    /** Availability checks that need no connection */
    public companion object {
        private const val SAMSUNG_HEALTH_PACKAGE = "com.sec.android.app.shealth"

        /**
         * Checks that the Samsung Health app is installed. It is needed on every phone, Samsung or not.
         *
         * @param context **Context** any context
         * @return **Boolean** true when Samsung Health is installed
         */
        public fun isAvailable(context: Context): Boolean =
            try {
                context.packageManager.getPackageInfo(SAMSUNG_HEALTH_PACKAGE, 0)
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }

        private fun connect(context: Context): HealthDataStore =
            try {
                HealthDataService.getStore(context)
            } catch (exception: HealthDataException) {
                throw exception.wrapped
            }
    }
}
