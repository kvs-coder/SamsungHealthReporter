package com.kvs.samsunghealthreporter.payload

import com.kvs.samsunghealthreporter.END
import com.kvs.samsunghealthreporter.START
import com.kvs.samsunghealthreporter.allSamples
import com.kvs.samsunghealthreporter.bloodGlucose
import com.kvs.samsunghealthreporter.bloodOxygen
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.decorator.sample
import com.kvs.samsunghealthreporter.exercise
import com.kvs.samsunghealthreporter.heartRate
import com.kvs.samsunghealthreporter.model.Payload
import com.kvs.samsunghealthreporter.model.payload.BloodGlucose
import com.kvs.samsunghealthreporter.model.payload.BloodOxygen
import com.kvs.samsunghealthreporter.model.payload.BloodPressure
import com.kvs.samsunghealthreporter.model.payload.BodyComposition
import com.kvs.samsunghealthreporter.model.payload.BodyTemperature
import com.kvs.samsunghealthreporter.model.payload.EnergyScore
import com.kvs.samsunghealthreporter.model.payload.Exercise
import com.kvs.samsunghealthreporter.model.payload.FloorsClimbed
import com.kvs.samsunghealthreporter.model.payload.HeartRate
import com.kvs.samsunghealthreporter.model.payload.IrregularHeartRhythmNotification
import com.kvs.samsunghealthreporter.model.payload.Nutrition
import com.kvs.samsunghealthreporter.model.payload.Sample
import com.kvs.samsunghealthreporter.model.payload.SkinTemperature
import com.kvs.samsunghealthreporter.model.payload.Sleep
import com.kvs.samsunghealthreporter.model.payload.SleepApnea
import com.kvs.samsunghealthreporter.model.payload.UserProfile
import com.kvs.samsunghealthreporter.model.payload.WaterIntake
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.sleep
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.UserDataPoint
import com.samsung.android.sdk.health.data.data.entries.ExerciseSession
import com.samsung.android.sdk.health.data.data.entries.SleepSession
import com.samsung.android.sdk.health.data.request.DataType
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import com.samsung.android.sdk.health.data.data.entries.SkinTemperature as OriginalSkinTemperature

class PayloadContractTest {
    private val factories: Map<HealthType, Payload.Factory<out Sample>> =
        mapOf(
            HealthType.HEART_RATE to HeartRate,
            HealthType.BLOOD_OXYGEN to BloodOxygen,
            HealthType.BLOOD_PRESSURE to BloodPressure,
            HealthType.BLOOD_GLUCOSE to BloodGlucose,
            HealthType.BODY_COMPOSITION to BodyComposition,
            HealthType.BODY_TEMPERATURE to BodyTemperature,
            HealthType.SKIN_TEMPERATURE to SkinTemperature,
            HealthType.EXERCISE to Exercise,
            HealthType.FLOORS_CLIMBED to FloorsClimbed,
            HealthType.SLEEP to Sleep,
            HealthType.NUTRITION to Nutrition,
            HealthType.WATER_INTAKE to WaterIntake,
            HealthType.ENERGY_SCORE to EnergyScore,
            HealthType.IRREGULAR_HEART_RHYTHM_NOTIFICATION to IrregularHeartRhythmNotification,
            HealthType.SLEEP_APNEA to SleepApnea,
        )

    @Test
    fun `every payload factory decodes its own json`() {
        assertEquals(allSamples.map { it.healthType }.toSet(), factories.keys)
        allSamples.forEach { sut ->
            assertEquals(sut, factories.getValue(sut.healthType).decode(sut.json), sut.healthType.identifier)
        }
    }

    @Test
    fun `optional values left out survive the conversion`() {
        val minimal =
            bloodGlucose.copy(
                harmonized =
                    bloodGlucose.harmonized.copy(
                        sampleSource = null,
                        insulinInjected = null,
                        mealTimestamp = null,
                        medicationTaken = null,
                        series = emptyList(),
                    ),
            )
        val converted = assertIs<BloodGlucose>(HealthType.BLOOD_GLUCOSE.sample(minimal.asOriginal))
        assertEquals(minimal.harmonized, converted.harmonized)
    }

    @Test
    fun `series-less heart rate and blood oxygen`() {
        val heart = heartRate.copy(harmonized = heartRate.harmonized.copy(series = emptyList()))
        assertEquals(heart.harmonized, assertIs<HeartRate>(HealthType.HEART_RATE.sample(heart.asOriginal)).harmonized)
        val oxygen = bloodOxygen.copy(harmonized = bloodOxygen.harmonized.copy(series = emptyList()))
        assertEquals(
            oxygen.harmonized,
            assertIs<BloodOxygen>(HealthType.BLOOD_OXYGEN.sample(oxygen.asOriginal)).harmonized,
        )
    }

