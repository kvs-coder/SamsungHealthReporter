package com.kvs.samsunghealthreporter.model.session

import com.google.gson.annotations.*
import com.kvs.samsunghealthreporter.SamsungHealthWriteException
import com.kvs.samsunghealthreporter.model.*
import com.samsung.android.sdk.healthdata.*
import java.util.*

class Exercise : Session<Exercise.ReadResult, Exercise.AggregateResult, Exercise.InsertResult> {
    enum class ExerciseType(val id: Int, val description: String) {
        CUSTOM(0, "Custom"),
        WALKING(1001, "Walking"),
        RUNNING(1002, "Running"),
        BASEBALL(2001, "Baseball"),
        SOFTBALL(2002, "Softball"),
        CRICKET(2003, "Cricket"),
        GOLF(3001, "Golf"),
        BILLIARDS(3002, "Billiards"),
        BOWLING(3003, "Bowling"),
        HOCKEY(4001, "Hockey"),
        RUGBY(4002, "Rugby"),
        BASKETBALL(4003, "Basketball"),
        FOOTBALL(4004, "Football"),
        HANDBALL(4005, "Handball"),
        SOCCER(4006, "Soccer"),
        VOLLEYBALL(5001, "Volleyball"),
        BEACH(5002, "Beach"),
        SQUASH(6001, "Squash"),
        TENNIS(6002, "Tennis"),
        BADMINTON(6003, "Badminton"),
        TABLE_TENNIS(6004, "Table tennis"),
        RACQUETBALL(6005, "Racquetball"),
        BOXING(7002, "Boxing"),
        MARTIAL(7003, "Martial"),
        BALLET(8001, "Ballet"),
        DANCING(8002, "Dancing"),
        BALLROOM_DANCING(8003, "Ballroom dancing"),
        PILATES(9001, "Pilates"),
        YOGA(9002, "Yoga"),
        STRETCHING(10001, "Stretching"),
        JUMP_ROPE(10002, "Jump rope"),
        HULA_HOOPING(10003, "Hula hooping"),
        PUS_HUPS(10004, "Push ups"),
        PULL_UPS(10005, "Pull ups"),
        SIT_UPS(10006, "Sit ups"),
        CIRCUIT_TRAINING(10007, "Circuit training"),
        MOUNTAIN_CLIMBERS(10008, "Mountain climbers"),
        JUMPING_JACKS(10009, "Jumping Jacks"),
        BURPEE(10010, "Burpee"),
        BENCH_PRESS(10011, "Bench press"),
        SQUATS(10012, "Squats"),
        LUNGES(10013, "Lunges"),
        LEGP_RESSES(10014, "Leg presses"),
        LEG_EXTENSIONS(10015, "Leg extensions"),
        LEG_CURLS(10016, "Leg curls"),
        BACK_EXTENSIONS(10017, "Back extensions"),
        LAT_PULL_DOWNS(10018, "Lat pull-downs"),
        DEADLIFTS(10019, "Deadlifts"),
        SHOULDER_PRESSES(10020, "Shoulder presses"),
        FRONT_RAISES(10021, "Front raises"),
        LATERAL_RAISES(10022, "Lateral raises"),
        CRUNCHES(10023, "Crunches"),
        LEG_RAISES(10024, "Leg raises"),
        PLANK(10025, "Plank"),
        ARM_CURLS(10026, "Arm curls"),
        ARM_EXTENSIONS(10027, "Arm extensions"),
        INLINE_SKATING(11001, "Inline skating"),
        HANG_GLIDING(11002, "Hang gliding"),
        PISTOL_SHOOTING(11003, "Pistol shooting"),
        ARCHERY(11004, "Archery"),
        HORSEBACK_RIDING(11005, "Horseback riding"),
        CYCLING(11007, "Cycling"),
        FLYING_DISC(11008, "Flying disc"),
        ROLLER_SKATING(11009, "Roller skating"),
        AEROBICS(12001, "Aerobics"),
        HIKING(13001, "Hiking"),
        ROCK_CLIMBING(13002, "Rock climbing"),
        BACKPACKING(13003, "Backpacking"),
        MOUNTAIN_BIKING(13004, "Mountain biking"),
        ORIENTEERING(13005, "Orienteering"),
        SWIMMING(14001, "Swimming"),
        AQUAROBICS(14002, "Aquarobics"),
        CANOEING(14003, "Canoeing"),
        SAILING(14004, "Sailing"),
        SCUBA_DIVING(14005, "Scuba diving"),
        SNORKELING(14006, "Snorkeling"),
        KAYAKING(14007, "Kayaking"),
        KITESURFING(14008, "Kitesurfing"),
        RAFTING(14009, "Rafting"),
        WINDSURFING(14011, "Windsurfing"),
        YACHTING(14012, "Yachting"),
        WATER_SKIING(14013, "Water skiing"),
        STEP_MACHINE(15001, "Step machine"),
        WEIGHT_MACHINE(15002, "Weight machine"),
        EXERCISE_BIKE(15003, "Exercise bike"),
        ROWING_MACHINE(15004, "Rowing machine"),
        TREADMILL(15005, "Treadmill"),
        ELLIPTICAL_TRAINER(15006, "Elliptical trainer"),
        CROSS_COUNTRY_SKIING(16001, "Cross country skiing"),
        SKIING(16002, "Skiing"),
        ICE_DANCING(16003, "Ice dancing"),
        ICE_SKATING(16004, "Ice skating"),
        ICE_HOSCKEY(16006, "Ice hockey"),
        SNOWBOARDING(16007, "Snowboarding"),
        ALPINE_SKIING(16008, "Alpine skiing"),
        SNOWSHOEING(16009, "Snowshoeing"),
    }

