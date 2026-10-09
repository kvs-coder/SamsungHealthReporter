package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **MealStatus** the meal status when a blood glucose level was measured.
 *
 * Mirrors the Samsung Health Data SDK `DataType.BloodGlucoseType.MealStatus` entries by name.
 */
@Serializable
public enum class MealStatus {
    UNDEFINED,
    FASTING,
    AFTER_MEAL,
    BEFORE_BREAKFAST,
    AFTER_BREAKFAST,
    BEFORE_LUNCH,
    AFTER_LUNCH,
    BEFORE_DINNER,
    AFTER_DINNER,
    AFTER_BED_TIME,
    AFTER_SNACK,
    BEFORE_MEAL,
    GENERAL,
    BEFORE_SLEEP,
}
