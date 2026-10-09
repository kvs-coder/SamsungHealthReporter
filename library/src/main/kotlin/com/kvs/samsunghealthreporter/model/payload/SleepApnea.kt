package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.decorator.encoded
import com.kvs.samsunghealthreporter.decorator.endTimestamp
import com.kvs.samsunghealthreporter.decorator.mapped
import com.kvs.samsunghealthreporter.decorator.require
import com.kvs.samsunghealthreporter.decorator.source
import com.kvs.samsunghealthreporter.decorator.startTimestamp
import com.kvs.samsunghealthreporter.decorator.validateInterval
import com.kvs.samsunghealthreporter.decorator.zoneOffsetSeconds
import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.Payload
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.SleepApneaSign
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * **SleepApnea** a sleep apnea check result.
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
@SerialName("sleep_apnea")
public data class SleepApnea(
    override val startTimestamp: Long,
    override val endTimestamp: Long? = null,
    val harmonized: Harmonized,
    override val uid: String? = null,
    override val clientDataId: String? = null,
    override val zoneOffsetSeconds: Int? = null,
    override val dataSource: DataSource? = null,
) : Sample {
    init {
        validateInterval()
    }

    override val healthType: HealthType get() = HealthType.SLEEP_APNEA

    override val json: String get() = encoded

    /**
     * **Harmonized** the values of SleepApnea.
     *
     * @param detectedSign **SleepApneaSign** the detected sign
     */
    @Serializable
    public data class Harmonized(
        val detectedSign: SleepApneaSign,
    )

    /** Factory of **SleepApnea** */
    public companion object : Payload.Factory<SleepApnea>({ SleepApnea.serializer() }) {
        internal fun from(point: HealthDataPoint): SleepApnea =
            SleepApnea(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.endTimestamp,
                harmonized =
                    Harmonized(
                        detectedSign = point.require(DataType.SleepApneaType.DETECTED_SIGN).mapped<SleepApneaSign>(),
                    ),
                uid = point.uid,
                clientDataId = point.clientDataId,
                zoneOffsetSeconds = point.zoneOffsetSeconds,
                dataSource = point.source,
            )
    }
}