    data class ReadResult(
        override val uuid: String,
        override val packageName: String,
        override val deviceUuid: String,
        override val custom: String?,
        override val createTime: Long,
        override val updateTime: Long,
        override val startTime: Long,
        override val timeOffset: Long,
        override val endTime: Long,
        val type: Type,
        val customType: String,
        val speed: Speed,
        val distance: Distance,
        val calorie: Calorie,
        val duration: Duration,
        val altitude: Altitude,
        val count: Count,
        val cadence: Cadence,
        val heartRate: HeartRate,
        val power: Power,
        val rpm: RPM,
        val comment: String,
        val live: Live?,
        val location: Location?,
        val additional: Additional?
    ) : Session.ReadResult {
        data class Type(val id: Int) {
            val description: String = ExerciseType.values().first {
                it.id == id
            }.description
        }

        data class Speed(
            val mean: Float,
            val max: Float,
            val unit: String
        )

        data class Distance(
            val value: Float,
            val incline: Float,
            val decline: Float,
            val unit: String
        )

        data class Calorie(
            val value: Float,
            val meanBurnRate: Float,
            val maxBurnRate: Float,
            val unit: String
        )

        data class Duration(val value: Long, val unit: String)

        data class Altitude(
            val gain: Float,
            val loss: Float,
            val min: Float,
            val max: Float,
            val unit: String
        )

        data class Count(val value: Int, val id: Int, val unit: String) {
            val description = when (id) {
                HealthConstants.Exercise.COUNT_TYPE_STRIDE -> "stride"
                HealthConstants.Exercise.COUNT_TYPE_STROKE -> "stroke"
                HealthConstants.Exercise.COUNT_TYPE_SWING -> "swing"
                HealthConstants.Exercise.COUNT_TYPE_REPETITION -> "repetition"
                else -> "unknown"
            }
        }

        data class Cadence(
            val mean: Float,
            val max: Float,
            val unit: String
        )

        data class HeartRate(
            val min: Float,
            val mean: Float,
            val max: Float,
            val unit: String
        )

        data class Power(
            val mean: Float,
            val max: Float,
            val unit: String
        )

        data class RPM(
            val mean: Float,
            val max: Float,
            val unit: String
        )

        class Live {
            @SerializedName("start_time")
            @Expose
            var startTime: Long? = null

            @SerializedName("heart_rate")
            @Expose
            var heartRate: Float? = null

            @SerializedName("cadence")
            @Expose
            var cadence: Float? = null

            @SerializedName("count")
            @Expose
            var count: Int? = null

            @SerializedName("power")
            @Expose
            var power: Float? = null

            @SerializedName("speed")
            @Expose
            var speed: Float? = null

            @SerializedName("distance")
            @Expose
            var distance: Float? = null
        }

        class Location {
            @SerializedName("start_time")
            @Expose
            var startTime: Long? = null

            @SerializedName("latitude")
            @Expose
            var latitude: Float? = null

            @SerializedName("longitude")
            @Expose
            var longitude: Float? = null

            @SerializedName("altitude")
            @Expose
            var altitude: Float? = null

            @SerializedName("accuracy")
            @Expose
            var accuracy: Float? = null
        }

        class Additional {
            class Length {
                @SerializedName("duration")
                @Expose
                var duration: Int? = null

                @SerializedName("interval")
                @Expose
                var interval: Int? = null

                @SerializedName("stroke_count")
                @Expose
                var strokeCount: Int? = null

                @SerializedName("stroke_type")
                @Expose
                var strokeType: String? = null
            }

            @SerializedName("pool_length")
            @Expose
            var poolLength: Int? = null

            @SerializedName("pool_length_unit")
            @Expose
            var poolLengthUnit: String? = null

            @SerializedName("total_distance")
            @Expose
            var totalDistance: Float? = null

            @SerializedName("total_duration")
            @Expose
            var totalDuration: Int? = null

            @SerializedName("lengths")
            @Expose
            var lengths: List<Length>? = null
        }
    }

