package com.samsung.android.sdk.health.data.data.entries

import com.samsung.android.sdk.health.data.request.DataType
import java.time.Duration
import java.time.Instant

class HeartRate private constructor(
    val heartRate: Float,
    val min: Float,
    val max: Float,
    val startTime: Instant,
    val endTime: Instant,
) {
    companion object {
        fun of(
            heartRate: Float,
            min: Float,
            max: Float,
            startTime: Instant,
            endTime: Instant,
        ): HeartRate = HeartRate(heartRate, min, max, startTime, endTime)
    }
}

class OxygenSaturation private constructor(
    val oxygenSaturation: Float,
    val min: Float,
    val max: Float,
    val startTime: Instant,
    val endTime: Instant,
) {
    companion object {
        fun of(
            oxygenSaturation: Float,
            min: Float,
            max: Float,
            startTime: Instant,
            endTime: Instant,
        ): OxygenSaturation = OxygenSaturation(oxygenSaturation, min, max, startTime, endTime)
    }
}

class SkinTemperature(
    val skinTemperature: Float,
    val min: Float,
    val max: Float,
    val startTime: Instant,
    val endTime: Instant,
)

class BloodGlucose private constructor(
    val glucose: Float,
    val timestamp: Instant,
) {
    companion object {
        fun of(
            glucose: Float,
            timestamp: Instant,
        ): BloodGlucose = BloodGlucose(glucose, timestamp)
    }
}

class SleepSession private constructor(
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration,
    val stages: List<SleepStage>?,
) {
    class SleepStage private constructor(
        val startTime: Instant,
        val endTime: Instant,
        val stage: DataType.SleepType.StageType,
    ) {
        companion object {
            @JvmStatic
            fun of(
                startTime: Instant,
                endTime: Instant,
                stage: DataType.SleepType.StageType,
            ): SleepStage = SleepStage(startTime, endTime, stage)
        }
    }

    companion object {
        fun of(
            startTime: Instant,
            endTime: Instant,
            duration: Duration,
            stages: List<SleepStage>?,
        ): SleepSession = SleepSession(startTime, endTime, duration, stages)
    }
}

class ExerciseLog private constructor(
    val timestamp: Instant,
    val heartRate: Float?,
    val cadence: Float?,
    val count: Int?,
    val power: Float?,
    val speed: Float?,
) {
    companion object {
        fun of(
            timestamp: Instant,
            heartRate: Float?,
            cadence: Float?,
            count: Int?,
            power: Float?,
            speed: Float?,
        ): ExerciseLog = ExerciseLog(timestamp, heartRate, cadence, count, power, speed)
    }
}

class ExerciseLocation private constructor(
    val timestamp: Instant,
    val longitude: Float,
    val latitude: Float,
    val altitude: Float?,
    val accuracy: Float?,
) {
    companion object {
        fun of(
            timestamp: Instant,
            longitude: Float,
            latitude: Float,
            altitude: Float?,
            accuracy: Float?,
        ): ExerciseLocation = ExerciseLocation(timestamp, longitude, latitude, altitude, accuracy)
    }
}

