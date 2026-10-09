package com.kvs.samsunghealthreporter.type

import com.kvs.samsunghealthreporter.END
import com.kvs.samsunghealthreporter.START
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.asMap
import com.kvs.samsunghealthreporter.decorator.asLocalDateGroup
import com.kvs.samsunghealthreporter.decorator.asLocalTimeGroup
import com.kvs.samsunghealthreporter.model.Aggregate
import com.kvs.samsunghealthreporter.model.Device
import com.kvs.samsunghealthreporter.model.TimeGroup
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.type.Aggregation
import com.kvs.samsunghealthreporter.model.type.TimeGroupUnit
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ValueTypeTest {
    @Test
    fun `time range round trips`() {
        val sut = TimeRange(START, END)
        assertEquals(sut, TimeRange.decode(sut.json))
        assertEquals(sut, TimeRange.make(mapOf("start" to START, "end" to END)))
    }

    @Test
    fun `time range must end after it starts`() {
        assertFailsWith<SamsungHealthException.InvalidValue> { TimeRange(END, START) }
        assertFailsWith<SamsungHealthException.InvalidValue> { TimeRange(START, START) }
        assertFailsWith<SamsungHealthException.InvalidValue> { TimeRange.make(mapOf("start" to END, "end" to START)) }
    }

    @Test
    fun `day covers one local calendar day`() {
        val sut = TimeRange.day(START)
        val zone = ZoneId.systemDefault()
        val start = Instant.ofEpochMilli(sut.start).atZone(zone)
        assertEquals(0, start.hour)
        assertEquals(Instant.ofEpochMilli(START).atZone(zone).toLocalDate(), start.toLocalDate())
        assertEquals(start.plusDays(1).toInstant().toEpochMilli(), sut.end)
    }

    @Test
    fun `last days ends with today`() {
        val sut = TimeRange.lastDays(7, START)
        assertEquals(TimeRange.day(START).end, sut.end)
        assertEquals(
            Instant
                .ofEpochMilli(
                    TimeRange.day(START).start,
                ).atZone(ZoneId.systemDefault())
                .minusDays(6)
                .toInstant()
                .toEpochMilli(),
            sut.start,
        )
        assertFailsWith<SamsungHealthException.InvalidValue> { TimeRange.lastDays(0) }
    }

    @Test
    fun `time group needs a positive multiplier`() {
        assertFailsWith<SamsungHealthException.InvalidValue> { TimeGroup(TimeGroupUnit.HOURLY, 0) }
    }

    @Test
    fun `time group round trips and is validated when decoded`() {
        val sut = TimeGroup(TimeGroupUnit.HOURLY, 2)
        val json =
            kotlinx.serialization.json.Json
                .encodeToString(TimeGroup.serializer(), sut)
        assertEquals(
            sut,
            kotlinx.serialization.json.Json
                .decodeFromString(TimeGroup.serializer(), json),
        )
        assertFailsWith<SamsungHealthException.InvalidValue> {
            kotlinx.serialization.json.Json.decodeFromString(
                TimeGroup.serializer(),
                """{"unit":"HOURLY","multiplier":0}""",
            )
        }
    }

    @Test
    fun `date groups reject time-of-day units`() {
        TimeGroupUnit.entries.forEach { TimeGroup(it).asLocalTimeGroup }
        assertFailsWith<SamsungHealthException.InvalidValue> { TimeGroup(TimeGroupUnit.HOURLY).asLocalDateGroup }
        assertFailsWith<SamsungHealthException.InvalidValue> { TimeGroup(TimeGroupUnit.MINUTELY).asLocalDateGroup }
        TimeGroup(TimeGroupUnit.WEEKLY).asLocalDateGroup
    }

    @Test
    fun `aggregate takes its unit from the aggregation`() {
        val sut = Aggregate(Aggregation.WATER_INTAKE_TOTAL, START, END, 1500.0)
        assertEquals("mL", sut.unit)
        assertEquals(sut, Aggregate.decode(sut.json))
        assertEquals(sut, Aggregate.make(sut.json.asMap()))
    }

    @Test
    fun `device round trips`() {
        val sut = Device("device-1", "WATCH", "Samsung", "SM-R960", "Galaxy Watch")
        assertEquals(sut, Device.decode(sut.json))
        assertEquals(sut, Device.make(sut.json.asMap()))
    }
}
