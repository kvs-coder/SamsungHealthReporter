package com.kvs.samsunghealthreporter.example.service

import android.app.Activity
import android.content.Context
import com.kvs.samsunghealthreporter.SamsungHealthReporter
import com.kvs.samsunghealthreporter.example.demo.DemoRow
import com.kvs.samsunghealthreporter.example.demo.DemoSection
import com.kvs.samsunghealthreporter.model.SourceFilter
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.payload.BloodGlucose
import com.kvs.samsunghealthreporter.model.payload.BloodOxygen
import com.kvs.samsunghealthreporter.model.payload.BloodPressure
import com.kvs.samsunghealthreporter.model.payload.BodyComposition
import com.kvs.samsunghealthreporter.model.payload.BodyTemperature
import com.kvs.samsunghealthreporter.model.payload.Exercise
import com.kvs.samsunghealthreporter.model.payload.FloorsClimbed
import com.kvs.samsunghealthreporter.model.payload.HeartRate
import com.kvs.samsunghealthreporter.model.payload.Nutrition
import com.kvs.samsunghealthreporter.model.payload.Sleep
import com.kvs.samsunghealthreporter.model.payload.WaterIntake
import com.kvs.samsunghealthreporter.model.payload.WritableSample
import com.kvs.samsunghealthreporter.model.type.ExerciseType
import com.kvs.samsunghealthreporter.model.type.GlucoseMeasurementType
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.MealStatus
import com.kvs.samsunghealthreporter.model.type.MealType
import com.kvs.samsunghealthreporter.model.type.SleepStageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Demos of **SamsungHealthWriter**: insert one sample of every writable type, then update and delete them.
 *
 * Writing needs Samsung Health developer mode, or Samsung partner approval for a released app.
 */
