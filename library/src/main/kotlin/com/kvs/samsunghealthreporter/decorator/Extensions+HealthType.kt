package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException
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
import com.kvs.samsunghealthreporter.model.payload.WaterIntake
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.ReadDataRequest

internal typealias DualTimeReadable =
    DataType.Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>

internal val HealthType.original: DataType
    get() =
        when (this) {
            HealthType.STEPS -> DataTypes.STEPS
            HealthType.STEPS_GOAL -> DataTypes.STEPS_GOAL
            HealthType.ACTIVITY_SUMMARY -> DataTypes.ACTIVITY_SUMMARY
            HealthType.ACTIVE_CALORIES_BURNED_GOAL -> DataTypes.ACTIVE_CALORIES_BURNED_GOAL
            HealthType.ACTIVE_TIME_GOAL -> DataTypes.ACTIVE_TIME_GOAL
            HealthType.HEART_RATE -> DataTypes.HEART_RATE
            HealthType.BLOOD_OXYGEN -> DataTypes.BLOOD_OXYGEN
            HealthType.BLOOD_PRESSURE -> DataTypes.BLOOD_PRESSURE
            HealthType.BLOOD_GLUCOSE -> DataTypes.BLOOD_GLUCOSE
            HealthType.BODY_COMPOSITION -> DataTypes.BODY_COMPOSITION
            HealthType.BODY_TEMPERATURE -> DataTypes.BODY_TEMPERATURE
            HealthType.SKIN_TEMPERATURE -> DataTypes.SKIN_TEMPERATURE
            HealthType.EXERCISE -> DataTypes.EXERCISE
            HealthType.EXERCISE_LOCATION -> DataTypes.EXERCISE_LOCATION
            HealthType.FLOORS_CLIMBED -> DataTypes.FLOORS_CLIMBED
            HealthType.SLEEP -> DataTypes.SLEEP
            HealthType.SLEEP_GOAL -> DataTypes.SLEEP_GOAL
            HealthType.NUTRITION -> DataTypes.NUTRITION
            HealthType.NUTRITION_GOAL -> DataTypes.NUTRITION_GOAL
            HealthType.WATER_INTAKE -> DataTypes.WATER_INTAKE
            HealthType.WATER_INTAKE_GOAL -> DataTypes.WATER_INTAKE_GOAL
            HealthType.ENERGY_SCORE -> DataTypes.ENERGY_SCORE
            HealthType.USER_PROFILE -> DataTypes.USER_PROFILE
            HealthType.IRREGULAR_HEART_RHYTHM_NOTIFICATION -> DataTypes.IRREGULAR_HEART_RHYTHM_NOTIFICATION
            HealthType.SLEEP_APNEA -> DataTypes.SLEEP_APNEA
        }

/** The read capability of a type read by local time; energy score is read by local date instead. */
internal val HealthType.dualTimeReadable: DualTimeReadable
    get() =
        when (this) {
            HealthType.HEART_RATE -> DataTypes.HEART_RATE
            HealthType.BLOOD_OXYGEN -> DataTypes.BLOOD_OXYGEN
            HealthType.BLOOD_PRESSURE -> DataTypes.BLOOD_PRESSURE
            HealthType.BLOOD_GLUCOSE -> DataTypes.BLOOD_GLUCOSE
            HealthType.BODY_COMPOSITION -> DataTypes.BODY_COMPOSITION
            HealthType.BODY_TEMPERATURE -> DataTypes.BODY_TEMPERATURE
            HealthType.SKIN_TEMPERATURE -> DataTypes.SKIN_TEMPERATURE
            HealthType.EXERCISE -> DataTypes.EXERCISE
            HealthType.FLOORS_CLIMBED -> DataTypes.FLOORS_CLIMBED
            HealthType.SLEEP -> DataTypes.SLEEP
            HealthType.NUTRITION -> DataTypes.NUTRITION
            HealthType.WATER_INTAKE -> DataTypes.WATER_INTAKE
            HealthType.IRREGULAR_HEART_RHYTHM_NOTIFICATION -> DataTypes.IRREGULAR_HEART_RHYTHM_NOTIFICATION
            HealthType.SLEEP_APNEA -> DataTypes.SLEEP_APNEA
            HealthType.ENERGY_SCORE,
            HealthType.STEPS,
            HealthType.STEPS_GOAL,
            HealthType.ACTIVITY_SUMMARY,
            HealthType.ACTIVE_CALORIES_BURNED_GOAL,
            HealthType.ACTIVE_TIME_GOAL,
            HealthType.EXERCISE_LOCATION,
            HealthType.SLEEP_GOAL,
            HealthType.NUTRITION_GOAL,
            HealthType.WATER_INTAKE_GOAL,
            HealthType.USER_PROFILE,
            -> throw SamsungHealthException.InvalidType("$identifier is not readable by time range")
        }

