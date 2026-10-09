package com.kvs.samsunghealthreporter.example.demo

import android.app.Activity

/** Everything the demo screen shows. */
data class DemoState(
    val isSamsungHealthAvailable: Boolean,
    val sections: List<DemoSection>,
    val results: Map<String, RowResult> = emptyMap(),
)

/** The state of one demo row. */
sealed interface RowResult {
    /** The demo is running; [output] holds the latest result of a long-running demo. */
    data class Running(
        val output: String? = null,
    ) : RowResult

    data class Success(
        val output: String,
    ) : RowResult

    data class Failure(
        val message: String,
    ) : RowResult
}

/** User intents. */
sealed interface DemoEvent {
    /** Runs the demo, or stops it when it is still running. */
    data class RowTapped(
        val row: DemoRow,
        val activity: Activity,
    ) : DemoEvent

    /** Clears every result. */
    data object ClearTapped : DemoEvent
}
