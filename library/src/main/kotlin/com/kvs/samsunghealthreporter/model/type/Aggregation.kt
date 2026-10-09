package com.kvs.samsunghealthreporter.model.type

import com.kvs.samsunghealthreporter.SamsungHealthException
import kotlinx.serialization.Serializable

/**
 * **Aggregation** one aggregate operation Samsung Health computes over a [healthType].
 *
 * Durations are reported in milliseconds, times of day in seconds since midnight.
 *
 * @property healthType **HealthType** the aggregated type
 * @property unit **String** unit of **Aggregate.value**
 * @property supportsTimeGrouping **Boolean** results can be grouped by minute or hour; date-based aggregations
 * only group by day, week, month or year
 */
@Serializable
public enum class Aggregation(
    public val healthType: HealthType,
    public val unit: String,
    public val supportsTimeGrouping: Boolean,
) {
    /** total steps */
    STEPS_TOTAL(HealthType.STEPS, "count", true),

    /** the last step goal */
    STEPS_GOAL_LAST(HealthType.STEPS_GOAL, "count", false),

    /** total calories burned due to activity */
    ACTIVITY_SUMMARY_TOTAL_ACTIVE_CALORIES_BURNED(HealthType.ACTIVITY_SUMMARY, "kcal", true),

    /** total active time */
    ACTIVITY_SUMMARY_TOTAL_ACTIVE_TIME(HealthType.ACTIVITY_SUMMARY, "ms", true),

    /** total calories burned */
    ACTIVITY_SUMMARY_TOTAL_CALORIES_BURNED(HealthType.ACTIVITY_SUMMARY, "kcal", true),

    /** total distance moved while active */
    ACTIVITY_SUMMARY_TOTAL_DISTANCE(HealthType.ACTIVITY_SUMMARY, "m", true),

    /** the last active calories burned goal */
    ACTIVE_CALORIES_BURNED_GOAL_LAST(HealthType.ACTIVE_CALORIES_BURNED_GOAL, "kcal", false),

    /** the last active time goal */
    ACTIVE_TIME_GOAL_LAST(HealthType.ACTIVE_TIME_GOAL, "ms", false),

    /** total exercise calories */
    EXERCISE_TOTAL_CALORIES(HealthType.EXERCISE, "kcal", true),

    /** total exercise duration */
    EXERCISE_TOTAL_DURATION(HealthType.EXERCISE, "ms", false),

    /** total floors climbed */
    FLOORS_CLIMBED_TOTAL(HealthType.FLOORS_CLIMBED, "floor", true),

    /** highest heart rate */
    HEART_RATE_MAX(HealthType.HEART_RATE, "bpm", false),

    /** lowest heart rate */
    HEART_RATE_MIN(HealthType.HEART_RATE, "bpm", false),

    /** total food calories */
    NUTRITION_TOTAL_CALORIES(HealthType.NUTRITION, "kcal", true),

    /** the last nutrition goal */
    NUTRITION_GOAL_LAST_CALORIES(HealthType.NUTRITION_GOAL, "kcal", false),

    /** total sleep duration */
    SLEEP_TOTAL_DURATION(HealthType.SLEEP, "ms", false),

    /** the last bedtime goal */
    SLEEP_GOAL_LAST_BED_TIME(HealthType.SLEEP_GOAL, "s", false),

    /** the last wake-up time goal */
    SLEEP_GOAL_LAST_WAKE_UP_TIME(HealthType.SLEEP_GOAL, "s", false),

    /** total water intake */
    WATER_INTAKE_TOTAL(HealthType.WATER_INTAKE, "mL", true),

    /** the last water intake goal */
    WATER_INTAKE_GOAL_LAST(HealthType.WATER_INTAKE_GOAL, "mL", false),
    ;

    /** Factory of **Aggregation** */
    public companion object {
        /**
         * Finds an aggregation by its name.
         *
         * @param name **String** e.g. `STEPS_TOTAL`
         * @return **Aggregation** the aggregation
         * @throws SamsungHealthException.InvalidType when no aggregation has this name
         */
        public fun make(from: String): Aggregation =
            entries.firstOrNull { it.name == from }
                ?: throw SamsungHealthException.InvalidType("Invalid Aggregation: $from")
    }
}
