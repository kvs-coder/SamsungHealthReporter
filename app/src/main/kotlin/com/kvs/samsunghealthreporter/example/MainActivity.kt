package com.kvs.samsunghealthreporter.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kvs.samsunghealthreporter.example.demo.DemoScreen
import com.kvs.samsunghealthreporter.example.demo.DemoViewModel

/** Hosts the demo screen; binds the view model's state to it and its events to the view model. */
class MainActivity : ComponentActivity() {
    private val viewModel: DemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                val state by viewModel.state.collectAsStateWithLifecycle()
                DemoScreen(state, viewModel::onEvent)
            }
        }
    }
}
