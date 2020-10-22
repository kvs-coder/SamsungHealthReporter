package com.kvs.samsunghealthreporter.decorator

import com.samsung.android.sdk.healthdata.HealthDataResolver

val HealthDataResolver.AggregateRequest.TimeGroupUnit.string : String get() = "${this.name}_aggregate"