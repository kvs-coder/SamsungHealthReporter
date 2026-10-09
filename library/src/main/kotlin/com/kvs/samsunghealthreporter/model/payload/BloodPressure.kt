package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.decorator.encoded
import com.kvs.samsunghealthreporter.decorator.endTimestamp
import com.kvs.samsunghealthreporter.decorator.originalBuilder
import com.kvs.samsunghealthreporter.decorator.require
import com.kvs.samsunghealthreporter.decorator.source
import com.kvs.samsunghealthreporter.decorator.startTimestamp
import com.kvs.samsunghealthreporter.decorator.validateInterval
import com.kvs.samsunghealthreporter.decorator.zoneOffsetSeconds
import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.Payload
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * **BloodPressure** a blood pressure measurement, in mmHg.
 *
 * @param startTimestamp **Long** epoch milliseconds
 * @param endTimestamp **Long?** epoch milliseconds; null for an instantaneous measurement
 * @param harmonized **Harmonized** the measured values
 * @param uid **String?** Samsung Health id; null until inserted
 * @param clientDataId **String?** your own id, 1–36 characters
 * @param zoneOffsetSeconds **Int?** zone offset in seconds
 * @param dataSource **DataSource?** writing app and device; set by Samsung Health
 * @throws com.kvs.samsunghealthreporter.SamsungHealthException.InvalidValue when [endTimestamp] is before [startTimestamp]
 */
@Serializable
@SerialName("blood_pressure")
public data class BloodPressure(
    override val startTimestamp: Long,
    override val endTimestamp: Long? = null,
    val harmonized: Harmonized,
    override val uid: String? = null,
    override val clientDataId: String? = null,
    override val zoneOffsetSeconds: Int? = null,
    override val dataSource: DataSource? = null,
) : WritableSample {
    init {
        validateInterval()
    }

    override val healthType: HealthType get() = HealthType.BLOOD_PRESSURE

    override val json: String get() = encoded

    /**
     * **Harmonized** the values of BloodPressure.
     *
     * @param systolic **Float** systolic pressure in mmHg (0–300)
     * @param diastolic **Float** diastolic pressure in mmHg (0–250)
     * @param mean **Float** mean pressure in mmHg (0–300)
     * @param pulseRate **Int?** pulse rate in beats per minute (15–300)
     * @param medicationTaken **Boolean?** medication was taken before the measurement
     */
    @Serializable
    public data class Harmonized(
        val systolic: Float,
        val diastolic: Float,
        val mean: Float,
        val pulseRate: Int? = null,
        val medicationTaken: Boolean? = null,
    )

    // region Original
    internal fun asOriginal(): HealthDataPoint =
        originalBuilder()
            .addFieldData(DataType.BloodPressureType.SYSTOLIC, harmonized.systolic)
            .addFieldData(DataType.BloodPressureType.DIASTOLIC, harmonized.diastolic)
            .addFieldData(DataType.BloodPressureType.MEAN, harmonized.mean)
            .addFieldData(DataType.BloodPressureType.PULSE_RATE, harmonized.pulseRate)
            .addFieldData(DataType.BloodPressureType.MEDICATION_TAKEN, harmonized.medicationTaken)
            .build()
    // endregion

    /** Factory of **BloodPressure** */
    public companion object : Payload.Factory<BloodPressure>({ BloodPressure.serializer() }) {
        internal fun from(point: HealthDataPoint): BloodPressure =
            BloodPressure(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.endTimestamp,
                harmonized =
                    Harmonized(
                        systolic = point.require(DataType.BloodPressureType.SYSTOLIC),
                        diastolic = point.require(DataType.BloodPressureType.DIASTOLIC),
                        mean = point.require(DataType.BloodPressureType.MEAN),
                        pulseRate = point.getValue(DataType.BloodPressureType.PULSE_RATE),
                        medicationTaken = point.getValue(DataType.BloodPressureType.MEDICATION_TAKEN),
                    ),
                uid = point.uid,
                clientDataId = point.clientDataId,
                zoneOffsetSeconds = point.zoneOffsetSeconds,
                dataSource = point.source,
            )
    }
}
