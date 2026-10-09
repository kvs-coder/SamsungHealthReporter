package com.samsung.android.sdk.health.data.request

import com.samsung.android.sdk.health.data.data.AggregateOperation
import com.samsung.android.sdk.health.data.data.DataPoint
import com.samsung.android.sdk.health.data.data.Field
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.UserDataPoint
import com.samsung.android.sdk.health.data.data.entries.BloodGlucose
import com.samsung.android.sdk.health.data.data.entries.ExerciseSession
import com.samsung.android.sdk.health.data.data.entries.HeartRate
import com.samsung.android.sdk.health.data.data.entries.OxygenSaturation
import com.samsung.android.sdk.health.data.data.entries.SkinTemperature
import com.samsung.android.sdk.health.data.data.entries.SleepSession
import java.time.Duration
import java.time.Instant
import java.time.LocalTime

@Suppress("ktlint")
abstract class DataType(val name: String) {
    interface Readable<T : DataPoint, S : ReadDataRequest.Builder<T>> {
        val readDataRequestBuilder: S
    }

    interface ChangeReadable<T : DataPoint> {
        val changedDataRequestBuilder: ChangedDataRequest.BasicBuilder<T>
    }

    interface Writeable<T : DataPoint> {
        val insertDataRequestBuilder: InsertDataRequest.BasicBuilder<T>
        val updateDataRequestBuilder: UpdateDataRequest.BasicBuilder<T>
        val deleteDataRequestBuilder: DeleteDataRequest.BasicBuilder
    }

    class ActiveCaloriesBurnedGoalType : DataType("active_calories_burned_goal") {
        companion object {
            @JvmField val LAST: AggregateOperation<Int, AggregateRequest.AllSourceLocalDateBuilder<Int>> = AggregateOperation { AggregateRequest.AllSourceLocalDateBuilder("active_calories_burned_goal.last") }
        }
    }

    class ActiveTimeGoalType : DataType("active_time_goal") {
        companion object {
            @JvmField val LAST: AggregateOperation<Duration, AggregateRequest.AllSourceLocalDateBuilder<Duration>> = AggregateOperation { AggregateRequest.AllSourceLocalDateBuilder("active_time_goal.last") }
        }
    }

    class ActivitySummaryType : DataType("activity_summary") {
        companion object {
            @JvmField val TOTAL_ACTIVE_CALORIES_BURNED: AggregateOperation<Float, AggregateRequest.LocalTimeBuilder<Float>> = AggregateOperation { AggregateRequest.LocalTimeBuilder("activity_summary.total_active_calories_burned") }
            @JvmField val TOTAL_ACTIVE_TIME: AggregateOperation<Duration, AggregateRequest.LocalTimeBuilder<Duration>> = AggregateOperation { AggregateRequest.LocalTimeBuilder("activity_summary.total_active_time") }
            @JvmField val TOTAL_CALORIES_BURNED: AggregateOperation<Float, AggregateRequest.LocalTimeBuilder<Float>> = AggregateOperation { AggregateRequest.LocalTimeBuilder("activity_summary.total_calories_burned") }
            @JvmField val TOTAL_DISTANCE: AggregateOperation<Float, AggregateRequest.LocalTimeBuilder<Float>> = AggregateOperation { AggregateRequest.LocalTimeBuilder("activity_summary.total_distance") }
        }
    }

    class BloodGlucoseType : DataType("blood_glucose"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        enum class MealStatus { UNDEFINED, FASTING, AFTER_MEAL, BEFORE_BREAKFAST, AFTER_BREAKFAST, BEFORE_LUNCH, AFTER_LUNCH, BEFORE_DINNER, AFTER_DINNER, AFTER_BED_TIME, AFTER_SNACK, BEFORE_MEAL, GENERAL, BEFORE_SLEEP }
        enum class MeasurementType { UNDEFINED, WHOLE_BLOOD, PLASMA, SERUM }
        enum class SampleSourceType { UNDEFINED, VENOUS, CAPILLARY }
        companion object {
            @JvmField val GLUCOSE_LEVEL: Field<Float> = Field("glucose_level", "Float")
            @JvmField val INSULIN_INJECTED: Field<Float> = Field("insulin_injected", "Float")
            @JvmField val MEAL_STATUS: Field<MealStatus> = Field("meal_status", "MealStatus")
            @JvmField val MEAL_TIME: Field<Instant> = Field("meal_time", "Instant")
            @JvmField val MEASUREMENT_TYPE: Field<MeasurementType> = Field("measurement_type", "MeasurementType")
            @JvmField val MEDICATION_TAKEN: Field<Boolean> = Field("medication_taken", "Boolean")
            @JvmField val SAMPLE_SOURCE_TYPE: Field<SampleSourceType> = Field("sample_source_type", "SampleSourceType")
            @JvmField val SERIES_DATA: Field<List<BloodGlucose>> = Field("series_data", "List<BloodGlucose>")
        }
    }

