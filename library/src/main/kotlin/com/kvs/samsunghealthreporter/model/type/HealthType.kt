package com.kvs.samsunghealthreporter.model.type

import com.kvs.samsunghealthreporter.SamsungHealthException
import kotlinx.serialization.Serializable

/**
 * **HealthType** one Samsung Health data type.
 *
 * @property identifier **String** stable library identifier, e.g. `heart_rate`
 * @property isReadable **Boolean** data points can be read with **SamsungHealthReader.read**
 * @property isWritable **Boolean** data points can be inserted, updated and deleted with **SamsungHealthWriter**
 * @property isObservable **Boolean** changes can be read with **SamsungHealthObserver**
 */
@Serializable
public enum class HealthType(
    public val identifier: String,
    public val isReadable: Boolean,
    public val isWritable: Boolean,
    public val isObservable: Boolean,
) {
    /** steps, aggregate only: [Aggregation.STEPS_TOTAL] */
    STEPS("steps", false, false, false),

    /** the step goal, aggregate only: [Aggregation.STEPS_GOAL_LAST] */
    STEPS_GOAL("steps_goal", false, false, false),

    /** the daily activity summary, aggregate only */
    ACTIVITY_SUMMARY("activity_summary", false, false, false),

    /** the active calories burned goal, aggregate only */
    ACTIVE_CALORIES_BURNED_GOAL("active_calories_burned_goal", false, false, false),

    /** the active time goal, aggregate only */
    ACTIVE_TIME_GOAL("active_time_goal", false, false, false),

    /** heart rate, **HeartRate** */
    HEART_RATE("heart_rate", true, true, true),

    /** blood oxygen saturation, **BloodOxygen** */
    BLOOD_OXYGEN("blood_oxygen", true, true, true),

    /** blood pressure, **BloodPressure** */
    BLOOD_PRESSURE("blood_pressure", true, true, true),

    /** blood glucose, **BloodGlucose** */
    BLOOD_GLUCOSE("blood_glucose", true, true, true),

    /** weight and body composition, **BodyComposition** */
    BODY_COMPOSITION("body_composition", true, true, true),

    /** body temperature, **BodyTemperature** */
    BODY_TEMPERATURE("body_temperature", true, true, true),

    /** skin temperature, **SkinTemperature** */
    SKIN_TEMPERATURE("skin_temperature", true, false, true),

    /** exercise, **Exercise** */
    EXERCISE("exercise", true, true, true),

    /** permission-only type that grants access to **Exercise.Session.route** */
    EXERCISE_LOCATION("exercise_location", false, false, false),

    /** floors climbed, **FloorsClimbed** */
    FLOORS_CLIMBED("floors_climbed", true, true, true),

    /** sleep, **Sleep** */
    SLEEP("sleep", true, true, true),

    /** the sleep goal, aggregate only */
    SLEEP_GOAL("sleep_goal", false, false, false),

    /** nutrition, **Nutrition** */
    NUTRITION("nutrition", true, true, true),

    /** the nutrition goal, aggregate only */
    NUTRITION_GOAL("nutrition_goal", false, false, false),

    /** water intake, **WaterIntake** */
    WATER_INTAKE("water_intake", true, true, true),

    /** the water intake goal, aggregate only */
    WATER_INTAKE_GOAL("water_intake_goal", false, false, false),

    /** the daily energy score, **EnergyScore** */
    ENERGY_SCORE("energy_score", true, false, true),

    /** the user profile, **UserProfile**, read with **SamsungHealthReader.userProfile** */
    USER_PROFILE("user_profile", false, false, false),

    /** irregular heart rhythm notifications, **IrregularHeartRhythmNotification** (Samsung Health Data SDK 1.1.0) */
    IRREGULAR_HEART_RHYTHM_NOTIFICATION("irregular_heart_rhythm_notification", true, false, true),

    /** sleep apnea signs, **SleepApnea** (Samsung Health Data SDK 1.1.0) */
    SLEEP_APNEA("sleep_apnea", true, false, true),
    ;

    /** **List<Aggregation>** the aggregations available for this type */
    public val aggregations: List<Aggregation> get() = Aggregation.entries.filter { it.healthType == this }

    /** Factory of **HealthType** */
    public companion object {
        /**
         * Finds a type by its identifier.
         *
         * @param identifier **String** e.g. `heart_rate`
         * @return **HealthType** the type
         * @throws SamsungHealthException.InvalidType when no type has this identifier
         */
        public fun make(from: String): HealthType =
            entries.firstOrNull { it.identifier == from }
                ?: throw SamsungHealthException.InvalidType("Invalid HealthType identifier: $from")
    }
}