class ExerciseSession private constructor(
    val startTime: Instant,
    val endTime: Instant,
    val exerciseType: DataType.ExerciseType.PredefinedExerciseType,
    val duration: Duration,
    val calories: Float,
    val customTitle: String?,
    val comment: String?,
    val distance: Float?,
    val count: Int?,
    val countType: DataType.ExerciseType.CountType,
    val altitudeGain: Float?,
    val altitudeLoss: Float?,
    val maxAltitude: Float?,
    val minAltitude: Float?,
    val inclineDistance: Float?,
    val declineDistance: Float?,
    val maxCadence: Float?,
    val meanCadence: Float?,
    val maxCalorieBurnRate: Float?,
    val meanCalorieBurnRate: Float?,
    val maxHeartRate: Float?,
    val meanHeartRate: Float?,
    val minHeartRate: Float?,
    val maxPower: Float?,
    val meanPower: Float?,
    val maxRpm: Float?,
    val meanRpm: Float?,
    val maxSpeed: Float?,
    val meanSpeed: Float?,
    val vo2Max: Float?,
    val log: List<ExerciseLog>?,
    val route: List<ExerciseLocation>?,
) {
    class Builder {
        private var startTime: Instant? = null
        private var endTime: Instant? = null
        private var exerciseType: DataType.ExerciseType.PredefinedExerciseType? = null
        private var duration: Duration? = null
        private var calories: Float = 0f
        private var customTitle: String? = null
        private var comment: String? = null
        private var distance: Float? = null
        private var count: Int? = null
        private var countType: DataType.ExerciseType.CountType? = null
        private var altitudeGain: Float? = null
        private var altitudeLoss: Float? = null
        private var maxAltitude: Float? = null
        private var minAltitude: Float? = null
        private var inclineDistance: Float? = null
        private var declineDistance: Float? = null
        private var maxCadence: Float? = null
        private var meanCadence: Float? = null
        private var maxCalorieBurnRate: Float? = null
        private var meanCalorieBurnRate: Float? = null
        private var maxHeartRate: Float? = null
        private var meanHeartRate: Float? = null
        private var minHeartRate: Float? = null
        private var maxPower: Float? = null
        private var meanPower: Float? = null
        private var maxRpm: Float? = null
        private var meanRpm: Float? = null
        private var maxSpeed: Float? = null
        private var meanSpeed: Float? = null
        private var vo2Max: Float? = null
        private var log: List<ExerciseLog>? = null
        private var route: List<ExerciseLocation>? = null

        fun setStartTime(startTime: Instant): Builder = apply { this.startTime = startTime }

        fun setEndTime(endTime: Instant): Builder = apply { this.endTime = endTime }

        fun setExerciseType(exerciseType: DataType.ExerciseType.PredefinedExerciseType): Builder =
            apply { this.exerciseType = exerciseType }

        fun setDuration(duration: Duration): Builder = apply { this.duration = duration }

        fun setCalories(calories: Float): Builder = apply { this.calories = calories }

        fun setCustomTitle(customTitle: String?): Builder = apply { this.customTitle = customTitle }

        fun setComment(comment: String?): Builder = apply { this.comment = comment }

        fun setDistance(distance: Float?): Builder = apply { this.distance = distance }

        fun setCount(count: Int?): Builder = apply { this.count = count }

        fun setCountType(countType: DataType.ExerciseType.CountType?): Builder = apply { this.countType = countType }

        fun setAltitudeGain(altitudeGain: Float?): Builder = apply { this.altitudeGain = altitudeGain }

        fun setAltitudeLoss(altitudeLoss: Float?): Builder = apply { this.altitudeLoss = altitudeLoss }

        fun setMaxAltitude(maxAltitude: Float?): Builder = apply { this.maxAltitude = maxAltitude }

        fun setMinAltitude(minAltitude: Float?): Builder = apply { this.minAltitude = minAltitude }

        fun setInclineDistance(inclineDistance: Float?): Builder = apply { this.inclineDistance = inclineDistance }

        fun setDeclineDistance(declineDistance: Float?): Builder = apply { this.declineDistance = declineDistance }

        fun setMaxCadence(maxCadence: Float?): Builder = apply { this.maxCadence = maxCadence }

        fun setMeanCadence(meanCadence: Float?): Builder = apply { this.meanCadence = meanCadence }

        fun setMaxCalorieBurnRate(maxCalorieBurnRate: Float?): Builder =
            apply {
                this.maxCalorieBurnRate =
                    maxCalorieBurnRate
            }

        fun setMeanCalorieBurnRate(meanCalorieBurnRate: Float?): Builder =
            apply {
                this.meanCalorieBurnRate =
                    meanCalorieBurnRate
            }

        fun setMaxHeartRate(maxHeartRate: Float?): Builder = apply { this.maxHeartRate = maxHeartRate }

        fun setMeanHeartRate(meanHeartRate: Float?): Builder = apply { this.meanHeartRate = meanHeartRate }

        fun setMinHeartRate(minHeartRate: Float?): Builder = apply { this.minHeartRate = minHeartRate }

        fun setMaxPower(maxPower: Float?): Builder = apply { this.maxPower = maxPower }

        fun setMeanPower(meanPower: Float?): Builder = apply { this.meanPower = meanPower }

        fun setMaxRPM(maxRPM: Float?): Builder = apply { this.maxRpm = maxRPM }

        fun setMeanRPM(meanRPM: Float?): Builder = apply { this.meanRpm = meanRPM }

        fun setMaxSpeed(maxSpeed: Float?): Builder = apply { this.maxSpeed = maxSpeed }

        fun setMeanSpeed(meanSpeed: Float?): Builder = apply { this.meanSpeed = meanSpeed }

        fun setVo2Max(vo2Max: Float?): Builder = apply { this.vo2Max = vo2Max }

        fun setLog(log: List<ExerciseLog>): Builder = apply { this.log = log }

        fun setRoute(route: List<ExerciseLocation>): Builder = apply { this.route = route }

        fun build(): ExerciseSession =
            ExerciseSession(
                startTime ?: error("startTime"),
                endTime ?: error("endTime"),
                exerciseType ?: error("exerciseType"),
                duration ?: error("duration"),
                calories,
                customTitle,
                comment,
                distance,
                count,
                countType ?: DataType.ExerciseType.CountType.UNDEFINED,
                altitudeGain,
                altitudeLoss,
                maxAltitude,
                minAltitude,
                inclineDistance,
                declineDistance,
                maxCadence,
                meanCadence,
                maxCalorieBurnRate,
                meanCalorieBurnRate,
                maxHeartRate,
                meanHeartRate,
                minHeartRate,
                maxPower,
                meanPower,
                maxRpm,
                meanRpm,
                maxSpeed,
                meanSpeed,
                vo2Max,
                log,
                route,
            )
    }

    companion object {
        fun builder(): Builder = Builder()
    }
}
