package com.kvs.samsunghealthreporter.type

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.changeReadable
import com.kvs.samsunghealthreporter.decorator.dualTimeReadable
import com.kvs.samsunghealthreporter.decorator.mapped
import com.kvs.samsunghealthreporter.decorator.original
import com.kvs.samsunghealthreporter.decorator.writeable
import com.kvs.samsunghealthreporter.model.type.Aggregation
import com.kvs.samsunghealthreporter.model.type.ExerciseCountType
import com.kvs.samsunghealthreporter.model.type.ExerciseType
import com.kvs.samsunghealthreporter.model.type.Gender
import com.kvs.samsunghealthreporter.model.type.GlucoseMeasurementType
import com.kvs.samsunghealthreporter.model.type.GlucoseSampleSource
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.IrregularHeartRhythmStatus
import com.kvs.samsunghealthreporter.model.type.MealStatus
import com.kvs.samsunghealthreporter.model.type.MealType
import com.kvs.samsunghealthreporter.model.type.SleepApneaSign
import com.kvs.samsunghealthreporter.model.type.SleepStageType
import com.samsung.android.sdk.health.data.request.DataType
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import com.samsung.android.sdk.health.data.request.DataType.IrregularHeartRhythmNotificationType.IrregularHeartRhythmStatus as OriginalIrregularHeartRhythmStatus

class TypeTest {
    @Test
    fun `health type is found by identifier`() {
        HealthType.entries.forEach { assertEquals(it, HealthType.make(it.identifier)) }
        assertFailsWith<SamsungHealthException.InvalidType> { HealthType.make("com.samsung.health.step_count") }
    }

    @Test
    fun `identifiers are unique`() {
        assertEquals(
            HealthType.entries.size,
            HealthType.entries
                .map { it.identifier }
                .toSet()
                .size,
        )
    }

    @Test
    fun `every type maps to a distinct Samsung Health type`() {
        assertEquals(
            HealthType.entries.size,
            HealthType.entries
                .map { it.original }
                .toSet()
                .size,
        )
    }

    @Test
    fun `capabilities match the Samsung Health type`() {
        HealthType.entries.forEach { type ->
            val readable = runCatching { type.dualTimeReadable }.isSuccess || type == HealthType.ENERGY_SCORE
            assertEquals(type.isReadable, readable, type.identifier)
            assertEquals(type.isObservable, runCatching { type.changeReadable }.isSuccess, type.identifier)
            assertEquals(type.isWritable, runCatching { type.writeable }.isSuccess, type.identifier)
        }
    }

    @Test
    fun `writable types are readable`() {
        HealthType.entries.filter { it.isWritable }.forEach { assertTrue(it.isReadable, it.identifier) }
    }

    @Test
    fun `aggregation is found by name and belongs to its type`() {
        Aggregation.entries.forEach {
            assertEquals(it, Aggregation.make(it.name))
            assertTrue(it in it.healthType.aggregations)
        }
        assertFailsWith<SamsungHealthException.InvalidType> { Aggregation.make("STEPS") }
        assertEquals(listOf(Aggregation.STEPS_TOTAL), HealthType.STEPS.aggregations)
    }

    @Test
    fun `value enums map to Samsung Health entries and back`() {
        roundTrip<MealStatus, DataType.BloodGlucoseType.MealStatus>()
        roundTrip<GlucoseMeasurementType, DataType.BloodGlucoseType.MeasurementType>()
        roundTrip<GlucoseSampleSource, DataType.BloodGlucoseType.SampleSourceType>()
        roundTrip<MealType, DataType.NutritionType.MealType>()
        roundTrip<SleepStageType, DataType.SleepType.StageType>()
        roundTrip<ExerciseType, DataType.ExerciseType.PredefinedExerciseType>()
        roundTrip<ExerciseCountType, DataType.ExerciseType.CountType>()
        roundTrip<Gender, DataType.UserProfileDataType.Gender>()
        roundTrip<SleepApneaSign, DataType.SleepApneaType.DetectedSign>()
        roundTrip<IrregularHeartRhythmStatus, OriginalIrregularHeartRhythmStatus>()
    }

    private inline fun <reified L : Enum<L>, reified S : Enum<S>> roundTrip() {
        assertEquals(enumValues<L>().size, enumValues<S>().size, L::class.simpleName)
        enumValues<L>().forEach { assertEquals(it, it.mapped<S>().mapped<L>()) }
    }
}
