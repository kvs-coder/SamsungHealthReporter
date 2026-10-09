package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.decorator.encoded
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

/**
 * **SkinTemperature** skin temperature over an interval, in degrees Celsius.
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
@SerialName("skin_temperature")
public data class SkinTemperature(
    override val startTimestamp: Long,
    override val endTimestamp: Long,
    val harmonized: Harmonized,
    override val uid: String? = null,
    override val clientDataId: String? = null,
    override val zoneOffsetSeconds: Int? = null,
    override val dataSource: DataSource? = null,
) : Sample {
    init {
        validateInterval()
    }

    override val healthType: HealthType get() = HealthType.SKIN_TEMPERATURE

    override val json: String get() = encoded

    /**
     * **Harmonized** skin temperature values, in degrees Celsius (10–50).
     *
     * @param skinTemperature **Float** the skin temperature
     * @param min **Float?** the lowest skin temperature in the interval
     * @param max **Float?** the highest skin temperature in the interval
     * @param series **List<Series>** continuous measurements within the interval
     */
    @Serializable
    public data class Harmonized(
        val skinTemperature: Float,
        val min: Float? = null,
        val max: Float? = null,
        val series: List<Series> = emptyList(),
    )

    /**
     * **Series** one continuous skin temperature measurement, in degrees Celsius.
     *
     * @param skinTemperature **Float** the skin temperature
     * @param min **Float** the lowest skin temperature
     * @param max **Float** the highest skin temperature
     * @param startTimestamp **Long** epoch milliseconds
     * @param endTimestamp **Long** epoch milliseconds
     */
    @Serializable
    public data class Series(
        val skinTemperature: Float,
        val min: Float,
        val max: Float,
        val startTimestamp: Long,
        val endTimestamp: Long,
    ) {
        init {
            startTimestamp.validateInterval(endTimestamp)
        }
    }

    /** Factory of **SkinTemperature** */
    public companion object : Payload.Factory<SkinTemperature>({ SkinTemperature.serializer() }) {
        internal fun from(point: HealthDataPoint): SkinTemperature =
            SkinTemperature(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.requiredEndTimestamp,
                harmonized =
                    Harmonized(
                        skinTemperature = point.require(DataType.SkinTemperatureType.SKIN_TEMPERATURE),
                        min = point.getValue(DataType.SkinTemperatureType.MIN_SKIN_TEMPERATURE),
                        max = point.getValue(DataType.SkinTemperatureType.MAX_SKIN_TEMPERATURE),
                        series =
                            point.getValue(DataType.SkinTemperatureType.SERIES_DATA).orEmpty().map {
                                Series(
                                    skinTemperature = it.skinTemperature,
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
