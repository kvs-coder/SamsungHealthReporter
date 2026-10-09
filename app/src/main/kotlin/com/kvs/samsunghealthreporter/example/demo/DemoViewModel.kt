package com.kvs.samsunghealthreporter.example.demo

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kvs.samsunghealthreporter.example.service.SamsungHealthReporterService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Turns [DemoEvent]s into demo runs and exposes their results as [DemoState]. */
class DemoViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val service = SamsungHealthReporterService(application)
    private val jobs = mutableMapOf<String, Job>()
    private val mutableState =
        MutableStateFlow(
            DemoState(
                isSamsungHealthAvailable = service.isSamsungHealthAvailable,
                sections = service.sections,
            ),
        )

    val state: StateFlow<DemoState> = mutableState.asStateFlow()

    fun onEvent(event: DemoEvent) {
        when (event) {
            is DemoEvent.RowTapped -> toggle(event.row, event.activity)
            DemoEvent.ClearTapped -> {
                jobs.values.forEach { it.cancel() }
                jobs.clear()
                mutableState.update { it.copy(results = emptyMap()) }
            }
        }
    }

    private fun toggle(
        row: DemoRow,
        activity: Activity,
    ) {
        jobs.remove(row.id)?.let { running ->
            running.cancel()
            setResult(row, RowResult.Success("Stopped"))
            return
        }
        setResult(row, RowResult.Running())
        jobs[row.id] =
            viewModelScope.launch {
                var last: String? = null
                service
                    .publisher(row, activity)
                    .onCompletion { error ->
                        jobs.remove(row.id)
                        if (error == null) setResult(row, RowResult.Success(last ?: "Done"))
                    }.catch { error ->
                        setResult(row, RowResult.Failure("${error::class.simpleName}: ${error.message}"))
                    }.collect { output ->
                        last = output
                        setResult(row, RowResult.Running(output))
                    }
            }
    }

    private fun setResult(
        row: DemoRow,
        result: RowResult,
    ) {
        mutableState.update { it.copy(results = it.results + (row.id to result)) }
    }
}