class WriterDemos(
    private val context: Context,
    private val reporter: () -> SamsungHealthReporter,
) : DemoPerformer {
    private val inserted = mutableMapOf<HealthType, WritableSample>()

    private val insertRows =
        HealthType.entries.filter { it.isWritable }.associateBy { type ->
            DemoRow("writer.insert.${type.identifier}", "Insert one ${type.identifier}", "writer.insert")
        }
    private val insertAll =
        DemoRow("writer.insertAll", "Insert one sample of every writable type", "writer.insert(list)")
    private val update = DemoRow("writer.update", "Update the inserted heart rate to 99 bpm", "writer.update")
    private val deleteInserted = DemoRow("writer.delete", "Delete every inserted sample", "writer.delete(sample)")
    private val deleteByClientDataIds =
        DemoRow(
            "writer.deleteByClientDataIds",
            "Delete the inserted water intake by client data id",
            "writer.deleteByClientDataIds",
        )
    private val deleteByUid =
        DemoRow(
            "writer.deleteByUid",
            "Delete today's heart rate written by this app, by uid",
            "writer.delete(type, uids)",
        )

    override val section =
        DemoSection(
            "Writer",
            listOf(insertAll) + insertRows.keys + listOf(update, deleteByClientDataIds, deleteByUid, deleteInserted),
        )

    override fun perform(
        row: DemoRow,
        activity: Activity,
    ): Flow<String> =
        flow {
            val writer = reporter().writer
            val output =
                when (row) {
                    insertAll -> {
                        val samples = writer.insert(insertRows.values.map { sample(it, System.currentTimeMillis()) })
                        samples.forEach { inserted[it.healthType] = it }
                        "Inserted ${samples.size} samples:\n" + samples.map { it.json }.prettyList()
                    }
                    in insertRows -> {
                        val sample = writer.insert(sample(insertRows.getValue(row), System.currentTimeMillis()))
                        inserted[sample.healthType] = sample
                        "Inserted:\n" + sample.json.pretty()
                    }
                    update -> {
                        val heartRate =
                            inserted[HealthType.HEART_RATE] as? HeartRate
                                ?: return@flow emit("Insert a heart rate first")
                        val updated = heartRate.copy(harmonized = heartRate.harmonized.copy(heartRate = 99f))
                        writer.update(updated)
                        inserted[HealthType.HEART_RATE] = updated
                        "Updated:\n" + updated.json.pretty()
                    }
                    deleteByClientDataIds -> {
                        val id =
                            inserted.remove(HealthType.WATER_INTAKE)?.clientDataId
                                ?: return@flow emit("Insert a water intake first")
                        writer.deleteByClientDataIds(HealthType.WATER_INTAKE, listOf(id))
                        "Deleted water intake $id"
                    }
                    deleteByUid -> {
                        val own =
                            reporter().reader.read(
                                HealthType.HEART_RATE,
                                TimeRange.day(),
                                source = SourceFilter.App(context.packageName),
                            )
                        val uids = own.mapNotNull { it.uid }
                        if (uids.isEmpty()) return@flow emit("No heart rate written by this app today")
                        writer.delete(HealthType.HEART_RATE, uids)
                        "Deleted ${uids.size} heart rate samples:\n" + uids.joinToString("\n")
                    }
                    deleteInserted -> {
                        val samples = inserted.values.toList()
                        samples.forEach { writer.delete(it) }
                        inserted.clear()
                        "Deleted ${samples.size} samples"
                    }
                    else -> error("Unknown row ${row.id}")
                }
            emit(output)
        }

    /** A plausible sample of [type] that ends now. */
    private fun sample(
        type: HealthType,
        now: Long,
    ): WritableSample {
        val start = now - HOUR
        return when (type) {
            HealthType.HEART_RATE -> HeartRate(start, now, HeartRate.Harmonized(heartRate = 72f, min = 60f, max = 90f))
            HealthType.BLOOD_OXYGEN ->
                BloodOxygen(
                    start,
                    now,
                    BloodOxygen.Harmonized(oxygenSaturation = 97f, min = 95f, max = 99f),
                )
            HealthType.BLOOD_PRESSURE ->
                BloodPressure(
                    now,
                    harmonized = BloodPressure.Harmonized(systolic = 120f, diastolic = 80f, mean = 93f, pulseRate = 65),
                )
            HealthType.BLOOD_GLUCOSE ->
                BloodGlucose(
                    now,
                    harmonized =
                        BloodGlucose.Harmonized(
                            glucoseLevel = 5.4f,
                            mealStatus = MealStatus.FASTING,
                            measurementType = GlucoseMeasurementType.WHOLE_BLOOD,
                        ),
                )
            HealthType.BODY_COMPOSITION ->
                BodyComposition(
                    now,
                    harmonized = BodyComposition.Harmonized(weight = 80f, height = 180f, bodyFat = 18f),
                )
            HealthType.BODY_TEMPERATURE ->
                BodyTemperature(
                    now,
                    harmonized = BodyTemperature.Harmonized(temperature = 36.6f),
                )
            HealthType.EXERCISE -> exercise(start, now)
            HealthType.FLOORS_CLIMBED -> FloorsClimbed(start, now, FloorsClimbed.Harmonized(floor = 3f))
            HealthType.SLEEP -> sleep(start, now)
            HealthType.NUTRITION ->
                Nutrition(
                    now,
                    harmonized =
                        Nutrition.Harmonized(
                            title = "Apple",
                            mealType = MealType.MORNING_SNACK,
                            calories = 95f,
                        ),
                )
            HealthType.WATER_INTAKE -> WaterIntake(now, harmonized = WaterIntake.Harmonized(amount = 250f))
            HealthType.SKIN_TEMPERATURE,
            HealthType.ENERGY_SCORE,
            HealthType.IRREGULAR_HEART_RHYTHM_NOTIFICATION,
            HealthType.SLEEP_APNEA,
            HealthType.STEPS,
            HealthType.STEPS_GOAL,
            HealthType.ACTIVITY_SUMMARY,
            HealthType.ACTIVE_CALORIES_BURNED_GOAL,
            HealthType.ACTIVE_TIME_GOAL,
            HealthType.EXERCISE_LOCATION,
            HealthType.SLEEP_GOAL,
            HealthType.NUTRITION_GOAL,
            HealthType.WATER_INTAKE_GOAL,
            HealthType.USER_PROFILE,
            -> error("${type.identifier} is read-only")
        }
    }

    private fun exercise(
        start: Long,
        now: Long,
    ): Exercise =
        Exercise(
            start,
            now,
            Exercise.Harmonized(
                exerciseType = ExerciseType.RUNNING,
                sessions =
                    listOf(
                        Exercise.Session(
                            exerciseType = ExerciseType.RUNNING,
                            startTimestamp = start,
                            endTimestamp = now,
                            durationMillis = HOUR,
                            calories = 600f,
                            distance = 10_000f,
                            meanHeartRate = 150f,
                        ),
                    ),
            ),
        )

    private fun sleep(
        start: Long,
        now: Long,
    ): Sleep =
        Sleep(
            start,
            now,
            Sleep.Harmonized(
                durationMillis = HOUR,
                sessions =
                    listOf(
                        Sleep.Session(
                            startTimestamp = start,
                            endTimestamp = now,
                            durationMillis = HOUR,
                            stages =
                                listOf(
                                    Sleep.Stage(start, start + HOUR / 2, SleepStageType.LIGHT),
                                    Sleep.Stage(start + HOUR / 2, now, SleepStageType.DEEP),
                                ),
                        ),
                    ),
            ),
        )

    private companion object {
        const val HOUR = 3_600_000L
    }
}
