package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **SleepApneaSign** the detected sign of sleep apnea.
 *
 * Mirrors the Samsung Health Data SDK `DataType.SleepApneaType.DetectedSign` entries by name.
 */
@Serializable
public enum class SleepApneaSign {
    UNDEFINED,
    DETECTED,
    NOT_DETECTED,
}