    data class AggregateResult(
        override val time: Time,
        val calorie: Calorie,
        val speed: Speed,
        val distance: Distance,
        val heartRate: HeartRate
    ) : Session.AggregateResult {
        data class Calorie(
            val min: Float,
            val max: Float,
            val avg: Float,
            val sum: Float,
            val count: Float,
            val unit: String
        )

        data class Speed(
            val min: Float,
            val max: Float,
            val avg: Float,
            val unit: String
        )

        data class Distance(
            val min: Float,
            val max: Float,
            val avg: Float,
            val sum: Float,
            val count: Float,
            val unit: String
        )

        data class HeartRate(
            val min: Float,
            val max: Float,
            val avg: Float,
            val unit: String
        )
    }

    data class InsertResult(
        override val packageName: String,
        override val startDate: Date,
        override val timeOffset: Long,
        override val endDate: Date,
        val comment: String?,
        val type: Type,
        val duration: Long,
        val speed: Speed,
        val distance: Distance,
        val calorie: Calorie,
        val altitude: Altitude,
        val count: Count,
        val cadence: Cadence,
        val heartRate: HeartRate,
        val power: Power,
        val rpm: RPM
    ) : Session.InsertResult {
        data class Type(val exerciseType: ExerciseType)

        data class Speed(
            val mean: Float,
            val max: Float,
        )

        data class Distance(
            val value: Float,
            val incline: Float,
            val decline: Float,
        )

        data class Calorie(
            val value: Float,
            val meanBurnRate: Float,
            val maxBurnRate: Float,
        )

        data class Altitude(
            val gain: Float,
            val loss: Float,
            val min: Float,
            val max: Float,
        )

        data class Count(val value: Int, val type: Int)

        data class Cadence(
            val mean: Float,
            val max: Float,
        )

        data class HeartRate(
            val min: Float,
            val mean: Float,
            val max: Float,
        )

        data class Power(
            val mean: Float,
            val max: Float,
        )

        data class RPM(
            val mean: Float,
            val max: Float,
        )
    }

