package com.kvs.samsunghealthreporter.example.service

import android.app.Activity
import android.content.Context
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.SamsungHealthReporter
import com.kvs.samsunghealthreporter.example.demo.DemoRow
import com.kvs.samsunghealthreporter.example.demo.DemoSection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/** Owns one **SamsungHealthReporter** and one performer per library area. */
class SamsungHealthReporterService(
    context: Context,
) {
    private val appContext = context.applicationContext

    // Created on first use: connecting throws SamsungHealthException.Resolvable when Samsung Health needs setup.
    private val reporter by lazy { SamsungHealthReporter(appContext) }
    private val errors = DemoErrors()
    private val performers: List<DemoPerformer> =
        listOf(
            ManagerDemos(appContext, { reporter }, errors),
            ReaderDemos({ reporter }),
            WriterDemos(appContext, { reporter }),
            ObserverDemos({ reporter }),
        )

    val isSamsungHealthAvailable: Boolean get() = SamsungHealthReporter.isAvailable(appContext)

    val sections: List<DemoSection> get() = performers.map { it.section }

    fun publisher(
        row: DemoRow,
        activity: Activity,
    ): Flow<String> =
        performers
            .first { performer -> row in performer.section.rows }
            .perform(row, activity)
            .catch { error ->
                if (error is SamsungHealthException.Resolvable) errors.lastResolvable = error
                throw error
            }
}

/** The last error Samsung Health can fix, for the "Resolve" demo. */
class DemoErrors {
    var lastResolvable: SamsungHealthException.Resolvable? = null
}
