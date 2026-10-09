package com.kvs.samsunghealthreporter.service

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.bodyComposition
import com.kvs.samsunghealthreporter.energyScore
import com.kvs.samsunghealthreporter.heartRate
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.waterIntake
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.error.InvalidRequestException
import com.samsung.android.sdk.health.data.request.DeleteDataRequest
import com.samsung.android.sdk.health.data.request.InsertDataRequest
import com.samsung.android.sdk.health.data.request.UpdateDataRequest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Robolectric: the Samsung Health Data SDK parcels data points while it builds write requests. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SamsungHealthWriterTest {
    private val store =
        mockk<HealthDataStore> {
            coEvery { insertData(any<InsertDataRequest<HealthDataPoint>>()) } just Runs
            coEvery { updateData(any<UpdateDataRequest<HealthDataPoint>>()) } just Runs
            coEvery { deleteData(any()) } just Runs
        }
    private val sut = SamsungHealthWriter(store) { "generated" }

    @Test
    fun `insert assigns missing client data ids and writes once per type`() =
        runTest {
            val inserted =
                sut.insert(
                    listOf(heartRate, waterIntake, bodyComposition, waterIntake.copy(clientDataId = "own")),
                )

            assertEquals(listOf("client-1", "generated", "generated", "own"), inserted.map { it.clientDataId })
            coVerify(exactly = 3) { store.insertData(any<InsertDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `insert one sample returns it with its client data id`() =
        runTest {
            assertEquals("generated", sut.insert(waterIntake).clientDataId)
        }

    @Test
    fun `insert wraps rejected input`() =
        runTest {
            coEvery { store.insertData(any<InsertDataRequest<HealthDataPoint>>()) } throws
                InvalidRequestException(1001, "out of range")
            assertFailsWith<SamsungHealthException.InvalidValue> { sut.insert(heartRate) }
        }

    @Test
    fun `ownership errors are wrapped`() =
        runTest {
            coEvery { store.insertData(any<InsertDataRequest<HealthDataPoint>>()) } throws
                com.samsung.android.sdk.health.data.error
                    .AuthorizationException(2002, "not yours")
            assertFailsWith<SamsungHealthException.NotAuthorized> { sut.insert(waterIntake) }
        }

    @Test
    fun `update finds samples by uid or client data id`() =
        runTest {
            sut.update(listOf(heartRate, waterIntake.copy(clientDataId = "own")))
            coVerify(exactly = 2) { store.updateData(any<UpdateDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `update without uid or client data id throws`() =
        runTest {
            assertFailsWith<SamsungHealthException.InvalidValue> { sut.update(waterIntake) }
            coVerify(exactly = 0) { store.updateData(any<UpdateDataRequest<HealthDataPoint>>()) }
        }

    @Test
    fun `delete by uid, client data id or sample`() =
        runTest {
            sut.delete(HealthType.HEART_RATE, listOf("uid-1", "uid-2"))
            sut.deleteByClientDataIds(HealthType.WATER_INTAKE, listOf("own"))
            sut.delete(heartRate)
            sut.delete(waterIntake.copy(clientDataId = "own"))
            coVerify(exactly = 4) { store.deleteData(any<DeleteDataRequest>()) }
        }

    @Test
    fun `delete rejects invalid input before calling Samsung Health`() =
        runTest {
            assertFailsWith<SamsungHealthException.InvalidValue> { sut.delete(HealthType.HEART_RATE, emptyList()) }
            assertFailsWith<SamsungHealthException.InvalidType> { sut.delete(HealthType.ENERGY_SCORE, listOf("uid")) }
            assertFailsWith<SamsungHealthException.InvalidType> { sut.delete(energyScore) }
            assertFailsWith<SamsungHealthException.InvalidValue> { sut.delete(waterIntake) }
            coVerify(exactly = 0) { store.deleteData(any<DeleteDataRequest>()) }
        }
}
