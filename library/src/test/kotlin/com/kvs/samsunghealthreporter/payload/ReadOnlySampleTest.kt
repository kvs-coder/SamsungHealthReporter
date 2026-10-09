package com.kvs.samsunghealthreporter.payload

import com.kvs.samsunghealthreporter.END
import com.kvs.samsunghealthreporter.START
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.collect
import com.kvs.samsunghealthreporter.decorator.sample
import com.kvs.samsunghealthreporter.model.payload.EnergyScore
import com.kvs.samsunghealthreporter.model.payload.IrregularHeartRhythmNotification
import com.kvs.samsunghealthreporter.model.payload.SkinTemperature
import com.kvs.samsunghealthreporter.model.payload.SleepApnea
import com.kvs.samsunghealthreporter.model.payload.UserProfile
import com.kvs.samsunghealthreporter.model.type.Gender
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.IrregularHeartRhythmStatus
import com.kvs.samsunghealthreporter.model.type.SleepApneaSign
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.UserDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class ReadOnlySampleTest {
    private fun point(): HealthDataPoint.Builder =
        HealthDataPoint
            .builder()
            .setStartTime(Instant.ofEpochMilli(START), ZoneOffset.ofHours(2))
            .setEndTime(Instant.ofEpochMilli(END), ZoneOffset.ofHours(2))

    @Test
    fun `skin temperature from data point`() {
        val sut =
            HealthType.SKIN_TEMPERATURE.sample(
                point()
                    .addFieldData(DataType.SkinTemperatureType.SKIN_TEMPERATURE, 33.5f)
                    .addFieldData(DataType.SkinTemperatureType.MIN_SKIN_TEMPERATURE, 33f)
                    .addFieldData(DataType.SkinTemperatureType.MAX_SKIN_TEMPERATURE, 34f)
                    .build(),
            )
        assertIs<SkinTemperature>(sut)
        assertEquals(SkinTemperature.Harmonized(33.5f, 33f, 34f), sut.harmonized)
        assertEquals(START, sut.startTimestamp)
        assertEquals(END, sut.endTimestamp)
        assertEquals(7200, sut.zoneOffsetSeconds)
    }

    @Test
    fun `energy score from data point`() {
        val sut =
            HealthType.ENERGY_SCORE.sample(
                point().addFieldData(DataType.EnergyScoreType.ENERGY_SCORE, 82f).build(),
            )
        assertIs<EnergyScore>(sut)
        assertEquals(82f, sut.harmonized.score)
    }

    @Test
    fun `irregular heart rhythm notification from data point`() {
        val sut =
            HealthType.IRREGULAR_HEART_RHYTHM_NOTIFICATION.sample(
                point()
                    .addFieldData(
                        DataType.IrregularHeartRhythmNotificationType.STATUS,
                        DataType.IrregularHeartRhythmNotificationType.IrregularHeartRhythmStatus.DETECTED,
                    ).build(),
            )
        assertIs<IrregularHeartRhythmNotification>(sut)
        assertEquals(IrregularHeartRhythmStatus.DETECTED, sut.harmonized.status)
    }

    @Test
    fun `sleep apnea from data point`() {
        val sut =
            HealthType.SLEEP_APNEA.sample(
                point()
                    .addFieldData(DataType.SleepApneaType.DETECTED_SIGN, DataType.SleepApneaType.DetectedSign.DETECTED)
                    .build(),
            )
        assertIs<SleepApnea>(sut)
        assertEquals(SleepApneaSign.DETECTED, sut.harmonized.detectedSign)
    }

    /** Samsung Health doesn't build empty data points, so "missing" points carry an unrelated field. */
    private fun pointWithoutRequiredFields(): HealthDataPoint =
        point().addFieldData(DataType.HeartRateType.MIN_HEART_RATE, 60f).build()

    @Test
    fun `data point without required field throws`() {
        assertFailsWith<SamsungHealthException.InvalidValue> {
            HealthType.ENERGY_SCORE.sample(pointWithoutRequiredFields())
        }
    }

    @Test
    fun `collect skips data points that can't be converted`() {
        val valid = point().addFieldData(DataType.EnergyScoreType.ENERGY_SCORE, 82f).build()
        assertEquals(2, HealthType.ENERGY_SCORE.collect(listOf(valid, pointWithoutRequiredFields(), valid)).size)
    }

    @Test
    fun `types without a sample payload throw`() {
        HealthType.entries.filter { !it.isReadable }.forEach { type ->
            assertFailsWith<SamsungHealthException.InvalidType>(
                type.identifier,
            ) { type.sample(pointWithoutRequiredFields()) }
        }
    }

    @Test
    fun `user profile from data point`() {
        val point =
            mockk<UserDataPoint> {
                every { getValue(DataType.UserProfileDataType.NICKNAME) } returns "Runner"
                every { getValue(DataType.UserProfileDataType.GENDER) } returns
                    DataType.UserProfileDataType.Gender.GENDER_FEMALE
                every { getValue(DataType.UserProfileDataType.DATE_OF_BIRTH) } returns "19900101"
                every { getValue(DataType.UserProfileDataType.HEIGHT) } returns 170f
                every { getValue(DataType.UserProfileDataType.WEIGHT) } returns 60f
            }
        val sut = UserProfile.from(point)
        assertEquals(UserProfile("Runner", Gender.GENDER_FEMALE, "19900101", 170f, 60f), sut)
        assertEquals(sut, UserProfile.decode(sut.json))
    }
}
