package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **GlucoseSampleSource** where in the body a blood glucose sample was taken.
 *
 * Mirrors the Samsung Health Data SDK `DataType.BloodGlucoseType.SampleSourceType` entries by name.
 */
@Serializable
public enum class GlucoseSampleSource {
    UNDEFINED,
    VENOUS,
    CAPILLARY,
}
