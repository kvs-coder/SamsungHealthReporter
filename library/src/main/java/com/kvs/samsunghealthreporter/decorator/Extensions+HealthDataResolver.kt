package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.model.AggregateFunction
import com.kvs.samsunghealthreporter.model.Time
import com.kvs.samsunghealthreporter.model.session.SleepStage
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver

val HealthDataResolver.AggregateRequest.TimeGroupUnit.string : String get() = "${this.name}_aggregate"

fun HealthDataResolver.AggregateRequest.Builder.addGrouping(type: String): HealthDataResolver.AggregateRequest.Builder {
    return this.apply {
        addGroup(type, "group_$type")
    }
}

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
    timeGroup: Time.Group,
    timeProperty: String,
    offsetProperty: String,
): HealthDataResolver.AggregateRequest.Builder {
    return this.apply {
        when (timeGroup) {
            Time.Group.MINUTELY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.MINUTELY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
            Time.Group.HOURLY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.HOURLY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
            Time.Group.DAILY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.DAILY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
            Time.Group.WEEKLY -> {
                setTimeGroup(
                    HealthDataResolver.AggregateRequest.TimeGroupUnit.WEEKLY,
                    1,
                    timeProperty,
                    offsetProperty,
                    timeGroup.alias
                )
            }
            Time.Group.MONTHLY -> {
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