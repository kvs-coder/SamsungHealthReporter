package com.kvs.samsunghealthreporter.service

import android.app.Activity
import android.content.Context
import com.kvs.samsunghealthreporter.END
import com.kvs.samsunghealthreporter.START
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.SamsungHealthReporter
import com.kvs.samsunghealthreporter.allSamples
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.heartRate
import com.kvs.samsunghealthreporter.model.SourceFilter
import com.kvs.samsunghealthreporter.model.TimeGroup
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.payload.WritableSample
import com.kvs.samsunghealthreporter.model.type.Aggregation
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.TimeGroupUnit
import com.kvs.samsunghealthreporter.response
import com.samsung.android.sdk.health.data.HealthDataService
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.data.Change
import com.samsung.android.sdk.health.data.data.ChangeType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.UserDataPoint
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException
import com.samsung.android.sdk.health.data.request.AggregateRequest
import com.samsung.android.sdk.health.data.request.ChangedDataRequest
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.InsertDataRequest
import com.samsung.android.sdk.health.data.request.ReadDataRequest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Robolectric: inserting builds write requests, which parcel data points. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ServiceEdgeCaseTest {
    private val store = mockk<HealthDataStore>()
    private val range = TimeRange(START, END)

    @Test
    fun `read stops when Samsung Health has fewer samples than the limit`() =
        runTest {
            coEvery { store.readData(any<ReadDataRequest<HealthDataPoint>>()) } returns
                response(listOf(heartRate.asOriginal))
            val sut = SamsungHealthReader(store)
            assertEquals(1, sut.read(HealthType.HEART_RATE, range, limit = 5, source = SourceFilter.LocalDevice).size)
            sut.readPage(HealthType.ENERGY_SCORE, range)
        }

    @Test
    fun `read keeps paging while under the limit`() =
        runTest {
            coEvery { store.readData(any<ReadDataRequest<HealthDataPoint>>()) } returnsMany
                listOf(response(listOf(heartRate.asOriginal), "next"), response(listOf(heartRate.asOriginal)))
            assertEquals(2, SamsungHealthReader(store).read(HealthType.HEART_RATE, range, limit = 3).size)
            coVerify(exactly = 2) { store.readData(any<ReadDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `user profile from Samsung Health`() =
        runTest {
            val profile =
                mockk<UserDataPoint> {
                    every { getValue(any<com.samsung.android.sdk.health.data.data.Field<Any>>()) } returns null
                    every { getValue(DataType.UserProfileDataType.NICKNAME) } returns "Runner"
                }
            coEvery { store.readData(any<ReadDataRequest<UserDataPoint>>()) } returns response(listOf(profile))
            assertEquals("Runner", SamsungHealthReader(store).userProfile()?.nickname)
        }

    @Test
    fun `date-based aggregation groups by day but not by minute`() =
        runTest {
            coEvery { store.aggregateData(any<AggregateRequest<Any>>()) } returns
                response(emptyList<AggregatedData<Any>>())
            val sut = SamsungHealthReader(store)
            sut.aggregate(Aggregation.HEART_RATE_MIN, range, TimeGroup(TimeGroupUnit.DAILY))
            sut.aggregate(Aggregation.WATER_INTAKE_TOTAL, range, TimeGroup(TimeGroupUnit.MINUTELY, 15))
            sut.aggregate(Aggregation.WATER_INTAKE_GOAL_LAST, range, TimeGroup(TimeGroupUnit.MONTHLY))
            assertFailsWith<SamsungHealthException.InvalidValue> {
                sut.aggregate(Aggregation.SLEEP_TOTAL_DURATION, range, TimeGroup(TimeGroupUnit.MINUTELY))
            }
            coVerify(exactly = 3) { store.aggregateData(any<AggregateRequest<Any>>()) }
        }

    @Test
    fun `changes skip data points that can't be converted`() =
        runTest {
            val broken =
                mockk<Change<HealthDataPoint>> {
                    every { changeType } returns ChangeType.UPSERT
                    every { upsertDataPoint } returns
                        HealthDataPoint
                            .builder()
                            .setStartTime(Instant.ofEpochMilli(START))
                            .setEndTime(Instant.ofEpochMilli(END))
                            .addFieldData(DataType.HeartRateType.MIN_HEART_RATE, 60f)
                            .build()
                }
            val unknownDelete =
                mockk<Change<HealthDataPoint>> {
                    every { changeType } returns ChangeType.DELETE
                    every { deleteDataUid } returns null
                }
            coEvery { store.readChanges(any<ChangedDataRequest<HealthDataPoint>>()) } returns
                response(listOf(broken, unknownDelete))
            val changes = SamsungHealthObserver(store) { END }.changes(HealthType.HEART_RATE, START)
            assertEquals(emptyList(), changes.upserted)
            assertEquals(emptyList(), changes.deletedUids)
        }

    @Test
    fun `every writable type gets a client data id`() =
        runTest {
            coEvery { store.insertData(any<InsertDataRequest<HealthDataPoint>>()) } just Runs
            val writable = allSamples.filterIsInstance<WritableSample>()
            val inserted = SamsungHealthWriter(store) { "generated" }.insert(writable.map { it.withoutClientDataId() })
            assertEquals(writable.map { "generated" }, inserted.map { it.clientDataId })
            coVerify(exactly = writable.size) { store.insertData(any<InsertDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `resolve ignores errors that didn't come from Samsung Health`() {
        val manager = SamsungHealthManager(store)
        assertFalse(
            manager.resolve(mockk<Activity>(), SamsungHealthException.Resolvable("other", IllegalStateException())),
        )
    }

    @Test
    fun `request permissions defaults to read only`() =
        runTest {
            coEvery { store.requestPermissions(any(), any()) } answers { firstArg() }
            assertEquals(1, SamsungHealthManager(store).requestPermissions(mockk(), setOf(HealthType.STEPS)).size)
        }

    @Test
    fun `reporter connects through Samsung Health`() {
        val context = mockk<Context>()
        every { context.applicationContext } returns context
        // getStore is @JvmStatic in the SDK: mock both the object and its static bridge.
        mockkObject(HealthDataService)
        mockkStatic(HealthDataService::class)
        try {
            every { HealthDataService.getStore(context) } returns store
            assertNotNull(SamsungHealthReporter(context).reader)

            every { HealthDataService.getStore(context) } throws
                ResolvablePlatformException(3000, "not installed", true)
            assertFailsWith<SamsungHealthException.Resolvable> { SamsungHealthReporter(context) }
        } finally {
            unmockkStatic(HealthDataService::class)
            unmockkObject(HealthDataService)
        }
    }

    @Test
    fun `exceptions keep message and optional cause`() {
        assertNull(SamsungHealthException.InvalidType("type").cause)
        assertNull(SamsungHealthException.Platform("platform").errorCode)
        assertEquals("value", SamsungHealthException.InvalidValue("value").message)
        assertNull(SamsungHealthException.NotAuthorized("denied").cause)
    }

    private fun WritableSample.withoutClientDataId(): WritableSample =
        when (this) {
            is com.kvs.samsunghealthreporter.model.payload.HeartRate -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.BloodOxygen -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.BloodPressure -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.BloodGlucose -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.BodyComposition -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.BodyTemperature -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.Exercise -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.FloorsClimbed -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.Sleep -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.Nutrition -> copy(clientDataId = null)
            is com.kvs.samsunghealthreporter.model.payload.WaterIntake -> copy(clientDataId = null)
        }
}