    class BloodOxygenType : DataType("blood_oxygen"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        companion object {
            @JvmField val MAX_OXYGEN_SATURATION: Field<Float> = Field("max_oxygen_saturation", "Float")
            @JvmField val MIN_OXYGEN_SATURATION: Field<Float> = Field("min_oxygen_saturation", "Float")
            @JvmField val OXYGEN_SATURATION: Field<Float> = Field("oxygen_saturation", "Float")
            @JvmField val SERIES_DATA: Field<List<OxygenSaturation>> = Field("series_data", "List<OxygenSaturation>")
        }
    }

    class BloodPressureType : DataType("blood_pressure"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        companion object {
            @JvmField val DIASTOLIC: Field<Float> = Field("diastolic", "Float")
            @JvmField val MEAN: Field<Float> = Field("mean", "Float")
            @JvmField val MEDICATION_TAKEN: Field<Boolean> = Field("medication_taken", "Boolean")
            @JvmField val PULSE_RATE: Field<Int> = Field("pulse_rate", "Int")
            @JvmField val SYSTOLIC: Field<Float> = Field("systolic", "Float")
        }
    }

    class BodyCompositionType : DataType("body_composition"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        companion object {
            @JvmField val BODY_FAT: Field<Float> = Field("body_fat", "Float")
            @JvmField val BODY_FAT_MASS: Field<Float> = Field("body_fat_mass", "Float")
            @JvmField val BODY_MASS_INDEX: Field<Float> = Field("body_mass_index", "Float")
            @JvmField val FAT_FREE: Field<Float> = Field("fat_free", "Float")
            @JvmField val FAT_FREE_MASS: Field<Float> = Field("fat_free_mass", "Float")
            @JvmField val HEIGHT: Field<Float> = Field("height", "Float")
            @JvmField val MUSCLE_MASS: Field<Float> = Field("muscle_mass", "Float")
            @JvmField val SKELETAL_MUSCLE: Field<Float> = Field("skeletal_muscle", "Float")
            @JvmField val SKELETAL_MUSCLE_MASS: Field<Float> = Field("skeletal_muscle_mass", "Float")
            @JvmField val TOTAL_BODY_WATER: Field<Float> = Field("total_body_water", "Float")
            @JvmField val WEIGHT: Field<Float> = Field("weight", "Float")
            @JvmField val BASAL_METABOLIC_RATE: Field<Int> = Field("basal_metabolic_rate", "Int")
        }
    }

    class BodyTemperatureType : DataType("body_temperature"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        companion object {
            @JvmField val BODY_TEMPERATURE: Field<Float> = Field("body_temperature", "Float")
        }
    }

