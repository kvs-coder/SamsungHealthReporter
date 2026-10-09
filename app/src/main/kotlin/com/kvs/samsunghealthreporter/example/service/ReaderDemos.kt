package com.kvs.samsunghealthreporter.example.service

import android.app.Activity
import com.kvs.samsunghealthreporter.SamsungHealthReporter
import com.kvs.samsunghealthreporter.example.demo.DemoRow
import com.kvs.samsunghealthreporter.example.demo.DemoSection
import com.kvs.samsunghealthreporter.model.SourceFilter
import com.kvs.samsunghealthreporter.model.TimeGroup
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.type.Aggregation
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.TimeGroupUnit
import com.kvs.samsunghealthreporter.service.aggregate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Demos of **SamsungHealthReader**: every readable type, every aggregation, paging and the user profile. */
class ReaderDemos(
    private val reporter: () -> SamsungHealthReporter,
) : DemoPerformer {
    private val readRows =
        HealthType.entries.filter { it.isReadable }.associateBy { type ->
            DemoRow("reader.read.${type.identifier}", "Read ${type.identifier}, last 7 days", "reader.read")
        }
    private val aggregateRows =
        Aggregation.entries.associateBy { aggregation ->
            DemoRow(
                "reader.aggregate.${aggregation.name}",
                "${aggregation.name.lowercase()} per day, last 7 days",
                "reader.aggregate",
            )
        }
    private val stepsHourly = DemoRow("reader.stepsHourly", "Steps per hour today", "reader.aggregate + TimeGroup")
    private val samsungHealthOnly =
        DemoRow("reader.source", "Heart rate measured by Samsung Health today", "reader.read + SourceFilter")
    private val paging = DemoRow("reader.page", "Heart rate, two pages of 5", "reader.readPage")
    private val userProfile = DemoRow("reader.userProfile", "User profile", "reader.userProfile")

    override val section =
        DemoSection(
            "Reader",
            listOf(userProfile, paging, samsungHealthOnly, stepsHourly) + readRows.keys + aggregateRows.keys,
        )

    override fun perform(
        row: DemoRow,
        activity: Activity,
    ): Flow<String> =
        flow {
            val reader = reporter().reader
            val week = TimeRange.lastDays(7)
            val output =
                when (row) {
                    userProfile -> reader.userProfile()?.json?.pretty() ?: "No profile"
                    paging -> {
                        val first = reader.readPage(HealthType.HEART_RATE, week, pageSize = 5)
                        val second =
                            first.nextPageToken?.let { reader.readPage(HealthType.HEART_RATE, week, 5, it) }
                        "Page 1 (${first.items.size}):\n${first.items.map { it.json }.prettyList()}\n\n" +
                            "Page 2 (${second?.items?.size ?: 0}):\n${second?.items.orEmpty().map {
                                it.json
                            }.prettyList()}"
                    }
                    samsungHealthOnly ->
                        reader
                            .read(
                                HealthType.HEART_RATE,
                                TimeRange.day(),
                                limit = 10,
                                source = SourceFilter.SamsungHealth,
                            ).map { it.json }
                            .prettyList()
                    stepsHourly ->
                        reader
                            .aggregate(Aggregation.STEPS_TOTAL, TimeRange.day(), TimeGroup(TimeGroupUnit.HOURLY))
                            .filter { it.value != null }
                            .map { it.json }
                            .prettyList()
                    in readRows -> reader.read(readRows.getValue(row), week, limit = 20).map { it.json }.prettyList()
                    in aggregateRows ->
                        reader
                            .aggregate(aggregateRows.getValue(row), week, TimeGroup(TimeGroupUnit.DAILY))
                            .map { it.json }
                            .prettyList()
                    else -> error("Unknown row ${row.id}")
                }
            emit(output)
        }
}
