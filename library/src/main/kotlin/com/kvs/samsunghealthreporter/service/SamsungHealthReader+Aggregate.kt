package com.kvs.samsunghealthreporter.service

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.asLocalDateFilter
import com.kvs.samsunghealthreporter.decorator.asLocalDateGroup
import com.kvs.samsunghealthreporter.decorator.asLocalTimeFilter
import com.kvs.samsunghealthreporter.decorator.asLocalTimeGroup
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.decorator.harmonize
import com.kvs.samsunghealthreporter.decorator.sdkCall
import com.kvs.samsunghealthreporter.model.Aggregate
import com.kvs.samsunghealthreporter.model.Ordering
import com.kvs.samsunghealthreporter.model.TimeGroup
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.type.Aggregation
import com.kvs.samsunghealthreporter.model.type.TimeGroupUnit
import com.samsung.android.sdk.health.data.data.AggregateOperation
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.request.AggregateRequest
import com.samsung.android.sdk.health.data.request.DataType

/**
 * Aggregates Samsung Health data, e.g. total steps per hour.
 *
 * Results cover [range] in local time; with a [group] there is one result per bucket.
 *
 * @param aggregation **Aggregation** the operation
 * @param range **TimeRange** local time range
 * @param group **TimeGroup?** bucket size. One result for the whole range by default
 * @param ordering **Ordering** by bucket start. [Ordering.ASCENDING] by default
 * @return **List<Aggregate>** one value per bucket
 * @throws SamsungHealthException.InvalidValue when [group] is by minute or hour and
 * [Aggregation.supportsTimeGrouping] is false
 * @throws SamsungHealthException.NotAuthorized when read permission for [Aggregation.healthType] is missing
 */
public suspend fun SamsungHealthReader.aggregate(
    aggregation: Aggregation,
    range: TimeRange,
    group: TimeGroup? = null,
    ordering: Ordering = Ordering.ASCENDING,
): List<Aggregate> {
    if (group != null && !aggregation.supportsTimeGrouping && group.unit.isTimeOfDay) {
        throw SamsungHealthException.InvalidValue("${aggregation.name} can't be grouped by ${group.unit}")
    }
    val query = AggregateQuery(range, group, ordering)
    val results =
        when (aggregation) {
            Aggregation.STEPS_TOTAL -> localTime(DataType.StepsType.TOTAL, query)
            Aggregation.ACTIVITY_SUMMARY_TOTAL_ACTIVE_CALORIES_BURNED ->
                localTime(DataType.ActivitySummaryType.TOTAL_ACTIVE_CALORIES_BURNED, query)
            Aggregation.ACTIVITY_SUMMARY_TOTAL_ACTIVE_TIME ->
                localTime(DataType.ActivitySummaryType.TOTAL_ACTIVE_TIME, query)
            Aggregation.ACTIVITY_SUMMARY_TOTAL_CALORIES_BURNED ->
                localTime(DataType.ActivitySummaryType.TOTAL_CALORIES_BURNED, query)
            Aggregation.ACTIVITY_SUMMARY_TOTAL_DISTANCE -> localTime(DataType.ActivitySummaryType.TOTAL_DISTANCE, query)
            Aggregation.EXERCISE_TOTAL_CALORIES -> localTime(DataType.ExerciseType.TOTAL_CALORIES, query)
            Aggregation.FLOORS_CLIMBED_TOTAL -> localTime(DataType.FloorsClimbedType.TOTAL, query)
            Aggregation.EXERCISE_TOTAL_DURATION -> localDate(DataType.ExerciseType.TOTAL_DURATION, query)
            Aggregation.HEART_RATE_MAX -> localDate(DataType.HeartRateType.MAX, query)
            Aggregation.HEART_RATE_MIN -> localDate(DataType.HeartRateType.MIN, query)
            Aggregation.SLEEP_TOTAL_DURATION -> localDate(DataType.SleepType.TOTAL_DURATION, query)
            Aggregation.NUTRITION_TOTAL_CALORIES -> dualTime(DataType.NutritionType.TOTAL_CALORIES, query)
            Aggregation.WATER_INTAKE_TOTAL -> dualTime(DataType.WaterIntakeType.TOTAL, query)
            Aggregation.STEPS_GOAL_LAST -> allSourceLocalDate(DataType.StepsGoalType.LAST, query)
            Aggregation.ACTIVE_CALORIES_BURNED_GOAL_LAST ->
                allSourceLocalDate(DataType.ActiveCaloriesBurnedGoalType.LAST, query)
            Aggregation.ACTIVE_TIME_GOAL_LAST -> allSourceLocalDate(DataType.ActiveTimeGoalType.LAST, query)
            Aggregation.NUTRITION_GOAL_LAST_CALORIES ->
                allSourceLocalDate(DataType.NutritionGoalType.LAST_CALORIES, query)
            Aggregation.SLEEP_GOAL_LAST_BED_TIME -> allSourceLocalDate(DataType.SleepGoalType.LAST_BED_TIME, query)
            Aggregation.SLEEP_GOAL_LAST_WAKE_UP_TIME ->
                allSourceLocalDate(DataType.SleepGoalType.LAST_WAKE_UP_TIME, query)
            Aggregation.WATER_INTAKE_GOAL_LAST -> allSourceLocalDate(DataType.WaterIntakeGoalType.LAST, query)
        }
    return results.map { it.harmonize(aggregation) }
}