    class EnergyScoreType : DataType("energy_score"), Readable<HealthDataPoint, ReadDataRequest.LocalDateBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.LocalDateBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        companion object {
            @JvmField val ENERGY_SCORE: Field<Float> = Field("energy_score", "Float")
        }
    }

    class ExerciseLocationType : DataType("exercise_location") {
    }

    class ExerciseType : DataType("exercise"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        enum class PredefinedExerciseType { UNDEFINED, OTHER, WALKING, RUNNING, STAIR_CLIMBING, TRACK_RUNNING, BASEBALL, SOFTBALL, CRICKET, GOLF, BOWLING, HOCKEY, RUGBY, BASKETBALL, SOCCER, HANDBALL, AMERICAN_FOOTBALL, VOLLEYBALL, BEACH_VOLLEYBALL, SQUASH, TENNIS, BADMINTON, TABLE_TENNIS, RACQUETBALL, BOXING, MARTIAL_ARTS, BALLET, DANCING, BALLROOM_DANCING, PILATES, YOGA, STRETCHING, JUMP_ROPE, HULA_HOOPING, PUSH_UPS, PULL_UPS, SIT_UPS, CIRCUIT_TRAINING, MOUNTAIN_CLIMBERS, JUMPING_JACKS, BURPEES, BENCH_PRESS, SQUATS, LUNGES, LEG_PRESSES, LEG_EXTENSIONS, LEG_CURLS, BACK_EXTENSIONS, LAT_PULLDOWNS, DEADLIFTS, SHOULDER_PRESSES, FRONT_RAISES, LATERAL_RAISES, CRUNCH, LEG_RAISES, PLANK, ARM_CURLS, ARM_EXTENSIONS, SKATERS, HIGH_KNEES, INLINE_SKATING, HANG_GLIDING, ARCHERY, HORSEBACK_RIDING, BIKING, FLYING_DISC, ROLLER_SKATING, AEROBICS, HIKING, ROCK_CLIMBING, BACKPACKING, MOUNTAIN_BIKING, ORIENTEERING, POOL_SWIMMING, AQUA_AEROBICS, CANOEING, SAILING, SCUBA_DIVING, SNORKELING, KAYAKING, KITESURFING, RAFTING, ROWING, WINDSURFING, YACHTING, WATER_SKIING, STEP_MACHINE, WEIGHT_MACHINE, STATIONARY_BIKING, ROWING_MACHINE, TREADMILL, ELLIPTICAL, STAIR_CLIMBING_MACHINE, CROSS_COUNTRY_SKIING, SKIING, ICE_DANCING, ICE_SKATING, ICE_HOCKEY, SNOWBOARDING, ALPINE_SKIING, SNOWSHOEING, TRIATHLON, DUATHLON, AQUATHLON, AQUABIKE, CROSS_TRIATHLON, CROSS_DUATHLON, BREAK, COOL_DOWN, WARM_UP, TRANSITION, ZUMBA, OPEN_WATER_SWIMMING }
        enum class CountType { UNDEFINED, STRIDE, STROKE, SWING, REPETITION }
        enum class StrokeType { UNDEFINED, BUTTERFLY, BACKSTROKE, FREESTYLE, BREASTSTROKE, KICK_BOARD, MIXED }
        companion object {
            @JvmField val CUSTOM_TITLE: Field<String> = Field("custom_title", "String")
            @JvmField val EXERCISE_TYPE: Field<PredefinedExerciseType> = Field("exercise_type", "PredefinedExerciseType")
            @JvmField val SESSIONS: Field<List<ExerciseSession>> = Field("sessions", "List<ExerciseSession>")
            @JvmField val TOTAL_CALORIES: AggregateOperation<Float, AggregateRequest.LocalTimeBuilder<Float>> = AggregateOperation { AggregateRequest.LocalTimeBuilder("exercise.total_calories") }
            @JvmField val TOTAL_DURATION: AggregateOperation<Duration, AggregateRequest.LocalDateBuilder<Duration>> = AggregateOperation { AggregateRequest.LocalDateBuilder("exercise.total_duration") }
        }
    }

    class FloorsClimbedType : DataType("floors_climbed"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        companion object {
            @JvmField val FLOOR: Field<Float> = Field("floor", "Float")
            @JvmField val TOTAL: AggregateOperation<Float, AggregateRequest.LocalTimeBuilder<Float>> = AggregateOperation { AggregateRequest.LocalTimeBuilder("floors_climbed.total") }
        }
    }

    class HeartRateType : DataType("heart_rate"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        companion object {
            @JvmField val HEART_RATE: Field<Float> = Field("heart_rate", "Float")
            @JvmField val MAX_HEART_RATE: Field<Float> = Field("max_heart_rate", "Float")
            @JvmField val MIN_HEART_RATE: Field<Float> = Field("min_heart_rate", "Float")
            @JvmField val SERIES_DATA: Field<List<HeartRate>> = Field("series_data", "List<HeartRate>")
            @JvmField val MAX: AggregateOperation<Float, AggregateRequest.LocalDateBuilder<Float>> = AggregateOperation { AggregateRequest.LocalDateBuilder("heart_rate.max") }
            @JvmField val MIN: AggregateOperation<Float, AggregateRequest.LocalDateBuilder<Float>> = AggregateOperation { AggregateRequest.LocalDateBuilder("heart_rate.min") }
        }
    }

    class IrregularHeartRhythmNotificationType : DataType("irregular_heart_rhythm_notification"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        enum class IrregularHeartRhythmStatus { UNDEFINED, DETECTED }
        companion object {
            @JvmField val STATUS: Field<IrregularHeartRhythmStatus> = Field("status", "IrregularHeartRhythmStatus")
        }
    }

    class NutritionGoalType : DataType("nutrition_goal") {
        companion object {
            @JvmField val LAST_CALORIES: AggregateOperation<Float, AggregateRequest.AllSourceLocalDateBuilder<Float>> = AggregateOperation { AggregateRequest.AllSourceLocalDateBuilder("nutrition_goal.last_calories") }
        }
    }

    class NutritionType : DataType("nutrition"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        enum class MealType { UNDEFINED, BREAKFAST, LUNCH, DINNER, MORNING_SNACK, AFTERNOON_SNACK, EVENING_SNACK }
        companion object {
            @JvmField val CALCIUM: Field<Float> = Field("calcium", "Float")
            @JvmField val CALORIES: Field<Float> = Field("calories", "Float")
            @JvmField val CARBOHYDRATE: Field<Float> = Field("carbohydrate", "Float")
            @JvmField val CHOLESTEROL: Field<Float> = Field("cholesterol", "Float")
            @JvmField val DIETARY_FIBER: Field<Float> = Field("dietary_fiber", "Float")
            @JvmField val IRON: Field<Float> = Field("iron", "Float")
            @JvmField val MONOSATURATED_FAT: Field<Float> = Field("monosaturated_fat", "Float")
            @JvmField val POLYSATURATED_FAT: Field<Float> = Field("polysaturated_fat", "Float")
            @JvmField val POTASSIUM: Field<Float> = Field("potassium", "Float")
            @JvmField val PROTEIN: Field<Float> = Field("protein", "Float")
            @JvmField val SATURATED_FAT: Field<Float> = Field("saturated_fat", "Float")
            @JvmField val SODIUM: Field<Float> = Field("sodium", "Float")
            @JvmField val SUGAR: Field<Float> = Field("sugar", "Float")
            @JvmField val TOTAL_FAT: Field<Float> = Field("total_fat", "Float")
            @JvmField val TRANS_FAT: Field<Float> = Field("trans_fat", "Float")
            @JvmField val VITAMIN_A: Field<Float> = Field("vitamin_a", "Float")
            @JvmField val VITAMIN_C: Field<Float> = Field("vitamin_c", "Float")
            @JvmField val MEAL_TYPE: Field<MealType> = Field("meal_type", "MealType")
            @JvmField val TITLE: Field<String> = Field("title", "String")
            @JvmField val TOTAL_CALORIES: AggregateOperation<Float, AggregateRequest.DualTimeBuilder<Float>> = AggregateOperation { AggregateRequest.DualTimeBuilder("nutrition.total_calories") }
        }
    }

    class SkinTemperatureType : DataType("skin_temperature"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        companion object {
            @JvmField val MAX_SKIN_TEMPERATURE: Field<Float> = Field("max_skin_temperature", "Float")
            @JvmField val MIN_SKIN_TEMPERATURE: Field<Float> = Field("min_skin_temperature", "Float")
            @JvmField val SERIES_DATA: Field<List<SkinTemperature>> = Field("series_data", "List<SkinTemperature>")
            @JvmField val SKIN_TEMPERATURE: Field<Float> = Field("skin_temperature", "Float")
        }
    }

    class SleepApneaType : DataType("sleep_apnea"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        enum class DetectedSign { UNDEFINED, DETECTED, NOT_DETECTED }
        companion object {
            @JvmField val DETECTED_SIGN: Field<DetectedSign> = Field("detected_sign", "DetectedSign")
        }
    }

    class SleepGoalType : DataType("sleep_goal") {
        companion object {
            @JvmField val LAST_BED_TIME: AggregateOperation<LocalTime, AggregateRequest.AllSourceLocalDateBuilder<LocalTime>> = AggregateOperation { AggregateRequest.AllSourceLocalDateBuilder("sleep_goal.last_bed_time") }
            @JvmField val LAST_WAKE_UP_TIME: AggregateOperation<LocalTime, AggregateRequest.AllSourceLocalDateBuilder<LocalTime>> = AggregateOperation { AggregateRequest.AllSourceLocalDateBuilder("sleep_goal.last_wake_up_time") }
        }
    }

    class SleepType : DataType("sleep"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        enum class StageType { UNDEFINED, AWAKE, LIGHT, DEEP, REM }
        companion object {
            @JvmField val DURATION: Field<Duration> = Field("duration", "Duration")
            @JvmField val SESSIONS: Field<List<SleepSession>> = Field("sessions", "List<SleepSession>")
            @JvmField val SLEEP_SCORE: Field<Int> = Field("sleep_score", "Int")
            @JvmField val TOTAL_DURATION: AggregateOperation<Duration, AggregateRequest.LocalDateBuilder<Duration>> = AggregateOperation { AggregateRequest.LocalDateBuilder("sleep.total_duration") }
        }
    }

    class StepsGoalType : DataType("steps_goal") {
        companion object {
            @JvmField val LAST: AggregateOperation<Int, AggregateRequest.AllSourceLocalDateBuilder<Int>> = AggregateOperation { AggregateRequest.AllSourceLocalDateBuilder("steps_goal.last") }
        }
    }

    class StepsType : DataType("steps") {
        companion object {
            @JvmField val TOTAL: AggregateOperation<Long, AggregateRequest.LocalTimeBuilder<Long>> = AggregateOperation { AggregateRequest.LocalTimeBuilder("steps.total") }
        }
    }

    class UserProfileDataType : DataType("user_profile"), Readable<UserDataPoint, ReadDataRequest.UserProfileBuilder> {
        override val readDataRequestBuilder get() = ReadDataRequest.UserProfileBuilder(this)
        enum class Gender { GENDER_UNKNOWN, GENDER_MALE, GENDER_FEMALE }
        companion object {
            @JvmField val DATE_OF_BIRTH: Field<String> = Field("date_of_birth", "String")
            @JvmField val GENDER: Field<Gender> = Field("gender", "Gender")
            @JvmField val HEIGHT: Field<Float> = Field("height", "Float")
            @JvmField val NICKNAME: Field<String> = Field("nickname", "String")
            @JvmField val WEIGHT: Field<Float> = Field("weight", "Float")
        }
    }

    class WaterIntakeGoalType : DataType("water_intake_goal") {
        companion object {
            @JvmField val LAST: AggregateOperation<Float, AggregateRequest.AllSourceLocalDateBuilder<Float>> = AggregateOperation { AggregateRequest.AllSourceLocalDateBuilder("water_intake_goal.last") }
        }
    }

    class WaterIntakeType : DataType("water_intake"), Readable<HealthDataPoint, ReadDataRequest.DualTimeBuilder<HealthDataPoint>>, ChangeReadable<HealthDataPoint>, Writeable<HealthDataPoint> {
        override val readDataRequestBuilder get() = ReadDataRequest.DualTimeBuilder<HealthDataPoint>(this)
        override val changedDataRequestBuilder get() = ChangedDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val insertDataRequestBuilder get() = InsertDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val updateDataRequestBuilder get() = UpdateDataRequest.BasicBuilder<HealthDataPoint>(this)
        override val deleteDataRequestBuilder get() = DeleteDataRequest.BasicBuilder(this)
        companion object {
            @JvmField val AMOUNT: Field<Float> = Field("amount", "Float")
            @JvmField val TOTAL: AggregateOperation<Float, AggregateRequest.DualTimeBuilder<Float>> = AggregateOperation { AggregateRequest.DualTimeBuilder("water_intake.total") }
        }
    }

}

