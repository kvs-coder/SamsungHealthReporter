package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.decorator.encoded
import com.kvs.samsunghealthreporter.decorator.endTimestamp
import com.kvs.samsunghealthreporter.decorator.mapped
import com.kvs.samsunghealthreporter.decorator.originalBuilder
import com.kvs.samsunghealthreporter.decorator.require
import com.kvs.samsunghealthreporter.decorator.source
import com.kvs.samsunghealthreporter.decorator.startTimestamp
import com.kvs.samsunghealthreporter.decorator.validateInterval
import com.kvs.samsunghealthreporter.decorator.zoneOffsetSeconds
import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.Payload
import com.kvs.samsunghealthreporter.model.type.GlucoseMeasurementType
import com.kvs.samsunghealthreporter.model.type.GlucoseSampleSource
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.MealStatus
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import com.samsung.android.sdk.health.data.data.entries.BloodGlucose as OriginalSeries

/**
 * **BloodGlucose** a blood glucose measurement, in mmol/L.
 *
 * @param startTimestamp **Long** epoch milliseconds
 * @param endTimestamp **Long?** epoch milliseconds; set for continuous measurements
 * @param harmonized **Harmonized** the measured values
 * @param uid **String?** Samsung Health id; null until inserted
 * @param clientDataId **String?** your own id, 1–36 characters
 * @param zoneOffsetSeconds **Int?** zone offset in seconds
 * @param dataSource **DataSource?** writing app and device; set by Samsung Health
 * @throws com.kvs.samsunghealthreporter.SamsungHealthException.InvalidValue when the interval is invalid
 */
@Serializable
@SerialName("blood_glucose")
public data class BloodGlucose(
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

    override val healthType: HealthType get() = HealthType.BLOOD_GLUCOSE

    override val json: String get() = encoded

    /**
     * **Harmonized** the values of BloodGlucose.
     *
     * @param glucoseLevel **Float** blood glucose in mmol/L (1–40)
     * @param mealStatus **MealStatus** meal status at the time of measurement
     * @param measurementType **GlucoseMeasurementType** the type of blood measured
     * @param sampleSource **GlucoseSampleSource?** where the sample was taken
     * @param insulinInjected **Float?** insulin injected before the measurement (0–100)
     * @param mealTimestamp **Long?** meal time, epoch milliseconds
     * @param medicationTaken **Boolean?** medication was taken before the measurement
     * @param series **List<Series>** continuous glucose levels
     */
    @Serializable
    public data class Harmonized(
        val glucoseLevel: Float,
        val mealStatus: MealStatus,
        val measurementType: GlucoseMeasurementType,
        val sampleSource: GlucoseSampleSource? = null,
        val insulinInjected: Float? = null,
        val mealTimestamp: Long? = null,
        val medicationTaken: Boolean? = null,
        val series: List<Series> = emptyList(),
    )

    /**
     * **Series** one continuous glucose level.
     *
     * @param glucose **Float** blood glucose in mmol/L
     * @param timestamp **Long** measurement time, epoch milliseconds
     */
    @Serializable
    public data class Series(
        val glucose: Float,
        val timestamp: Long,
    )

    // region Original
    internal fun asOriginal(): HealthDataPoint =
        originalBuilder()
            .addFieldData(DataType.BloodGlucoseType.GLUCOSE_LEVEL, harmonized.glucoseLevel)
            .addFieldData(
                DataType.BloodGlucoseType.MEAL_STATUS,
                harmonized.mealStatus.mapped<DataType.BloodGlucoseType.MealStatus>(),
            ).addFieldData(
                DataType.BloodGlucoseType.MEASUREMENT_TYPE,
                harmonized.measurementType.mapped<DataType.BloodGlucoseType.MeasurementType>(),
            ).addFieldData(
                DataType.BloodGlucoseType.SAMPLE_SOURCE_TYPE,
                harmonized.sampleSource?.mapped<DataType.BloodGlucoseType.SampleSourceType>(),
            ).addFieldData(DataType.BloodGlucoseType.INSULIN_INJECTED, harmonized.insulinInjected)
            .addFieldData(DataType.BloodGlucoseType.MEAL_TIME, harmonized.mealTimestamp?.let(Instant::ofEpochMilli))
            .addFieldData(DataType.BloodGlucoseType.MEDICATION_TAKEN, harmonized.medicationTaken)
            .addFieldData(
                DataType.BloodGlucoseType.SERIES_DATA,
                harmonized.series
                    .map { OriginalSeries.of(it.glucose, Instant.ofEpochMilli(it.timestamp)) }
                    .ifEmpty { null },
            ).build()
    // endregion

    /** Factory of **BloodGlucose** */
    public companion object : Payload.Factory<BloodGlucose>({ BloodGlucose.serializer() }) {
        internal fun from(point: HealthDataPoint): BloodGlucose =
            BloodGlucose(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.endTimestamp,
                harmonized =
                    Harmonized(
                        glucoseLevel = point.require(DataType.BloodGlucoseType.GLUCOSE_LEVEL),
                        mealStatus = point.require(DataType.BloodGlucoseType.MEAL_STATUS).mapped(),
                        measurementType = point.require(DataType.BloodGlucoseType.MEASUREMENT_TYPE).mapped(),
                        sampleSource =
                            point
                                .getValue(
                                    DataType.BloodGlucoseType.SAMPLE_SOURCE_TYPE,
                                )?.mapped<GlucoseSampleSource>(),
                        insulinInjected = point.getValue(DataType.BloodGlucoseType.INSULIN_INJECTED),
                        mealTimestamp = point.getValue(DataType.BloodGlucoseType.MEAL_TIME)?.toEpochMilli(),
                        medicationTaken = point.getValue(DataType.BloodGlucoseType.MEDICATION_TAKEN),
                        series =
                            point.getValue(DataType.BloodGlucoseType.SERIES_DATA).orEmpty().map {
                                Series(glucose = it.glucose, timestamp = it.timestamp.toEpochMilli())
                            },
                    ),
                uid = point.uid,
                clientDataId = point.clientDataId,
                zoneOffsetSeconds = point.zoneOffsetSeconds,
                dataSource = point.source,
            )
    }
}