    @Test
    fun `exercise without count type, log or route`() {
        val session =
            exercise.harmonized.sessions
                .single()
                .copy(countType = null, count = null, log = emptyList(), route = emptyList())
        val minimal = exercise.copy(harmonized = exercise.harmonized.copy(sessions = listOf(session)))
        val converted = assertIs<Exercise>(HealthType.EXERCISE.sample(minimal.asOriginal))
        assertEquals(session, converted.harmonized.sessions.single())
    }

    @Test
    fun `exercise session as Samsung Health stores it without optional lists`() {
        val stored =
            ExerciseSession
                .builder()
                .setExerciseType(DataType.ExerciseType.PredefinedExerciseType.WALKING)
                .setStartTime(Instant.ofEpochMilli(START))
                .setEndTime(Instant.ofEpochMilli(END))
                .setDuration(Duration.ofHours(1))
                .setCalories(100f)
                .build()
        val point =
            HealthDataPoint
                .builder()
                .setStartTime(Instant.ofEpochMilli(START))
                .setEndTime(Instant.ofEpochMilli(END))
                .addFieldData(DataType.ExerciseType.EXERCISE_TYPE, DataType.ExerciseType.PredefinedExerciseType.WALKING)
                .addFieldData(DataType.ExerciseType.SESSIONS, listOf(stored))
                .build()
        val session = assertIs<Exercise>(HealthType.EXERCISE.sample(point)).harmonized.sessions.single()
        assertNull(session.countType)
        assertEquals(emptyList(), session.log)
        assertEquals(emptyList(), session.route)
    }

    @Test
    fun `sleep session without stages`() {
        val point =
            HealthDataPoint
                .builder()
                .setStartTime(Instant.ofEpochMilli(START))
                .setEndTime(Instant.ofEpochMilli(END))
                .addFieldData(DataType.SleepType.DURATION, Duration.ofHours(1))
                .addFieldData(
                    DataType.SleepType.SESSIONS,
                    listOf(
                        SleepSession.of(
                            Instant.ofEpochMilli(START),
                            Instant.ofEpochMilli(END),
                            Duration.ofHours(1),
                            null,
                        ),
                    ),
                ).build()
        val converted = assertIs<Sleep>(HealthType.SLEEP.sample(point))
        assertEquals(
            emptyList(),
            converted.harmonized.sessions
                .single()
                .stages,
        )
        assertNull(converted.harmonized.sleepScore)
        val noStages =
            sleep.copy(
                harmonized =
                    sleep.harmonized.copy(
                        sessions =
                            listOf(
                                sleep.harmonized.sessions
                                    .single()
                                    .copy(stages = emptyList()),
                            ),
                    ),
            )
        assertEquals(
            emptyList(),
            assertIs<Sleep>(HealthType.SLEEP.sample(noStages.asOriginal))
                .harmonized.sessions
                .single()
                .stages,
        )
    }

    @Test
    fun `skin temperature series`() {
        val series =
            mockk<OriginalSkinTemperature> {
                every { skinTemperature } returns 33.5f
                every { min } returns 33f
                every { max } returns 34f
                every { startTime } returns Instant.ofEpochMilli(START)
                every { endTime } returns Instant.ofEpochMilli(END)
            }
        val point =
            mockk<HealthDataPoint>(relaxed = true) {
                every { uid } returns "uid"
                every { startTime } returns Instant.ofEpochMilli(START)
                every { endTime } returns Instant.ofEpochMilli(END)
                every { zoneOffset } returns null
                every { dataSource } returns null
                every { clientDataId } returns null
                every { getValue(DataType.SkinTemperatureType.SKIN_TEMPERATURE) } returns 33.5f
                every { getValue(DataType.SkinTemperatureType.MIN_SKIN_TEMPERATURE) } returns null
                every { getValue(DataType.SkinTemperatureType.MAX_SKIN_TEMPERATURE) } returns null
                every { getValue(DataType.SkinTemperatureType.SERIES_DATA) } returns listOf(series)
            }
        val sut = assertIs<SkinTemperature>(HealthType.SKIN_TEMPERATURE.sample(point))
        assertEquals(listOf(SkinTemperature.Series(33.5f, 33f, 34f, START, END)), sut.harmonized.series)
    }

    @Test
    fun `user profile without gender`() {
        val point =
            mockk<UserDataPoint> {
                every {
                    getValue(any<com.samsung.android.sdk.health.data.data.Field<Any>>())
                } returns null
            }
        assertEquals(UserProfile(), UserProfile.from(point))
    }
}