@Suppress("ktlint")
interface DataTypes {
    companion object {
        @JvmField val ACTIVE_CALORIES_BURNED_GOAL = DataType.ActiveCaloriesBurnedGoalType()
        @JvmField val ACTIVE_TIME_GOAL = DataType.ActiveTimeGoalType()
        @JvmField val ACTIVITY_SUMMARY = DataType.ActivitySummaryType()
        @JvmField val BLOOD_GLUCOSE = DataType.BloodGlucoseType()
        @JvmField val BLOOD_OXYGEN = DataType.BloodOxygenType()
        @JvmField val BLOOD_PRESSURE = DataType.BloodPressureType()
        @JvmField val BODY_COMPOSITION = DataType.BodyCompositionType()
        @JvmField val BODY_TEMPERATURE = DataType.BodyTemperatureType()
        @JvmField val ENERGY_SCORE = DataType.EnergyScoreType()
        @JvmField val EXERCISE_LOCATION = DataType.ExerciseLocationType()
        @JvmField val EXERCISE = DataType.ExerciseType()
        @JvmField val FLOORS_CLIMBED = DataType.FloorsClimbedType()
        @JvmField val HEART_RATE = DataType.HeartRateType()
        @JvmField val IRREGULAR_HEART_RHYTHM_NOTIFICATION = DataType.IrregularHeartRhythmNotificationType()
        @JvmField val NUTRITION_GOAL = DataType.NutritionGoalType()
        @JvmField val NUTRITION = DataType.NutritionType()
        @JvmField val SKIN_TEMPERATURE = DataType.SkinTemperatureType()
        @JvmField val SLEEP_APNEA = DataType.SleepApneaType()
        @JvmField val SLEEP_GOAL = DataType.SleepGoalType()
        @JvmField val SLEEP = DataType.SleepType()
        @JvmField val STEPS_GOAL = DataType.StepsGoalType()
        @JvmField val STEPS = DataType.StepsType()
        @JvmField val USER_PROFILE = DataType.UserProfileDataType()
        @JvmField val WATER_INTAKE_GOAL = DataType.WaterIntakeGoalType()
        @JvmField val WATER_INTAKE = DataType.WaterIntakeType()
    }
}
