package com.kvs.samsunghealthreporter.service

import com.kvs.samsunghealthreporter.END
import com.kvs.samsunghealthreporter.START
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.heartRate
import com.kvs.samsunghealthreporter.model.payload.HeartRate
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.response
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.Change
import com.samsung.android.sdk.health.data.data.ChangeType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.ChangedDataRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class SamsungHealthObserverTest {
    private val store = mockk<HealthDataStore>()
    private var clock = END
    private val sut = SamsungHealthObserver(store) { clock }

    @Test
    fun `changes collects upserts and deletes over pages`() =
        runTest {
            coEvery { store.readChanges(any<ChangedDataRequest<HealthDataPoint>>()) } returnsMany
                listOf(response(listOf(upsert()), "next"), response(listOf(delete("gone"))))

            val changeSet = sut.changes(HealthType.HEART_RATE, START)

            assertEquals(HealthType.HEART_RATE, changeSet.type)
            assertEquals(START, changeSet.since)
            assertEquals(END, changeSet.until)
            assertEquals(heartRate.harmonized, assertIs<HeartRate>(changeSet.upserted.single()).harmonized)
            assertEquals(listOf("gone"), changeSet.deletedUids)
        }

    @Test
    fun `changes rejects invalid input before calling Samsung Health`() =
        runTest {
            assertFailsWith<SamsungHealthException.InvalidType> { sut.changes(HealthType.STEPS, START) }
            assertFailsWith<SamsungHealthException.InvalidValue> { sut.changes(HealthType.HEART_RATE, END, START) }
            assertFailsWith<SamsungHealthException.InvalidType> { sut.observe(HealthType.USER_PROFILE, START) }
            coVerify(exactly = 0) { store.readChanges(any<ChangedDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `observe emits only non-empty change sets and advances the window`() =
        runTest {
            coEvery { store.readChanges(any<ChangedDataRequest<HealthDataPoint>>()) } returnsMany
                listOf(response(listOf(upsert())), response(emptyList()), response(listOf(delete("gone"))))

            val changeSets =
                sut
                    .observe(HealthType.HEART_RATE, START)
                    .take(2)
                    .toList()

            assertEquals(1, changeSets[0].upserted.size)
            assertEquals(listOf("gone"), changeSets[1].deletedUids)
            assertEquals(END, changeSets[1].since)
            coVerify(exactly = 3) { store.readChanges(any<ChangedDataRequest<HealthDataPoint>>()) }
        }

    private fun upsert(): Change<HealthDataPoint> =
        mockk {
            every { changeType } returns ChangeType.UPSERT
            every { upsertDataPoint } returns heartRate.asOriginal
        }

    private fun delete(uid: String): Change<HealthDataPoint> =
        mockk {
            every { changeType } returns ChangeType.DELETE
            every { deleteDataUid } returns uid
        }
}