    companion object : Common.Factory<Exercise> {
        private const val SPEED_UNIT = "m/s"
        private const val REVOLUTIONS_MIN_UNIT = "revolutions/min"
        private const val MILLIS_UNIT = "millis"
        private const val WATT_UNIT = "watt"
        private const val BPM_UNIT = "bpm"
        private const val COUNT_UNIT = "count"
        private const val COUNT_MIN_UNIT = "count/min"
        private const val CALORIE_UNIT = "kcal"
        private const val ALIAS_MIN_HEART_RATE = "heart_rate_min"
        private const val ALIAS_MAX_HEART_RATE = "heart_rate_max"
        private const val ALIAS_AVG_HEART_RATE = "heart_rate_avg"
        private const val ALIAS_MIN_CALORIE = "calorie_min"
        private const val ALIAS_MAX_CALORIE = "calorie_max"
        private const val ALIAS_AVG_CALORIE = "calorie_avg"
        private const val ALIAS_SUM_CALORIE = "calorie_sum"
        private const val ALIAS_COUNT_CALORIE = "calorie_count"
        private const val ALIAS_MIN_SPEED = "speed_min"
        private const val ALIAS_MAX_SPEED = "speed_max"
        private const val ALIAS_AVG_SPEED = "speed_avg"
        private const val ALIAS_MIN_DISTANCE = "distance_min"
        private const val ALIAS_MAX_DISTANCE = "distance_max"
        private const val ALIAS_AVG_DISTANCE = "distance_avg"
        private const val ALIAS_SUM_DISTANCE = "distance_sum"
        private const val ALIAS_COUNT_DISTANCE = "distance_count"

        override fun fromReadData(data: HealthData): Exercise {
            return Exercise().apply {
                val liveData =
                    if (data.getBlob(HealthConstants.Exercise.LIVE_DATA) != null) HealthDataUtil.getStructuredData(
                        data.getBlob(HealthConstants.Exercise.LIVE_DATA),
                        ReadResult.Live::class.java
                    ) else null

                val locationData =
                    if (data.getBlob(HealthConstants.Exercise.LOCATION_DATA) != null) HealthDataUtil.getStructuredData(
                        data.getBlob(HealthConstants.Exercise.LOCATION_DATA),
                        ReadResult.Location::class.java
                    ) else null

                val additionalData =
                    if (data.getBlob(HealthConstants.Exercise.ADDITIONAL) != null) HealthDataUtil.getStructuredData(
                        data.getBlob(HealthConstants.Exercise.ADDITIONAL),
                        ReadResult.Additional::class.java
                    ) else null
                readResult = ReadResult(
                    data.getString(HealthConstants.Exercise.UUID),
                    data.getString(HealthConstants.Exercise.PACKAGE_NAME),
                    data.getString(HealthConstants.Exercise.DEVICE_UUID),
                    data.getString(HealthConstants.Exercise.CUSTOM),
                    data.getLong(HealthConstants.Exercise.CREATE_TIME),
                    data.getLong(HealthConstants.Exercise.UPDATE_TIME),
                    data.getLong(HealthConstants.Exercise.START_TIME),
                    data.getLong(HealthConstants.Exercise.TIME_OFFSET),
                    data.getLong(HealthConstants.Exercise.END_TIME),
                    ReadResult.Type(data.getInt(HealthConstants.Exercise.EXERCISE_TYPE)),
                    data.getString(HealthConstants.Exercise.EXERCISE_CUSTOM_TYPE),
                    ReadResult.Speed(
                        data.getFloat(HealthConstants.Exercise.MEAN_SPEED),
                        data.getFloat(HealthConstants.Exercise.MAX_SPEED),
                        SPEED_UNIT
                    ),
                    ReadResult.Distance(
                        data.getFloat(HealthConstants.Exercise.DISTANCE),
                        data.getFloat(HealthConstants.Exercise.INCLINE_DISTANCE),
                        data.getFloat(HealthConstants.Exercise.DECLINE_DISTANCE),
                        HealthDataUnit.METER.unitName
                    ),
                    ReadResult.Calorie(
                        data.getFloat(HealthConstants.Exercise.CALORIE),
                        data.getFloat(HealthConstants.Exercise.MEAN_CALORICBURN_RATE),
                        data.getFloat(HealthConstants.Exercise.MAX_CALORICBURN_RATE),
                        CALORIE_UNIT
                    ),
                    ReadResult.Duration(
                        data.getLong(HealthConstants.Exercise.DURATION),
                        MILLIS_UNIT
                    ),
                    ReadResult.Altitude(
                        data.getFloat(HealthConstants.Exercise.ALTITUDE_GAIN),
                        data.getFloat(HealthConstants.Exercise.ALTITUDE_LOSS),
                        data.getFloat(HealthConstants.Exercise.MIN_ALTITUDE),
                        data.getFloat(HealthConstants.Exercise.MAX_ALTITUDE),
                        HealthDataUnit.METER.unitName
                    ),
                    ReadResult.Count(
                        data.getInt(HealthConstants.Exercise.COUNT),
                        data.getInt(HealthConstants.Exercise.COUNT_TYPE),
                        COUNT_UNIT
                    ),
                    ReadResult.Cadence(
                        data.getFloat(HealthConstants.Exercise.MEAN_CADENCE),
                        data.getFloat(HealthConstants.Exercise.MAX_CADENCE),
                        COUNT_MIN_UNIT
                    ),
                    ReadResult.HeartRate(
                        data.getFloat(HealthConstants.Exercise.MIN_HEART_RATE),
                        data.getFloat(HealthConstants.Exercise.MEAN_HEART_RATE),
                        data.getFloat(HealthConstants.Exercise.MAX_HEART_RATE),
                        BPM_UNIT
                    ),
                    ReadResult.Power(
                        data.getFloat(HealthConstants.Exercise.MEAN_POWER),
                        data.getFloat(HealthConstants.Exercise.MAX_POWER),
                        WATT_UNIT
                    ),
                    ReadResult.RPM(
                        data.getFloat(HealthConstants.Exercise.MEAN_RPM),
                        data.getFloat(HealthConstants.Exercise.MAX_RPM),
                        REVOLUTIONS_MIN_UNIT
                    ),
                    data.getString(HealthConstants.Exercise.COMMENT),
                    liveData,
                    locationData,
                    additionalData
                )
            }
        }

        override fun fromAggregateData(data: HealthData, timeGroup: Time.Group): Exercise {
            return Exercise().apply {
                aggregateResult = AggregateResult(
                    Time(data.getString(timeGroup.alias), timeGroup),
                    AggregateResult.Calorie(
                        data.getFloat(ALIAS_MIN_CALORIE),
                        data.getFloat(ALIAS_MAX_CALORIE),
                        data.getFloat(ALIAS_AVG_CALORIE),
                        data.getFloat(ALIAS_SUM_CALORIE),
                        data.getFloat(ALIAS_COUNT_CALORIE),
                        CALORIE_UNIT
                    ),
                    AggregateResult.Speed(
                        data.getFloat(ALIAS_MIN_SPEED),
                        data.getFloat(ALIAS_MAX_SPEED),
                        data.getFloat(ALIAS_AVG_SPEED),
                        SPEED_UNIT
                    ),
                    AggregateResult.Distance(
                        data.getFloat(ALIAS_MIN_DISTANCE),
                        data.getFloat(ALIAS_MAX_DISTANCE),
                        data.getFloat(ALIAS_AVG_DISTANCE),
                        data.getFloat(ALIAS_SUM_DISTANCE),
                        data.getFloat(ALIAS_COUNT_DISTANCE),
                        HealthDataUnit.METER.unitName
                    ),
                    AggregateResult.HeartRate(
                        data.getFloat(ALIAS_MIN_HEART_RATE),
                        data.getFloat(ALIAS_MAX_HEART_RATE),
                        data.getFloat(ALIAS_AVG_HEART_RATE),
                        BPM_UNIT
                    )
                )
            }
        }
    }

