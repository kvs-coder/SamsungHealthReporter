package com.kvs.samsunghealthreporter.example.demo

/**
 * One demo of one public library call.
 *
 * @param id unique id of the row
 * @param title what the demo does
 * @param call the library call it exercises
 */
data class DemoRow(
    val id: String,
    val title: String,
    val call: String,
)

/** The demos of one library area. */
data class DemoSection(
    val title: String,
    val rows: List<DemoRow>,
)
