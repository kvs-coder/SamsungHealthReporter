package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **ExerciseCountType** what the count of a countable exercise measures.
 *
 * Mirrors the Samsung Health Data SDK `DataType.ExerciseType.CountType` entries by name.
 */
@Serializable
public enum class ExerciseCountType {
    UNDEFINED,
    STRIDE,
    STROKE,
    SWING,
    REPETITION,
}
