package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **IrregularHeartRhythmStatus** the status of irregular heart rhythm detection.
 *
 * Mirrors the Samsung Health Data SDK
 * `DataType.IrregularHeartRhythmNotificationType.IrregularHeartRhythmStatus` entries by name.
 */
@Serializable
public enum class IrregularHeartRhythmStatus {
    UNDEFINED,
    DETECTED,
}
