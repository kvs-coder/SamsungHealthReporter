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
import com.kvs.samsunghealthreporter.model.type.ExerciseCountType
import com.kvs.samsunghealthreporter.model.type.ExerciseType
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.entries.ExerciseLocation
import com.samsung.android.sdk.health.data.data.entries.ExerciseLog
import com.samsung.android.sdk.health.data.data.entries.ExerciseSession
import com.samsung.android.sdk.health.data.request.DataType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant

/**
 * **Exercise** one workout made of one or more sessions.
 *
 * Samsung Health requires [startTimestamp] and [endTimestamp] to match the session times, and sessions not to
 * overlap. Reading [Session.route] needs the [HealthType.EXERCISE_LOCATION] read permission.
 *
 * @param startTimestamp **Long** epoch milliseconds
 * @param endTimestamp **Long** epoch milliseconds, not before [startTimestamp]
 * @param harmonized **Harmonized** the workout details
 * @param uid **String?** Samsung Health id; null until inserted
 * @param clientDataId **String?** your own id, 1–36 characters
 * @param zoneOffsetSeconds **Int?** zone offset in seconds
 * @param dataSource **DataSource?** writing app and device; set by Samsung Health
 * @throws com.kvs.samsunghealthreporter.SamsungHealthException.InvalidValue when the interval is invalid
 */
