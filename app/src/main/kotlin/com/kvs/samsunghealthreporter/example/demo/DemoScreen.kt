package com.kvs.samsunghealthreporter.example.demo

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val COLLAPSED_LINES = 8

/** The demo screen: one section per library service, one row per public call. Tap a row to run it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScreen(
    state: DemoState,
    onEvent: (DemoEvent) -> Unit,
) {
    val activity = LocalContext.current as Activity
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SamsungHealthReporter") },
                actions = { TextButton(onClick = { onEvent(DemoEvent.ClearTapped) }) { Text("Clear") } },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { DemoBanner(state.isSamsungHealthAvailable) }
            state.sections.forEach { section ->
                item(key = section.title) { DemoHeader(section) }
                items(section.rows, key = { it.id }) { row ->
                    DemoCell(row, state.results[row.id]) { onEvent(DemoEvent.RowTapped(row, activity)) }
                }
            }
        }
    }
}

@Composable
private fun DemoBanner(isAvailable: Boolean) {
    val text =
        if (isAvailable) {
            "Samsung Health is installed. Start with Manager › Request every permission. Writing needs developer mode."
        } else {
            "Samsung Health is not installed. Install it from the store, then reopen the app."
        }
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                .padding(12.dp),
    )
}

@Composable
private fun DemoHeader(section: DemoSection) {
    Row(Modifier.padding(top = 16.dp, bottom = 4.dp), verticalAlignment = Alignment.Bottom) {
        Text(section.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Text("${section.rows.size} demos", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
    }
}

@Composable
private fun DemoCell(
    row: DemoRow,
    result: RowResult?,
    onTap: () -> Unit,
) {
    var expanded by remember(row.id) { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onTap)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(row.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        row.call,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray,
                    )
                }
                DemoStatus(result)
            }
            val output =
                when (result) {
                    null -> null
                    is RowResult.Running -> result.output
                    is RowResult.Success -> result.output
                    is RowResult.Failure -> result.message
                }
            if (output != null) {
                Text(
                    text = output,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = if (result is RowResult.Failure) MaterialTheme.colorScheme.error else Color.Unspecified,
                    maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_LINES,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .clickable { expanded = !expanded },
                )
            }
        }
    }
}

@Composable
private fun DemoStatus(result: RowResult?) {
    when (result) {
        null -> Text("Run", color = MaterialTheme.colorScheme.primary)
        is RowResult.Running -> CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
        is RowResult.Success -> Text("✓", color = Color(0xFF2E7D32), fontSize = 18.sp)
        is RowResult.Failure -> Text("✕", color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
    }
}
