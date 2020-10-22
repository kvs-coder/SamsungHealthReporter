package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.model.AggregateFunction
import com.kvs.samsunghealthreporter.model.StepCount
import com.kvs.samsunghealthreporter.model.TimeGroup
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver

val HealthDataResolver.AggregateRequest.TimeGroupUnit.string : String get() = "${this.name}_aggregate"

fun HealthDataResolver.AggregateRequest.Builder.addAggregateFunction(
    aggregateFunction: AggregateFunction,
    type: String
): HealthDataResolver.AggregateRequest.Builder {
    return this.apply {
        when (aggregateFunction) {
            AggregateFunction.SUM -> {
                addFunction(
                    HealthDataResolver.AggregateRequest.AggregateFunction.SUM,
                    type,
                    aggregateFunction.asAlias(type)
                )
            }
            AggregateFunction.MIN -> {
                addFunction(
                    HealthDataResolver.AggregateRequest.AggregateFunction.MIN,
                    type,
                    aggregateFunction.asAlias(type)
                )
            }
            AggregateFunction.MAX -> {
                addFunction(
                    HealthDataResolver.AggregateRequest.AggregateFunction.MAX,
                    type,
                    aggregateFunction.asAlias(type)
                )
            }
            AggregateFunction.AVG -> {
                addFunction(
                    HealthDataResolver.AggregateRequest.AggregateFunction.AVG,
                    type,
                    aggregateFunction.asAlias(type)
                )
            }
            AggregateFunction.COUNT -> {
                addFunction(
                    HealthDataResolver.AggregateRequest.AggregateFunction.COUNT,
                    type,
                    aggregateFunction.asAlias(type)
                )
            }
        }
    }
}

fun HealthDataResolver.AggregateRequest.Builder.setTimeUnit(
    timeGroup: TimeGroup,
    timeProperty: String,
    offsetProperty: String,
): HealthDataResolver.AggregateRequest.Builder {
    return this.apply {
        when (timeGroup) {
            TimeGroup.MINUTELY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.MINUTELY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
            TimeGroup.HOURLY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.HOURLY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
            TimeGroup.DAILY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.DAILY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
            TimeGroup.WEEKLY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.WEEKLY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
            TimeGroup.MONTHLY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.MONTHLY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
        }
    }
}