    override val type = HealthConstants.Exercise.HEALTH_DATA_TYPE
    override var readResult: ReadResult? = null
    override var aggregateResult: AggregateResult? = null
    override var insertResult: InsertResult? = null

    private constructor()

    constructor(insertResult: InsertResult) {
        this.insertResult = insertResult
    }

    override fun asOriginal(healthDataStore: HealthDataStore): HealthData {
        val insertResult = this.insertResult ?: throw SamsungHealthWriteException(
            "Insert result was null, nothing to write in Samsung Health"
        )
        val deviceUuid = HealthDeviceManager(healthDataStore).localDevice.uuid
        return HealthData().apply {
            sourceDevice = deviceUuid
            putString(HealthConstants.Exercise.DEVICE_UUID, deviceUuid)
            putString(HealthConstants.Exercise.PACKAGE_NAME, insertResult.packageName)
            putLong(HealthConstants.Exercise.START_TIME, insertResult.startDate.time)
            putLong(HealthConstants.Exercise.TIME_OFFSET, insertResult.timeOffset)
            putLong(HealthConstants.Exercise.END_TIME, insertResult.endDate.time)
            putString(HealthConstants.Exercise.COMMENT, insertResult.comment)
            putInt(HealthConstants.Exercise.EXERCISE_TYPE, insertResult.type.exerciseType.id)
            putFloat(HealthConstants.Exercise.MEAN_SPEED, insertResult.speed.mean)
            putFloat(HealthConstants.Exercise.MAX_SPEED, insertResult.speed.max)
            putFloat(HealthConstants.Exercise.DISTANCE, insertResult.distance.value)
            putFloat(HealthConstants.Exercise.INCLINE_DISTANCE, insertResult.distance.incline)
            putFloat(HealthConstants.Exercise.DECLINE_DISTANCE, insertResult.distance.decline)
            putFloat(HealthConstants.Exercise.CALORIE, insertResult.calorie.value)
            putFloat(HealthConstants.Exercise.MEAN_CALORICBURN_RATE, insertResult.calorie.meanBurnRate)
            putFloat(HealthConstants.Exercise.MAX_CALORICBURN_RATE, insertResult.calorie.maxBurnRate)
            putLong(HealthConstants.Exercise.DURATION, insertResult.duration)
            putFloat(HealthConstants.Exercise.ALTITUDE_GAIN, insertResult.altitude.gain)
            putFloat(HealthConstants.Exercise.ALTITUDE_LOSS, insertResult.altitude.loss)
            putFloat(HealthConstants.Exercise.MIN_ALTITUDE, insertResult.altitude.min)
            putFloat(HealthConstants.Exercise.MAX_ALTITUDE, insertResult.altitude.max)
            putInt(HealthConstants.Exercise.COUNT, insertResult.count.value)
            putInt(HealthConstants.Exercise.COUNT_TYPE, insertResult.count.type)
            putFloat(HealthConstants.Exercise.MEAN_CADENCE, insertResult.cadence.mean)
            putFloat(HealthConstants.Exercise.MAX_CADENCE, insertResult.cadence.max)
            putFloat(HealthConstants.Exercise.MIN_HEART_RATE, insertResult.heartRate.min)
            putFloat(HealthConstants.Exercise.MEAN_HEART_RATE, insertResult.heartRate.mean)
            putFloat(HealthConstants.Exercise.MAX_HEART_RATE, insertResult.heartRate.max)
            putFloat(HealthConstants.Exercise.MEAN_POWER, insertResult.power.mean)
            putFloat(HealthConstants.Exercise.MAX_POWER, insertResult.power.max)
            putFloat(HealthConstants.Exercise.MEAN_RPM, insertResult.rpm.mean)
            putFloat(HealthConstants.Exercise.MAX_RPM, insertResult.rpm.max)
        }
    }
}