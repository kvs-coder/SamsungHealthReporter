package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.decorator.encoded
import com.kvs.samsunghealthreporter.decorator.originalBuilder
import com.kvs.samsunghealthreporter.decorator.require
import com.kvs.samsunghealthreporter.decorator.requiredEndTimestamp
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
import java.time.Instant
import com.samsung.android.sdk.health.data.data.entries.OxygenSaturation as OriginalSeries

/**
 * **BloodOxygen** blood oxygen saturation over an interval, in percent.
 *
 * @param startTimestamp **Long** epoch milliseconds
 * @param endTimestamp **Long** epoch milliseconds, not before [startTimestamp]
 * @param harmonized **Harmonized** the measured values
 * @param uid **String?** Samsung Health id; null until inserted
 * @param clientDataId **String?** your own id, 1–36 characters
 * @param zoneOffsetSeconds **Int?** zone offset in seconds
 * @param dataSource **DataSource?** writing app and device; set by Samsung Health
 * @throws com.kvs.samsunghealthreporter.SamsungHealthException.InvalidValue when [endTimestamp] is before [startTimestamp]
 */
@Serializable
@SerialName("blood_oxygen")
public data class BloodOxygen(
    override val startTimestamp: Long,
    override val endTimestamp: Long,
    val harmonized: Harmonized,
    override val uid: String? = null,
    override val clientDataId: String? = null,
    override val zoneOffsetSeconds: Int? = null,
    override val dataSource: DataSource? = null,
) : WritableSample {
    init {
        validateInterval()
    }

    override val healthType: HealthType get() = HealthType.BLOOD_OXYGEN

    override val json: String get() = encoded

    /**
     * **Harmonized** blood oxygen saturation values, in percent (0–100).
     *
     * @param oxygenSaturation **Float** the oxygen saturation
     * @param min **Float?** the lowest oxygen saturation in the interval
     * @param max **Float?** the highest oxygen saturation in the interval
     * @param series **List<Series>** continuous measurements within the interval
     */
    @Serializable
    public data class Harmonized(
        val oxygenSaturation: Float,
        val min: Float? = null,
        val max: Float? = null,
        val series: List<Series> = emptyList(),
    )

    /**
     * **Series** one continuous blood oxygen measurement, in percent.
     *
     * @param oxygenSaturation **Float** the oxygen saturation
     * @param min **Float** the lowest oxygen saturation
     * @param max **Float** the highest oxygen saturation
     * @param startTimestamp **Long** epoch milliseconds
     * @param endTimestamp **Long** epoch milliseconds
     */
    @Serializable
    public data class Series(
        val oxygenSaturation: Float,
        val min: Float,
        val max: Float,
        val startTimestamp: Long,
        val endTimestamp: Long,
    ) {
        init {
            startTimestamp.validateInterval(endTimestamp)
        }
    }

    // region Original
    internal fun asOriginal(): HealthDataPoint =
        originalBuilder()
            .addFieldData(DataType.BloodOxygenType.OXYGEN_SATURATION, harmonized.oxygenSaturation)
            .addFieldData(DataType.BloodOxygenType.MIN_OXYGEN_SATURATION, harmonized.min)
            .addFieldData(DataType.BloodOxygenType.MAX_OXYGEN_SATURATION, harmonized.max)
            .addFieldData(
                DataType.BloodOxygenType.SERIES_DATA,
                harmonized.series
                    .map {
                        OriginalSeries.of(
                            it.oxygenSaturation,
                            it.min,
                            it.max,
                            Instant.ofEpochMilli(it.startTimestamp),
                            Instant.ofEpochMilli(it.endTimestamp),
                        )
                    }.ifEmpty { null },
            ).build()
    // endregion

    /** Factory of **BloodOxygen** */
    public companion object : Payload.Factory<BloodOxygen>({ BloodOxygen.serializer() }) {
        internal fun from(point: HealthDataPoint): BloodOxygen =
            BloodOxygen(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.requiredEndTimestamp,
                harmonized =
                    Harmonized(
                        oxygenSaturation = point.require(DataType.BloodOxygenType.OXYGEN_SATURATION),
                        min = point.getValue(DataType.BloodOxygenType.MIN_OXYGEN_SATURATION),
                        max = point.getValue(DataType.BloodOxygenType.MAX_OXYGEN_SATURATION),
                        series =
                            point.getValue(DataType.BloodOxygenType.SERIES_DATA).orEmpty().map {
                                Series(
                                    oxygenSaturation = it.oxygenSaturation,
                                    min = it.min,
                                    max = it.max,
                                    startTimestamp = it.startTime.toEpochMilli(),
                                    endTimestamp = it.endTime.toEpochMilli(),
                                )
                            },
                    ),
                uid = point.uid,
                clientDataId = point.clientDataId,
                zoneOffsetSeconds = point.zoneOffsetSeconds,
                dataSource = point.source,
            )
    }
}
