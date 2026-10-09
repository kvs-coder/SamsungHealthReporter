package com.kvs.samsunghealthreporter

import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.payload.BloodGlucose
import com.kvs.samsunghealthreporter.model.payload.BloodOxygen
import com.kvs.samsunghealthreporter.model.payload.BloodPressure
import com.kvs.samsunghealthreporter.model.payload.BodyComposition
import com.kvs.samsunghealthreporter.model.payload.BodyTemperature
import com.kvs.samsunghealthreporter.model.payload.EnergyScore
import com.kvs.samsunghealthreporter.model.payload.Exercise
import com.kvs.samsunghealthreporter.model.payload.FloorsClimbed
import com.kvs.samsunghealthreporter.model.payload.HeartRate
import com.kvs.samsunghealthreporter.model.payload.IrregularHeartRhythmNotification
import com.kvs.samsunghealthreporter.model.payload.Nutrition
import com.kvs.samsunghealthreporter.model.payload.Sample
import com.kvs.samsunghealthreporter.model.payload.SkinTemperature
import com.kvs.samsunghealthreporter.model.payload.Sleep
import com.kvs.samsunghealthreporter.model.payload.SleepApnea
import com.kvs.samsunghealthreporter.model.payload.WaterIntake
import com.kvs.samsunghealthreporter.model.type.ExerciseCountType
import com.kvs.samsunghealthreporter.model.type.ExerciseType
import com.kvs.samsunghealthreporter.model.type.GlucoseMeasurementType
import com.kvs.samsunghealthreporter.model.type.GlucoseSampleSource
import com.kvs.samsunghealthreporter.model.type.IrregularHeartRhythmStatus
import com.kvs.samsunghealthreporter.model.type.MealStatus
import com.kvs.samsunghealthreporter.model.type.MealType
import com.kvs.samsunghealthreporter.model.type.SleepApneaSign
import com.kvs.samsunghealthreporter.model.type.SleepStageType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

const val START = 1_626_884_800_000L
const val END = START + 3_600_000L
const val OFFSET = 7_200

val source = DataSource(appId = "com.example", deviceId = "device-1")

/** Parses JSON into the plain map a Flutter plugin would send. */
fun String.asMap(): Map<String, Any?> = Json.parseToJsonElement(this).plain as Map<String, Any?>

private val JsonElement.plain: Any?
    get() =
        when (this) {
            is JsonNull -> null
            is JsonObject -> mapValues { it.value.plain }
            is JsonArray -> map { it.plain }
            is JsonPrimitive ->
                when {
                    isString -> content
                    booleanOrNull != null -> booleanOrNull
                    longOrNull != null -> longOrNull
                    else -> doubleOrNull
                }
        }

val heartRate =
    HeartRate(
        startTimestamp = START,
        endTimestamp = END,
        harmonized =
            HeartRate.Harmonized(
                heartRate = 72f,
                min = 60f,
                max = 90f,
                series = listOf(HeartRate.Series(72f, 60f, 90f, START, START + 60_000)),
            ),
        uid = "uid-1",
        clientDataId = "client-1",
        zoneOffsetSeconds = OFFSET,
        dataSource = source,
    )

val bloodOxygen =
    BloodOxygen(
        startTimestamp = START,
        endTimestamp = END,
        harmonized =
            BloodOxygen.Harmonized(
                oxygenSaturation = 97f,
                min = 95f,
                max = 99f,
                series = listOf(BloodOxygen.Series(97f, 95f, 99f, START, START + 60_000)),
            ),
        clientDataId = "client-2",
        zoneOffsetSeconds = OFFSET,
    )

val skinTemperature =
    SkinTemperature(
        startTimestamp = START,
        endTimestamp = END,
        harmonized =
            SkinTemperature.Harmonized(
                skinTemperature = 33.5f,
                min = 33f,
                max = 34f,
                series = listOf(SkinTemperature.Series(33.5f, 33f, 34f, START, START + 60_000)),
            ),
        uid = "uid-3",
    )

val bloodPressure =
    BloodPressure(
        startTimestamp = START,
        harmonized =
            BloodPressure.Harmonized(
                systolic = 120f,
                diastolic = 80f,
                mean = 93.3f,
                pulseRate = 65,
                medicationTaken = false,
            ),
        clientDataId = "client-4",
    )

val bloodGlucose =
    BloodGlucose(
        startTimestamp = START,
        endTimestamp = END,
        harmonized =
            BloodGlucose.Harmonized(
                glucoseLevel = 5.4f,
                mealStatus = MealStatus.FASTING,
                measurementType = GlucoseMeasurementType.WHOLE_BLOOD,
                sampleSource = GlucoseSampleSource.CAPILLARY,
                insulinInjected = 2f,
                mealTimestamp = START - 3_600_000,
                medicationTaken = true,
                series = listOf(BloodGlucose.Series(5.4f, START)),
            ),
        clientDataId = "client-5",
    )

