package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **SleepStageType** a sleep stage.
 *
 * Mirrors the Samsung Health Data SDK `DataType.SleepType.StageType` entries by name.
 */
@Serializable
public enum class SleepStageType {
    UNDEFINED,
    AWAKE,
    LIGHT,
    DEEP,
    REM,
}
