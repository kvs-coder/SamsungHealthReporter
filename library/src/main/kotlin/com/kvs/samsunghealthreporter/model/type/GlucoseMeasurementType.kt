package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **GlucoseMeasurementType** the type of blood used for a blood glucose measurement.
 *
 * Mirrors the Samsung Health Data SDK `DataType.BloodGlucoseType.MeasurementType` entries by name.
 */
@Serializable
public enum class GlucoseMeasurementType {
    UNDEFINED,
    WHOLE_BLOOD,
    PLASMA,
    SERUM,
}