val bodyComposition =
    BodyComposition(
        startTimestamp = START,
        harmonized =
            BodyComposition.Harmonized(
                weight = 80f,
                height = 180f,
                bodyFat = 18f,
                bodyFatMass = 14.4f,
                bodyMassIndex = 24.7f,
                fatFree = 82f,
                fatFreeMass = 65.6f,
                muscleMass = 40f,
                skeletalMuscle = 38f,
                skeletalMuscleMass = 30.4f,
                totalBodyWater = 48f,
                basalMetabolicRate = 1800,
            ),
    )

val bodyTemperature =
    BodyTemperature(
        startTimestamp = START,
        harmonized = BodyTemperature.Harmonized(temperature = 36.6f),
    )

val floorsClimbed =
    FloorsClimbed(
        startTimestamp = START,
        endTimestamp = END,
        harmonized = FloorsClimbed.Harmonized(floor = 3f),
    )

val waterIntake =
    WaterIntake(
        startTimestamp = START,
        harmonized = WaterIntake.Harmonized(amount = 250f),
    )

val nutrition =
    Nutrition(
        startTimestamp = START,
        harmonized =
            Nutrition.Harmonized(
                title = "Apple",
                mealType = MealType.MORNING_SNACK,
                calories = 95f,
                carbohydrate = 25f,
                protein = 0.5f,
                totalFat = 0.3f,
                sugar = 19f,
                dietaryFiber = 4.4f,
                potassium = 195f,
                vitaminC = 8.4f,
            ),
    )

val energyScore =
    EnergyScore(
        startTimestamp = START,
        harmonized = EnergyScore.Harmonized(score = 82f),
        uid = "uid-11",
    )

val irregularHeartRhythmNotification =
    IrregularHeartRhythmNotification(
        startTimestamp = START,
        harmonized = IrregularHeartRhythmNotification.Harmonized(status = IrregularHeartRhythmStatus.DETECTED),
    )

val sleepApnea =
    SleepApnea(
        startTimestamp = START,
        harmonized = SleepApnea.Harmonized(detectedSign = SleepApneaSign.NOT_DETECTED),
    )

val sleep =
    Sleep(
        startTimestamp = START,
        endTimestamp = END,
        harmonized =
            Sleep.Harmonized(
                durationMillis = 3_600_000,
                sessions =
                    listOf(
                        Sleep.Session(
                            startTimestamp = START,
                            endTimestamp = END,
                            durationMillis = 3_600_000,
                            stages =
                                listOf(
                                    Sleep.Stage(START, START + 1_800_000, SleepStageType.LIGHT),
                                    Sleep.Stage(START + 1_800_000, END, SleepStageType.DEEP),
                                ),
                        ),
                    ),
                sleepScore = 80,
            ),
        clientDataId = "client-14",
    )

val exercise =
    Exercise(
        startTimestamp = START,
        endTimestamp = END,
        harmonized =
            Exercise.Harmonized(
                exerciseType = ExerciseType.RUNNING,
                sessions =
                    listOf(
                        Exercise.Session(
                            exerciseType = ExerciseType.RUNNING,
                            startTimestamp = START,
                            endTimestamp = END,
                            durationMillis = 3_600_000,
                            calories = 600f,
                            distance = 10_000f,
                            count = 9_000,
                            countType = ExerciseCountType.STRIDE,
                            meanHeartRate = 150f,
                            maxHeartRate = 175f,
                            meanSpeed = 2.8f,
                            vo2Max = 48f,
                            log = listOf(Exercise.Log(START + 60_000, heartRate = 140f, speed = 2.7f)),
                            route = listOf(Exercise.Location(START + 60_000, 52.52f, 13.40f, 34f, 5f)),
                        ),
                    ),
            ),
        clientDataId = "client-15",
    )

/** One sample of every payload type. */
val allSamples: List<Sample> =
    listOf(
        heartRate,
        bloodOxygen,
        skinTemperature,
        bloodPressure,
        bloodGlucose,
        bodyComposition,
        bodyTemperature,
        floorsClimbed,
        waterIntake,
        nutrition,
        energyScore,
        irregularHeartRhythmNotification,
        sleepApnea,
        sleep,
        exercise,
    )

/** A Samsung Health response page, mocked because the SDK doesn't document a public constructor. */
fun <T : android.os.Parcelable> response(
    items: List<T>,
    nextPageToken: String? = null,
): com.samsung.android.sdk.health.data.response.DataResponse<T> =
    io.mockk.mockk {
        io.mockk.every { dataList } returns items
        io.mockk.every { pageToken } returns nextPageToken
    }