@Serializable
@SerialName("exercise")
public data class Exercise(
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

    override val healthType: HealthType get() = HealthType.EXERCISE

    override val json: String get() = encoded

    /**
     * **Harmonized** the values of Exercise.
     *
     * @param exerciseType **ExerciseType** the representative exercise type
     * @param sessions **List<Session>** the exercise sessions, at least one
     * @param customTitle **String?** custom title when [exerciseType] is OTHER
     */
    @Serializable
    public data class Harmonized(
        val exerciseType: ExerciseType,
        val sessions: List<Session>,
        val customTitle: String? = null,
    )

    /**
     * **Session** one exercise activity.
     *
     * @param exerciseType **ExerciseType** the activity
     * @param startTimestamp **Long** epoch milliseconds
     * @param endTimestamp **Long** epoch milliseconds
     * @param durationMillis **Long** duration in milliseconds (up to 2880 minutes)
     * @param calories **Float** burned calories in kilocalories
     * @param customTitle **String?** custom title when exerciseType is OTHER (1–255 characters)
     * @param comment **String?** comment (0–255 characters)
     * @param distance **Float?** distance in meters
     * @param count **Int?** count of a countable exercise, see countType
     * @param countType **ExerciseCountType?** what count measures
     * @param altitudeGain **Float?** altitude gain in meters
     * @param altitudeLoss **Float?** altitude loss in meters
     * @param maxAltitude **Float?** highest altitude in meters
     * @param minAltitude **Float?** lowest altitude in meters
     * @param inclineDistance **Float?** uphill distance in meters
     * @param declineDistance **Float?** downhill distance in meters
     * @param maxCadence **Float?** highest cadence per minute
     * @param meanCadence **Float?** mean cadence per minute
     * @param maxCalorieBurnRate **Float?** highest calorie burn rate in kilocalories per hour
     * @param meanCalorieBurnRate **Float?** mean calorie burn rate in kilocalories per hour
     * @param maxHeartRate **Float?** highest heart rate in beats per minute
     * @param meanHeartRate **Float?** mean heart rate in beats per minute
     * @param minHeartRate **Float?** lowest heart rate in beats per minute
     * @param maxPower **Float?** highest power in watts
     * @param meanPower **Float?** mean power in watts
     * @param maxRpm **Float?** highest revolutions per minute
     * @param meanRpm **Float?** mean revolutions per minute
     * @param maxSpeed **Float?** highest speed in meters per second
     * @param meanSpeed **Float?** mean speed in meters per second
     * @param vo2Max **Float?** VO2 max in mL/kg/min
     * @param log **List<Log>** periodic measurements during the session
     * @param route **List<Location>** the route; needs the EXERCISE_LOCATION permission
     */
    @Serializable
    public data class Session(
        val exerciseType: ExerciseType,
        val startTimestamp: Long,
        val endTimestamp: Long,
        val durationMillis: Long,
        val calories: Float,
        val customTitle: String? = null,
        val comment: String? = null,
        val distance: Float? = null,
        val count: Int? = null,
        val countType: ExerciseCountType? = null,
        val altitudeGain: Float? = null,
        val altitudeLoss: Float? = null,
        val maxAltitude: Float? = null,
        val minAltitude: Float? = null,
        val inclineDistance: Float? = null,
        val declineDistance: Float? = null,
        val maxCadence: Float? = null,
        val meanCadence: Float? = null,
        val maxCalorieBurnRate: Float? = null,
        val meanCalorieBurnRate: Float? = null,
        val maxHeartRate: Float? = null,
        val meanHeartRate: Float? = null,
        val minHeartRate: Float? = null,
        val maxPower: Float? = null,
        val meanPower: Float? = null,
        val maxRpm: Float? = null,
        val meanRpm: Float? = null,
        val maxSpeed: Float? = null,
        val meanSpeed: Float? = null,
        val vo2Max: Float? = null,
        val log: List<Log> = emptyList(),
        val route: List<Location> = emptyList(),
    ) {
        init {
            startTimestamp.validateInterval(endTimestamp)
        }

        internal val asOriginal: ExerciseSession
            get() =
                ExerciseSession
                    .builder()
                    .setExerciseType(exerciseType.mapped())
                    .setStartTime(Instant.ofEpochMilli(startTimestamp))
                    .setEndTime(Instant.ofEpochMilli(endTimestamp))
                    .setDuration(Duration.ofMillis(durationMillis))
                    .setCalories(calories)
                    .setCustomTitle(customTitle)
                    .setComment(comment)
                    .setDistance(distance)
                    .setCount(count)
                    .setCountType(countType?.mapped<DataType.ExerciseType.CountType>())
                    .setAltitudeGain(altitudeGain)
                    .setAltitudeLoss(altitudeLoss)
                    .setMaxAltitude(maxAltitude)
                    .setMinAltitude(minAltitude)
                    .setInclineDistance(inclineDistance)
                    .setDeclineDistance(declineDistance)
                    .setMaxCadence(maxCadence)
                    .setMeanCadence(meanCadence)
                    .setMaxCalorieBurnRate(maxCalorieBurnRate)
                    .setMeanCalorieBurnRate(meanCalorieBurnRate)
                    .setMaxHeartRate(maxHeartRate)
                    .setMeanHeartRate(meanHeartRate)
                    .setMinHeartRate(minHeartRate)
                    .setMaxPower(maxPower)
                    .setMeanPower(meanPower)
                    .setMaxRPM(maxRpm)
                    .setMeanRPM(meanRpm)
                    .setMaxSpeed(maxSpeed)
                    .setMeanSpeed(meanSpeed)
                    .setVo2Max(vo2Max)
                    .setLog(log.map { it.asOriginal })
                    .setRoute(route.map { it.asOriginal })
                    .build()
    }

    /**
     * **Log** one periodic measurement during a session.
     *
     * @param timestamp **Long** epoch milliseconds, within the session
     * @param heartRate **Float?** heart rate in beats per minute
     * @param cadence **Float?** cadence per minute
     * @param count **Int?** count
     * @param power **Float?** power in watts
     * @param speed **Float?** speed in meters per second
     */
    @Serializable
    public data class Log(
        val timestamp: Long,
        val heartRate: Float? = null,
        val cadence: Float? = null,
        val count: Int? = null,
        val power: Float? = null,
        val speed: Float? = null,
    ) {
        internal val asOriginal: ExerciseLog
            get() = ExerciseLog.of(Instant.ofEpochMilli(timestamp), heartRate, cadence, count, power, speed)
    }

    /**
     * **Location** one route point during a session.
     *
     * @param timestamp **Long** epoch milliseconds, within the session
     * @param latitude **Float** latitude in degrees
     * @param longitude **Float** longitude in degrees
     * @param altitude **Float?** altitude in meters
     * @param accuracy **Float?** accuracy in meters
     */
    @Serializable
    public data class Location(
        val timestamp: Long,
        val latitude: Float,
        val longitude: Float,
        val altitude: Float? = null,
        val accuracy: Float? = null,
    ) {
        internal val asOriginal: ExerciseLocation
            get() = ExerciseLocation.of(Instant.ofEpochMilli(timestamp), longitude, latitude, altitude, accuracy)
    }

    // region Original
    internal fun asOriginal(): HealthDataPoint =
        originalBuilder()
            .addFieldData(
                DataType.ExerciseType.EXERCISE_TYPE,
                harmonized.exerciseType.mapped<DataType.ExerciseType.PredefinedExerciseType>(),
            ).addFieldData(DataType.ExerciseType.CUSTOM_TITLE, harmonized.customTitle)
            .addFieldData(DataType.ExerciseType.SESSIONS, harmonized.sessions.map { it.asOriginal })
            .build()
    // endregion

    /** Factory of **Exercise** */
    public companion object : Payload.Factory<Exercise>({ Exercise.serializer() }) {
        internal fun from(point: HealthDataPoint): Exercise =
            Exercise(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.requiredEndTimestamp,
                harmonized =
                    Harmonized(
                        exerciseType = point.require(DataType.ExerciseType.EXERCISE_TYPE).mapped(),
                        sessions = point.require(DataType.ExerciseType.SESSIONS).map { it.harmonized },
                        customTitle = point.getValue(DataType.ExerciseType.CUSTOM_TITLE),
                    ),
                uid = point.uid,
                clientDataId = point.clientDataId,
                zoneOffsetSeconds = point.zoneOffsetSeconds,
                dataSource = point.source,
            )

        private val ExerciseSession.harmonized: Session
            get() =
                Session(
                    exerciseType = exerciseType.mapped(),
                    startTimestamp = startTime.toEpochMilli(),
                    endTimestamp = endTime.toEpochMilli(),
                    durationMillis = duration.toMillis(),
                    calories = calories,
                    customTitle = customTitle,
                    comment = comment,
                    distance = distance,
                    count = count,
                    countType =
                        countType
                            .takeUnless { it == DataType.ExerciseType.CountType.UNDEFINED }
                            ?.mapped<ExerciseCountType>(),
                    altitudeGain = altitudeGain,
                    altitudeLoss = altitudeLoss,
                    maxAltitude = maxAltitude,
                    minAltitude = minAltitude,
                    inclineDistance = inclineDistance,
                    declineDistance = declineDistance,
                    maxCadence = maxCadence,
                    meanCadence = meanCadence,
                    maxCalorieBurnRate = maxCalorieBurnRate,
                    meanCalorieBurnRate = meanCalorieBurnRate,
                    maxHeartRate = maxHeartRate,
                    meanHeartRate = meanHeartRate,
                    minHeartRate = minHeartRate,
                    maxPower = maxPower,
                    meanPower = meanPower,
                    maxRpm = maxRpm,
                    meanRpm = meanRpm,
                    maxSpeed = maxSpeed,
                    meanSpeed = meanSpeed,
                    vo2Max = vo2Max,
                    log =
                        log.orEmpty().map {
                            Log(it.timestamp.toEpochMilli(), it.heartRate, it.cadence, it.count, it.power, it.speed)
                        },
                    route =
                        route.orEmpty().map {
                            Location(it.timestamp.toEpochMilli(), it.latitude, it.longitude, it.altitude, it.accuracy)
                        },
                )
    }
}
