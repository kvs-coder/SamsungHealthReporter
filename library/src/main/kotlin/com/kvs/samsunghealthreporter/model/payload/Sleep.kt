package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.decorator.encoded
import com.kvs.samsunghealthreporter.decorator.mapped
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
import com.kvs.samsunghealthreporter.model.type.SleepStageType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.entries.SleepSession
import com.samsung.android.sdk.health.data.request.DataType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant

/**
 * **Sleep** one sleep record made of one or more sessions.
 *
 * Samsung Health requires [startTimestamp] and [endTimestamp] to match the first session's start and the last
 * session's end, and sessions not to overlap.
 *
 * @param startTimestamp **Long** epoch milliseconds
 * @param endTimestamp **Long** epoch milliseconds, not before [startTimestamp]
 * @param harmonized **Harmonized** the sleep details
 * @param uid **String?** Samsung Health id; null until inserted
 * @param clientDataId **String?** your own id, 1–36 characters
 * @param zoneOffsetSeconds **Int?** zone offset in seconds
 * @param dataSource **DataSource?** writing app and device; set by Samsung Health
 * @throws com.kvs.samsunghealthreporter.SamsungHealthException.InvalidValue when the interval is invalid
 */
@Serializable
@SerialName("sleep")
public data class Sleep(
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

    override val healthType: HealthType get() = HealthType.SLEEP

    override val json: String get() = encoded

    /**
     * **Harmonized** the values of Sleep.
     *
     * @param durationMillis **Long** sleep duration in milliseconds (up to 1440 minutes)
     * @param sessions **List<Session>** the sleep sessions, at least one
     * @param sleepScore **Int?** overall sleep quality (0–100); computed by Samsung Health, ignored on writes
     */
    @Serializable
    public data class Harmonized(
        val durationMillis: Long,
        val sessions: List<Session>,
        val sleepScore: Int? = null,
    )

    /**
     * **Session** one period from bedtime to wake-up.
     *
     * @param startTimestamp **Long** bedtime, epoch milliseconds
     * @param endTimestamp **Long** wake-up time, epoch milliseconds
     * @param durationMillis **Long** duration in milliseconds
     * @param stages **List<Stage>** the recorded sleep stages
     */
    @Serializable
    public data class Session(
        val startTimestamp: Long,
        val endTimestamp: Long,
        val durationMillis: Long,
        val stages: List<Stage> = emptyList(),
    ) {
        init {
            startTimestamp.validateInterval(endTimestamp)
        }
    }

    /**
     * **Stage** one sleep stage.
     *
     * @param startTimestamp **Long** epoch milliseconds
     * @param endTimestamp **Long** epoch milliseconds
     * @param stage **SleepStageType** the stage
     */
    @Serializable
    public data class Stage(
        val startTimestamp: Long,
        val endTimestamp: Long,
        val stage: SleepStageType,
    ) {
        init {
            startTimestamp.validateInterval(endTimestamp)
        }
    }

    // region Original
    internal fun asOriginal(): HealthDataPoint =
        originalBuilder()
            .addFieldData(DataType.SleepType.DURATION, Duration.ofMillis(harmonized.durationMillis))
            .addFieldData(
                DataType.SleepType.SESSIONS,
                harmonized.sessions.map { session ->
                    SleepSession.of(
                        Instant.ofEpochMilli(session.startTimestamp),
                        Instant.ofEpochMilli(session.endTimestamp),
                        Duration.ofMillis(session.durationMillis),
                        session.stages
                            .map {
                                SleepSession.SleepStage.of(
                                    Instant.ofEpochMilli(it.startTimestamp),
                                    Instant.ofEpochMilli(it.endTimestamp),
                                    it.stage.mapped(),
                                )
                            }.ifEmpty { null },
                    )
                },
            ).build()
    // endregion

    /** Factory of **Sleep** */
    public companion object : Payload.Factory<Sleep>({ Sleep.serializer() }) {
        internal fun from(point: HealthDataPoint): Sleep =
            Sleep(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.requiredEndTimestamp,
                harmonized =
                    Harmonized(
                        durationMillis = point.require(DataType.SleepType.DURATION).toMillis(),
                        sessions =
                            point.require(DataType.SleepType.SESSIONS).map { session ->
                                Session(
                                    startTimestamp = session.startTime.toEpochMilli(),
                                    endTimestamp = session.endTime.toEpochMilli(),
                                    durationMillis = session.duration.toMillis(),
                                    stages =
                                        session.stages.orEmpty().map {
                                            Stage(
                                                startTimestamp = it.startTime.toEpochMilli(),
                                                endTimestamp = it.endTime.toEpochMilli(),
                                                stage = it.stage.mapped(),
                                            )
                                        },
                                )
                            },
                        sleepScore = point.getValue(DataType.SleepType.SLEEP_SCORE),
                    ),
                uid = point.uid,
                clientDataId = point.clientDataId,
                zoneOffsetSeconds = point.zoneOffsetSeconds,
                dataSource = point.source,
            )
    }
}