internal val HealthType.changeReadable: DataType.ChangeReadable<HealthDataPoint>
    get() =
        when (this) {
            HealthType.ENERGY_SCORE -> DataTypes.ENERGY_SCORE
            HealthType.HEART_RATE -> DataTypes.HEART_RATE
            HealthType.BLOOD_OXYGEN -> DataTypes.BLOOD_OXYGEN
            HealthType.BLOOD_PRESSURE -> DataTypes.BLOOD_PRESSURE
            HealthType.BLOOD_GLUCOSE -> DataTypes.BLOOD_GLUCOSE
            HealthType.BODY_COMPOSITION -> DataTypes.BODY_COMPOSITION
            HealthType.BODY_TEMPERATURE -> DataTypes.BODY_TEMPERATURE
            HealthType.SKIN_TEMPERATURE -> DataTypes.SKIN_TEMPERATURE
            HealthType.EXERCISE -> DataTypes.EXERCISE
            HealthType.FLOORS_CLIMBED -> DataTypes.FLOORS_CLIMBED
            HealthType.SLEEP -> DataTypes.SLEEP
            HealthType.NUTRITION -> DataTypes.NUTRITION
            HealthType.WATER_INTAKE -> DataTypes.WATER_INTAKE
            HealthType.IRREGULAR_HEART_RHYTHM_NOTIFICATION -> DataTypes.IRREGULAR_HEART_RHYTHM_NOTIFICATION
            HealthType.SLEEP_APNEA -> DataTypes.SLEEP_APNEA
            HealthType.STEPS,
            HealthType.STEPS_GOAL,
            HealthType.ACTIVITY_SUMMARY,
            HealthType.ACTIVE_CALORIES_BURNED_GOAL,
            HealthType.ACTIVE_TIME_GOAL,
            HealthType.EXERCISE_LOCATION,
            HealthType.SLEEP_GOAL,
            HealthType.NUTRITION_GOAL,
            HealthType.WATER_INTAKE_GOAL,
            HealthType.USER_PROFILE,
            -> throw SamsungHealthException.InvalidType("$identifier is not observable")
        }

internal val HealthType.writeable: DataType.Writeable<HealthDataPoint>
    get() =
        when (this) {
            HealthType.HEART_RATE -> DataTypes.HEART_RATE
            HealthType.BLOOD_OXYGEN -> DataTypes.BLOOD_OXYGEN
            HealthType.BLOOD_PRESSURE -> DataTypes.BLOOD_PRESSURE
            HealthType.BLOOD_GLUCOSE -> DataTypes.BLOOD_GLUCOSE
            HealthType.BODY_COMPOSITION -> DataTypes.BODY_COMPOSITION
            HealthType.BODY_TEMPERATURE -> DataTypes.BODY_TEMPERATURE
            HealthType.EXERCISE -> DataTypes.EXERCISE
            HealthType.FLOORS_CLIMBED -> DataTypes.FLOORS_CLIMBED
            HealthType.SLEEP -> DataTypes.SLEEP
            HealthType.NUTRITION -> DataTypes.NUTRITION
            HealthType.WATER_INTAKE -> DataTypes.WATER_INTAKE
            HealthType.SKIN_TEMPERATURE,
            HealthType.ENERGY_SCORE,
            HealthType.IRREGULAR_HEART_RHYTHM_NOTIFICATION,
            HealthType.SLEEP_APNEA,
            HealthType.STEPS,
            HealthType.STEPS_GOAL,
            HealthType.ACTIVITY_SUMMARY,
            HealthType.ACTIVE_CALORIES_BURNED_GOAL,
            HealthType.ACTIVE_TIME_GOAL,
            HealthType.EXERCISE_LOCATION,
            HealthType.SLEEP_GOAL,
            HealthType.NUTRITION_GOAL,
            HealthType.WATER_INTAKE_GOAL,
            HealthType.USER_PROFILE,
            -> throw SamsungHealthException.InvalidType("$identifier is read-only")
        }

/** Converts a data point of this type to its payload. */
internal fun HealthType.sample(point: HealthDataPoint): Sample =
    when (this) {
        HealthType.HEART_RATE -> HeartRate.from(point)
        HealthType.BLOOD_OXYGEN -> BloodOxygen.from(point)
        HealthType.BLOOD_PRESSURE -> BloodPressure.from(point)
        HealthType.BLOOD_GLUCOSE -> BloodGlucose.from(point)
        HealthType.BODY_COMPOSITION -> BodyComposition.from(point)
        HealthType.BODY_TEMPERATURE -> BodyTemperature.from(point)
        HealthType.SKIN_TEMPERATURE -> SkinTemperature.from(point)
        HealthType.EXERCISE -> Exercise.from(point)
        HealthType.FLOORS_CLIMBED -> FloorsClimbed.from(point)
        HealthType.SLEEP -> Sleep.from(point)
        HealthType.NUTRITION -> Nutrition.from(point)
        HealthType.WATER_INTAKE -> WaterIntake.from(point)
        HealthType.ENERGY_SCORE -> EnergyScore.from(point)
        HealthType.IRREGULAR_HEART_RHYTHM_NOTIFICATION -> IrregularHeartRhythmNotification.from(point)
        HealthType.SLEEP_APNEA -> SleepApnea.from(point)
        HealthType.STEPS,
        HealthType.STEPS_GOAL,
        HealthType.ACTIVITY_SUMMARY,
        HealthType.ACTIVE_CALORIES_BURNED_GOAL,
        HealthType.ACTIVE_TIME_GOAL,
        HealthType.EXERCISE_LOCATION,
        HealthType.SLEEP_GOAL,
        HealthType.NUTRITION_GOAL,
        HealthType.WATER_INTAKE_GOAL,
        HealthType.USER_PROFILE,
        -> throw SamsungHealthException.InvalidType("$identifier has no sample payload")
    }

/** Converts data points of this type, skipping the ones that can't be converted. */
internal fun HealthType.collect(points: List<HealthDataPoint>): List<Sample> =
    points.mapNotNull { point ->
        try {
            sample(point)
        } catch (_: SamsungHealthException.InvalidValue) {
            null
        }
    }
