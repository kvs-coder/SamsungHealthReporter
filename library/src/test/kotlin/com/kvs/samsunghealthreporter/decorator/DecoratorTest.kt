package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.END
import com.kvs.samsunghealthreporter.START
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.SourceFilter
import com.kvs.samsunghealthreporter.model.TimeGroup
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.type.Aggregation
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.MealType
import com.kvs.samsunghealthreporter.model.type.TimeGroupUnit
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.device.AccessoryType
import com.samsung.android.sdk.health.data.device.DeviceGroup
import com.samsung.android.sdk.health.data.device.DeviceType
import com.samsung.android.sdk.health.data.error.HealthDataException
import com.samsung.android.sdk.health.data.error.PlatformInternalException
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.LocalDateGroupUnit
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import com.samsung.android.sdk.health.data.data.DataSource as OriginalDataSource
import com.samsung.android.sdk.health.data.device.Device as OriginalDevice

class DecoratorTest {
    @Test
    fun `map values of every supported kind become json`() {
        val sut =
            mapOf(
                "null" to null,
                "json" to JsonPrimitive("raw"),
                "string" to "text",
                "number" to 1.5,
                "boolean" to true,
                "enum" to MealType.LUNCH,
                "map" to mapOf(1 to "one"),
                "list" to listOf(1, 2),
                "array" to arrayOf("a", "b"),
            ).asJsonElement

        assertIs<JsonObject>(sut)
        assertEquals(JsonNull, sut["null"])
        assertEquals(JsonPrimitive("raw"), sut["json"])
        assertEquals(JsonPrimitive(true), sut["boolean"])
        assertEquals(JsonPrimitive("LUNCH"), sut["enum"])
        assertEquals(JsonObject(mapOf("1" to JsonPrimitive("one"))), sut["map"])
        assertEquals(JsonArray(listOf(JsonPrimitive(1), JsonPrimitive(2))), sut["list"])
        assertEquals(JsonArray(listOf(JsonPrimitive("a"), JsonPrimitive("b"))), sut["array"])
    }

    @Test
    fun `unsupported map value throws`() {
        assertFailsWith<SamsungHealthException.InvalidValue> { mapOf("date" to Instant.EPOCH).asJsonElement }
    }

    @Test
    fun `any other Samsung Health error becomes Platform with its code`() {
        val internal = PlatformInternalException(9000, "database")
        val sut = assertIs<SamsungHealthException.Platform>(internal.wrapped)
        assertEquals(9000, sut.errorCode)
        assertEquals(internal, sut.cause)
        assertNull(assertIs<SamsungHealthException.Platform>(HealthDataException(null, "unknown").wrapped).errorCode)
    }

    @Test
    fun `devices keep their group or accessory type`() {
        fun device(type: DeviceType) =
            mockk<OriginalDevice> {
                every { id } returns "id"
                every { deviceType } returns type
                every { manufacturer } returns null
                every { model } returns null
                every { name } returns null
            }
        assertEquals("WEIGHT_SCALE", device(AccessoryType.WEIGHT_SCALE).harmonized.type)
        assertEquals("RING", device(DeviceGroup.RING).harmonized.type)
    }

    @Test
    fun `enum without a matching entry throws`() {
        assertFailsWith<SamsungHealthException.InvalidValue> {
            DataType.SleepType.StageType.UNDEFINED
                .mapped<TimeGroupUnit>()
        }
    }

    @Test
    fun `unsupported aggregate value throws`() {
        val data =
            mockk<AggregatedData<Any>> {
                every { startTime } returns Instant.ofEpochMilli(START)
                every { endTime } returns Instant.ofEpochMilli(END)
                every { value } returns "text"
            }
        assertFailsWith<SamsungHealthException.InvalidValue> { data.harmonize(Aggregation.STEPS_TOTAL) }
    }

    @Test
    fun `instantaneous data point has no end`() {
        // Samsung Health rejects heart rate without an end time, but not instantaneous types like body temperature.
        val point =
            HealthDataPoint
                .builder()
                .setStartTime(Instant.ofEpochMilli(START))
                .addFieldData(DataType.BodyTemperatureType.BODY_TEMPERATURE, 36.6f)
                .build()
        assertEquals(START, point.startTimestamp)
        assertNull(point.endTimestamp)
        assertFailsWith<SamsungHealthException.InvalidValue> { point.requiredEndTimestamp }
        assertFailsWith<SamsungHealthException.InvalidValue> { HealthType.HEART_RATE.sample(point) }
    }

    @Test
    fun `data source is copied from Samsung Health`() {
        val point =
            mockk<HealthDataPoint> {
                every { dataSource } returns
                    mockk<OriginalDataSource> {
                        every { appId } returns "com.example"
                        every { deviceId } returns "device-1"
                    }
                every { zoneOffset } returns null
            }
        assertEquals(DataSource("com.example", "device-1"), point.source)
        assertNull(point.zoneOffsetSeconds)
    }

    @Test
    fun `every date unit maps to a local date group`() {
        val units =
            mapOf(
                TimeGroupUnit.DAILY to LocalDateGroupUnit.DAILY,
                TimeGroupUnit.WEEKLY to LocalDateGroupUnit.WEEKLY,
                TimeGroupUnit.MONTHLY to LocalDateGroupUnit.MONTHLY,
                TimeGroupUnit.YEARLY to LocalDateGroupUnit.YEARLY,
            )
        units.keys.forEach { TimeGroup(it, 2).asLocalDateGroup }
        assertEquals(4, units.size)
    }

    @Test
    fun `source filters map to Samsung Health filters`() {
        SourceFilter.LocalDevice.asOriginal
        SourceFilter.SamsungHealth.asOriginal
        SourceFilter.App("com.example").asOriginal
    }

    @Test
    fun `today is the default day`() {
        val sut = TimeRange.day()
        assertEquals(TimeRange.day(System.currentTimeMillis()).start, sut.start)
    }
}
