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
import com.samsung.android.sdk.health.data.data.entries.HeartRate as OriginalSeries

/**
 * **HeartRate** heart rate over an interval, in beats per minute.
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
@SerialName("heart_rate")
public data class HeartRate(
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

    override val healthType: HealthType get() = HealthType.HEART_RATE

    override val json: String get() = encoded

    /**
     * **Harmonized** heart rate values, in beats per minute (0–300).
     *
     * @param heartRate **Float** the heart rate
     * @param min **Float?** the lowest heart rate in the interval
     * @param max **Float?** the highest heart rate in the interval
     * @param series **List<Series>** continuous measurements within the interval
     */
    @Serializable
    public data class Harmonized(
        val heartRate: Float,
        val min: Float? = null,
        val max: Float? = null,
        val series: List<Series> = emptyList(),
    )

    /**
     * **Series** one continuous heart rate measurement, in beats per minute.
     *
     * @param heartRate **Float** the heart rate
     * @param min **Float** the lowest heart rate
     * @param max **Float** the highest heart rate
     * @param startTimestamp **Long** epoch milliseconds
     * @param endTimestamp **Long** epoch milliseconds
     */
    @Serializable
    public data class Series(
        val heartRate: Float,
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
            .addFieldData(DataType.HeartRateType.HEART_RATE, harmonized.heartRate)
            .addFieldData(DataType.HeartRateType.MIN_HEART_RATE, harmonized.min)
            .addFieldData(DataType.HeartRateType.MAX_HEART_RATE, harmonized.max)
            .addFieldData(
                DataType.HeartRateType.SERIES_DATA,
                harmonized.series
                    .map {
                        OriginalSeries.of(
                            it.heartRate,
                            it.min,
                            it.max,
                            Instant.ofEpochMilli(it.startTimestamp),
                            Instant.ofEpochMilli(it.endTimestamp),
                        )
                    }.ifEmpty { null },
            ).build()
    // endregion

    /** Factory of **HeartRate** */
    public companion object : Payload.Factory<HeartRate>({ HeartRate.serializer() }) {
        internal fun from(point: HealthDataPoint): HeartRate =
            HeartRate(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.requiredEndTimestamp,
                harmonized =
                    Harmonized(
                        heartRate = point.require(DataType.HeartRateType.HEART_RATE),
                        min = point.getValue(DataType.HeartRateType.MIN_HEART_RATE),
                        max = point.getValue(DataType.HeartRateType.MAX_HEART_RATE),
                        series =
                            point.getValue(DataType.HeartRateType.SERIES_DATA).orEmpty().map {
                                Series(
                                    heartRate = it.heartRate,
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
