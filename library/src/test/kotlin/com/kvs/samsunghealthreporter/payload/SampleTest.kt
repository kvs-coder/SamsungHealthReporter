package com.kvs.samsunghealthreporter.payload

import com.kvs.samsunghealthreporter.END
import com.kvs.samsunghealthreporter.START
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.allSamples
import com.kvs.samsunghealthreporter.asMap
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.decorator.sample
import com.kvs.samsunghealthreporter.heartRate
import com.kvs.samsunghealthreporter.model.ChangeSet
import com.kvs.samsunghealthreporter.model.payload.HeartRate
import com.kvs.samsunghealthreporter.model.payload.Sample
import com.kvs.samsunghealthreporter.model.payload.Sleep
import com.kvs.samsunghealthreporter.model.payload.WritableSample
import com.kvs.samsunghealthreporter.model.type.HealthType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SampleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `every payload type has a fixture`() {
        val covered = allSamples.map { it.healthType }.toSet()
        val withPayload = HealthType.entries.filter { it.isReadable }.toSet()
        assertEquals(withPayload, covered)
    }

    @Test
    fun `create then encode then decode`() {
        allSamples.forEach { sut ->
            val decoded = json.decodeFromString(Sample.serializer(), sut.json)
            assertEquals(sut, decoded, sut.healthType.identifier)
        }
    }

    @Test
    fun `encoded sample carries its type as discriminator`() {
        allSamples.forEach { sut ->
            assertEquals(sut.healthType.identifier, sut.json.asMap()["type"], sut.healthType.identifier)
        }
    }

    @Test
    fun `list of mixed samples round trips`() {
        val serializer = ListSerializer(Sample.serializer())
        val encoded = json.encodeToString(serializer, allSamples)
        assertEquals(allSamples, json.decodeFromString(serializer, encoded))
    }

    @Test
    fun `writable samples convert to Samsung Health and back`() {
        allSamples.filterIsInstance<WritableSample>().forEach { sut ->
            val converted = sut.healthType.sample(sut.asOriginal)
            // Samsung Health computes the sleep score itself; apps can't write it.
            val expected = if (sut is Sleep) sut.copy(harmonized = sut.harmonized.copy(sleepScore = null)) else sut
            assertEquals(expected.comparable, converted.comparable, sut.healthType.identifier)
        }
    }

    @Test
    fun `only writable types have writable payloads`() {
        allSamples.forEach { sut ->
            assertEquals(sut.healthType.isWritable, sut is WritableSample, sut.healthType.identifier)
        }
    }

    @Test
    fun `create from map`() {
        val sut =
            HeartRate.make(
                mapOf(
                    "startTimestamp" to START,
                    "endTimestamp" to END,
                    "harmonized" to mapOf("heartRate" to 72.0, "min" to 60, "max" to 90),
                    "uid" to "uid-1",
                    "clientDataId" to "client-1",
                    "zoneOffsetSeconds" to 7200,
                    "dataSource" to mapOf("appId" to "com.example", "deviceId" to "device-1"),
                ),
            )
        assertEquals(heartRate.copy(harmonized = heartRate.harmonized.copy(series = emptyList())), sut)
    }

    @Test
    fun `create from encoded map keeps every field`() {
        assertEquals(heartRate, HeartRate.make(heartRate.json.asMap()))
    }

    @Test
    fun `create from map without required key throws`() {
        val map = heartRate.json.asMap() - "startTimestamp"
        assertFailsWith<SamsungHealthException.InvalidValue> { HeartRate.make(map) }
    }

    @Test
    fun `create from map with wrong value type throws`() {
        val map = heartRate.json.asMap() + ("startTimestamp" to "yesterday")
        assertFailsWith<SamsungHealthException.InvalidValue> { HeartRate.make(map) }
    }

    @Test
    fun `collect skips invalid maps`() {
        val valid = heartRate.json.asMap()
        val sut = HeartRate.collect(listOf(valid, mapOf("uid" to "x"), valid))
        assertEquals(listOf(heartRate, heartRate), sut)
    }

    @Test
    fun `decode invalid json throws`() {
        assertFailsWith<SamsungHealthException.InvalidValue> { HeartRate.decode("{}") }
    }

    @Test
    fun `end before start throws`() {
        assertFailsWith<SamsungHealthException.InvalidValue> { heartRate.copy(endTimestamp = START - 1) }
        assertFailsWith<SamsungHealthException.InvalidValue> { HeartRate.Series(1f, 1f, 1f, END, START) }
    }

    @Test
    fun `copy keeps uid unless replaced`() {
        assertEquals("uid-1", heartRate.copy(clientDataId = "other").uid)
        assertEquals("uid-2", heartRate.copy(uid = "uid-2").uid)
    }

    @Test
    fun `change set round trips with mixed samples`() {
        val sut = ChangeSet(HealthType.HEART_RATE, START, END, listOf(heartRate), listOf("gone"))
        assertEquals(sut, ChangeSet.decode(sut.json))
        assertEquals(sut, ChangeSet.make(sut.json.asMap()))
        assertTrue(ChangeSet(HealthType.HEART_RATE, START, END, emptyList(), emptyList()).isEmpty)
    }

    /** Fields Samsung Health sets itself are left out; it fills a missing zone offset with the device's. */
    private val Sample.comparable: Map<String, Any?>
        get() = json.asMap() - "uid" - "dataSource" - "zoneOffsetSeconds"

    @Test
    fun `zone offset is kept when set`() {
        val converted = heartRate.healthType.sample(heartRate.asOriginal)
        assertEquals(heartRate.zoneOffsetSeconds, converted.zoneOffsetSeconds)
    }
}
