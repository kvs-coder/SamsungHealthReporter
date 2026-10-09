package com.kvs.samsunghealthreporter.service

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.SamsungHealthReporter
import com.kvs.samsunghealthreporter.model.Device
import com.kvs.samsunghealthreporter.model.Permission
import com.kvs.samsunghealthreporter.model.type.AccessType
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.DeviceManager
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.device.DeviceGroup
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException
import com.samsung.android.sdk.health.data.request.DataTypes
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import com.samsung.android.sdk.health.data.device.Device as OriginalDevice
import com.samsung.android.sdk.health.data.permission.AccessType as OriginalAccessType
import com.samsung.android.sdk.health.data.permission.Permission as OriginalPermission

class SamsungHealthManagerTest {
    private val deviceManager = mockk<DeviceManager>()
    private val store = mockk<HealthDataStore> { every { getDeviceManager() } returns deviceManager }
    private val sut = SamsungHealthManager(store)
    private val activity = mockk<Activity>()
    private val heartRateRead = OriginalPermission.of(DataTypes.HEART_RATE, OriginalAccessType.READ)

    @Test
    fun `granted permissions map back to the asked-for ones`() =
        runTest {
            coEvery { store.getGrantedPermissions(any()) } returns setOf(heartRateRead)

            val granted =
                sut.grantedPermissions(
                    setOf(HealthType.HEART_RATE, HealthType.STEPS),
                    setOf(HealthType.HEART_RATE),
                )

            assertEquals(setOf(Permission(HealthType.HEART_RATE, AccessType.READ)), granted)
            assertFalse(sut.isAuthorized(setOf(HealthType.HEART_RATE, HealthType.STEPS)))
            assertTrue(sut.isAuthorized(setOf(HealthType.HEART_RATE)))
        }

    @Test
    fun `request permissions asks for every type`() =
        runTest {
            coEvery { store.requestPermissions(any(), activity) } answers { firstArg() }

            val granted =
                sut.requestPermissions(
                    activity,
                    read = HealthType.entries.toSet(),
                    write = HealthType.entries.filter { it.isWritable }.toSet(),
                )

            assertEquals(HealthType.entries.size + HealthType.entries.count { it.isWritable }, granted.size)
            coVerify { store.requestPermissions(match { it.size == granted.size }, activity) }
        }

    @Test
    fun `permissions reject invalid input before calling Samsung Health`() =
        runTest {
            assertFailsWith<SamsungHealthException.InvalidValue> { sut.grantedPermissions(emptySet()) }
            assertFailsWith<SamsungHealthException.InvalidType> {
                sut.requestPermissions(activity, emptySet(), setOf(HealthType.STEPS))
            }
            coVerify(exactly = 0) { store.getGrantedPermissions(any()) }
            coVerify(exactly = 0) { store.requestPermissions(any(), any()) }
        }

    @Test
    fun `resolve starts the fix only when Samsung Health offers one`() {
        val fixable =
            mockk<ResolvablePlatformException> {
                every { hasResolution } returns true
                every { resolve(activity) } just Runs
            }
        assertTrue(sut.resolve(activity, SamsungHealthException.Resolvable("update", fixable)))
        verify { fixable.resolve(activity) }

        val unfixable = mockk<ResolvablePlatformException> { every { hasResolution } returns false }
        assertFalse(sut.resolve(activity, SamsungHealthException.Resolvable("disabled", unfixable)))
    }

    @Test
    fun `devices are mapped to library devices`() =
        runTest {
            val watch =
                mockk<OriginalDevice> {
                    every { id } returns "watch-1"
                    every { deviceType } returns DeviceGroup.WATCH
                    every { manufacturer } returns "Samsung"
                    every { model } returns "SM-R960"
                    every { name } returns "Galaxy Watch"
                }
            coEvery { deviceManager.getLocalDevice() } returns watch
            coEvery { deviceManager.getOwnDevices() } returns listOf(watch)
            coEvery { deviceManager.getDevice("watch-1") } returns watch
            coEvery { deviceManager.getDevice("unknown") } returns null

            val expected = Device("watch-1", "WATCH", "Samsung", "SM-R960", "Galaxy Watch")
            assertEquals(expected, sut.localDevice())
            assertEquals(listOf(expected), sut.devices())
            assertEquals(expected, sut.device("watch-1"))
            assertNull(sut.device("unknown"))
        }

    @Test
    fun `reporter shares one store across services`() {
        val reporter = SamsungHealthReporter(store)
        assertNotNull(reporter.reader)
        assertNotNull(reporter.writer)
        assertNotNull(reporter.observer)
        assertNotNull(reporter.manager)
    }

    @Test
    fun `availability checks for the Samsung Health app`() {
        val packageManager = mockk<PackageManager>()
        val context = mockk<Context> { every { this@mockk.packageManager } returns packageManager }

        every { packageManager.getPackageInfo("com.sec.android.app.shealth", 0) } returns PackageInfo()
        assertTrue(SamsungHealthReporter.isAvailable(context))

        every { packageManager.getPackageInfo("com.sec.android.app.shealth", 0) } throws
            PackageManager.NameNotFoundException()
        assertFalse(SamsungHealthReporter.isAvailable(context))
    }
}
