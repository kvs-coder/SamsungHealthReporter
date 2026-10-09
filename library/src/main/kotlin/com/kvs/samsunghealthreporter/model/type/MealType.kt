package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **MealType** the meal a nutrition record belongs to.
 *
 * Mirrors the Samsung Health Data SDK `DataType.NutritionType.MealType` entries by name.
 */
@Serializable
public enum class MealType {
    UNDEFINED,
    BREAKFAST,
    LUNCH,
    DINNER,
    MORNING_SNACK,
    AFTERNOON_SNACK,
    EVENING_SNACK,
}
