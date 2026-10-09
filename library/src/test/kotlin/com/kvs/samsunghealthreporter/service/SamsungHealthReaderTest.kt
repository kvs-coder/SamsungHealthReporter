package com.kvs.samsunghealthreporter.service

import com.kvs.samsunghealthreporter.END
import com.kvs.samsunghealthreporter.START
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.heartRate
import com.kvs.samsunghealthreporter.model.Aggregate
import com.kvs.samsunghealthreporter.model.SourceFilter
import com.kvs.samsunghealthreporter.model.TimeGroup
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.payload.EnergyScore
import com.kvs.samsunghealthreporter.model.payload.HeartRate
import com.kvs.samsunghealthreporter.model.type.Aggregation
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.TimeGroupUnit
import com.kvs.samsunghealthreporter.response
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.UserDataPoint
import com.samsung.android.sdk.health.data.error.AuthorizationException
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException
import com.samsung.android.sdk.health.data.request.AggregateRequest
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.ReadDataRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class SamsungHealthReaderTest {
    private val store = mockk<HealthDataStore>()
    private val sut = SamsungHealthReader(store)
    private val range = TimeRange(START, END)
    private val point = heartRate.asOriginal

    @Test
    fun `read converts data points and follows pages`() =
        runTest {
            coEvery { store.readData(any<ReadDataRequest<HealthDataPoint>>()) } returnsMany
                listOf(response(listOf(point), "next"), response(listOf(point)))

            val samples = sut.read(HealthType.HEART_RATE, range, source = SourceFilter.SamsungHealth)

            assertEquals(2, samples.size)
            samples.forEach { assertEquals(heartRate.harmonized, assertIs<HeartRate>(it).harmonized) }
            coVerify(exactly = 2) { store.readData(any<ReadDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `read stops at the limit`() =
        runTest {
            coEvery { store.readData(any<ReadDataRequest<HealthDataPoint>>()) } returns
                response(listOf(point, point), "next")

            assertEquals(1, sut.read(HealthType.HEART_RATE, range, limit = 1).size)
            coVerify(exactly = 1) { store.readData(any<ReadDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `read energy score by local date`() =
        runTest {
            val score =
                HealthDataPoint
                    .builder()
                    .setStartTime(Instant.ofEpochMilli(START))
                    .addFieldData(DataType.EnergyScoreType.ENERGY_SCORE, 75f)
                    .build()
            coEvery { store.readData(any<ReadDataRequest<HealthDataPoint>>()) } returns response(listOf(score))

            val page = sut.readPage(HealthType.ENERGY_SCORE, range, source = SourceFilter.App("com.example"))

            assertEquals(75f, assertIs<EnergyScore>(page.items.single()).harmonized.score)
            assertNull(page.nextPageToken)
        }

    @Test
    fun `read rejects invalid input before calling Samsung Health`() =
        runTest {
            assertFailsWith<SamsungHealthException.InvalidType> { sut.read(HealthType.STEPS, range) }
            assertFailsWith<SamsungHealthException.InvalidType> { sut.read(HealthType.USER_PROFILE, range) }
            assertFailsWith<SamsungHealthException.InvalidValue> { sut.read(HealthType.HEART_RATE, range, limit = 0) }
            assertFailsWith<SamsungHealthException.InvalidValue> {
                sut.readPage(
                    HealthType.HEART_RATE,
                    range,
                    pageSize = 0,
                )
            }
            coVerify(exactly = 0) { store.readData(any<ReadDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `read wraps Samsung Health errors`() =
        runTest {
            val denied = AuthorizationException(2000, "denied")
            coEvery { store.readData(any<ReadDataRequest<HealthDataPoint>>()) } throws denied

            val error = assertFailsWith<SamsungHealthException.NotAuthorized> { sut.read(HealthType.HEART_RATE, range) }
            assertEquals(denied, error.cause)

            coEvery { store.readData(any<ReadDataRequest<HealthDataPoint>>()) } throws
                ResolvablePlatformException(3000, "not installed", true)
            assertFailsWith<SamsungHealthException.Resolvable> { sut.read(HealthType.HEART_RATE, range) }
        }

    @Test
    fun `user profile is null without data`() =
        runTest {
            coEvery { store.readData(any<ReadDataRequest<UserDataPoint>>()) } returns response(emptyList())
            assertNull(sut.userProfile())
        }

    @Test
    fun `aggregate steps per bucket and follows pages`() =
        runTest {
            coEvery { store.aggregateData(any<AggregateRequest<Long>>()) } returnsMany
                listOf(
                    response(listOf(aggregated(1200L)), "next"),
                    response(listOf(aggregated<Long>(null))),
                )

            val result = sut.aggregate(Aggregation.STEPS_TOTAL, range, TimeGroup(TimeGroupUnit.HOURLY))

            assertEquals(
                listOf(
                    Aggregate(Aggregation.STEPS_TOTAL, START, END, 1200.0),
                    Aggregate(Aggregation.STEPS_TOTAL, START, END, null),
                ),
                result,
            )
        }

    @Test
    fun `aggregate converts durations and times of day`() =
        runTest {
            coEvery { store.aggregateData(any<AggregateRequest<Duration>>()) } returns
                response(listOf(aggregated(Duration.ofHours(7))))
            assertEquals(25_200_000.0, sut.aggregate(Aggregation.SLEEP_TOTAL_DURATION, range).single().value)

            coEvery { store.aggregateData(any<AggregateRequest<LocalTime>>()) } returns
                response(listOf(aggregated(LocalTime.of(22, 30))))
            assertEquals(81_000.0, sut.aggregate(Aggregation.SLEEP_GOAL_LAST_BED_TIME, range).single().value)
        }

    @Test
    fun `every aggregation reaches Samsung Health`() =
        runTest {
            coEvery { store.aggregateData(any<AggregateRequest<Any>>()) } returns response(emptyList())
            Aggregation.entries.forEach { assertEquals(emptyList(), sut.aggregate(it, range)) }
            coVerify(exactly = Aggregation.entries.size) { store.aggregateData(any<AggregateRequest<Any>>()) }
        }

    @Test
    fun `date-based aggregation can't be grouped by hour`() =
        runTest {
            assertFailsWith<SamsungHealthException.InvalidValue> {
                sut.aggregate(Aggregation.HEART_RATE_MAX, range, TimeGroup(TimeGroupUnit.HOURLY))
            }
            coVerify(exactly = 0) { store.aggregateData(any<AggregateRequest<Any>>()) }
        }

    private fun <T : Any> aggregated(value: T?): AggregatedData<T> =
        mockk {
            every { startTime } returns Instant.ofEpochMilli(START)
            every { endTime } returns Instant.ofEpochMilli(END)
            every { this@mockk.value } returns value
        }
}
