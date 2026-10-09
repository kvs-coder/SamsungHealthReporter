package com.kvs.samsunghealthreporter.example.service

import android.app.Activity
import com.kvs.samsunghealthreporter.SamsungHealthReporter
import com.kvs.samsunghealthreporter.example.demo.DemoRow
import com.kvs.samsunghealthreporter.example.demo.DemoSection
import com.kvs.samsunghealthreporter.model.ChangeSet
import com.kvs.samsunghealthreporter.model.type.HealthType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration.Companion.seconds

/** Demos of **SamsungHealthObserver**: one-shot changes per type since the last sync, and a polling flow. */
class ObserverDemos(
    private val reporter: () -> SamsungHealthReporter,
) : DemoPerformer {
    /** The last sync time per type; a real app persists it. */
    private val lastSync = mutableMapOf<HealthType, Long>()
    private var observed = 0

    private val changeRows =
        HealthType.entries.filter { it.isObservable }.associateBy { type ->
            DemoRow(
                "observer.changes.${type.identifier}",
                "Changes of ${type.identifier} since last sync",
                "observer.changes",
            )
        }
    private val observe =
        DemoRow("observer.observe", "Observe heart rate every 10 s (tap again to stop)", "observer.observe")

    override val section = DemoSection("Observer", listOf(observe) + changeRows.keys)

    override fun perform(
        row: DemoRow,
        activity: Activity,
    ): Flow<String> =
        when (row) {
            observe -> {
                observed = 0
                reporter()
                    .observer
                    .observe(HealthType.HEART_RATE, System.currentTimeMillis(), 10.seconds)
                    .map { changeSet ->
                        observed++
                        "Change set #$observed\n${changeSet.summary}"
                    }.onStart { emit("Waiting for heart rate changes…") }
            }
            in changeRows ->
                flow {
                    val type = changeRows.getValue(row)
                    val since = lastSync[type] ?: (System.currentTimeMillis() - DAY)
                    val changeSet = reporter().observer.changes(type, since)
                    lastSync[type] = changeSet.until
                    emit(changeSet.summary)
                }
            else -> error("Unknown row ${row.id}")
        }

    private val ChangeSet.summary: String
        get() =
            "${upserted.size} upserted, ${deletedUids.size} deleted\n" +
                "Next sync from $until\n" +
                upserted.map { it.json }.prettyList()

    private companion object {
        const val DAY = 86_400_000L
    }
}