private class AggregateQuery(
    val range: TimeRange,
    val group: TimeGroup?,
    val ordering: Ordering,
)

private val TimeGroupUnit.isTimeOfDay: Boolean
    get() = this == TimeGroupUnit.MINUTELY || this == TimeGroupUnit.HOURLY

private suspend fun <T : Any> SamsungHealthReader.pages(
    request: (pageToken: String?) -> AggregateRequest<T>,
): List<AggregatedData<T>> {
    val results = mutableListOf<AggregatedData<T>>()
    var pageToken: String? = null
    do {
        val response = sdkCall { store.aggregateData(request(pageToken)) }
        results += response.dataList
        pageToken = response.pageToken
    } while (pageToken != null)
    return results
}

private suspend fun <T : Any> SamsungHealthReader.localTime(
    operation: AggregateOperation<T, AggregateRequest.LocalTimeBuilder<T>>,
    query: AggregateQuery,
): List<AggregatedData<T>> =
    pages { pageToken ->
        operation.requestBuilder
            .setLocalTimeFilterWithGroup(query.range.asLocalTimeFilter, query.group?.asLocalTimeGroup)
            .setOrdering(query.ordering.asOriginal)
            .setPageToken(pageToken)
            .build()
    }

private suspend fun <T : Any> SamsungHealthReader.dualTime(
    operation: AggregateOperation<T, AggregateRequest.DualTimeBuilder<T>>,
    query: AggregateQuery,
): List<AggregatedData<T>> =
    pages { pageToken ->
        operation.requestBuilder
            .setLocalTimeFilterWithGroup(query.range.asLocalTimeFilter, query.group?.asLocalTimeGroup)
            .setOrdering(query.ordering.asOriginal)
            .setPageToken(pageToken)
            .build()
    }

private suspend fun <T : Any> SamsungHealthReader.localDate(
    operation: AggregateOperation<T, AggregateRequest.LocalDateBuilder<T>>,
    query: AggregateQuery,
): List<AggregatedData<T>> =
    pages { pageToken ->
        operation.requestBuilder
            .setLocalDateFilterWithGroup(query.range.asLocalDateFilter, query.group?.asLocalDateGroup)
            .setOrdering(query.ordering.asOriginal)
            .setPageToken(pageToken)
            .build()
    }

private suspend fun <T : Any> SamsungHealthReader.allSourceLocalDate(
    operation: AggregateOperation<T, AggregateRequest.AllSourceLocalDateBuilder<T>>,
    query: AggregateQuery,
): List<AggregatedData<T>> =
    pages { pageToken ->
        operation.requestBuilder
            .setLocalDateFilterWithGroup(query.range.asLocalDateFilter, query.group?.asLocalDateGroup)
            .setOrdering(query.ordering.asOriginal)
            .setPageToken(pageToken)
            .build()
    }
