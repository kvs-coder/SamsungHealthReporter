package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.model.payload.BloodGlucose
import com.kvs.samsunghealthreporter.model.payload.BloodOxygen
import com.kvs.samsunghealthreporter.model.payload.BloodPressure
import com.kvs.samsunghealthreporter.model.payload.BodyComposition
import com.kvs.samsunghealthreporter.model.payload.BodyTemperature
import com.kvs.samsunghealthreporter.model.payload.Exercise
import com.kvs.samsunghealthreporter.model.payload.FloorsClimbed
import com.kvs.samsunghealthreporter.model.payload.HeartRate
import com.kvs.samsunghealthreporter.model.payload.Nutrition
import com.kvs.samsunghealthreporter.model.payload.Sleep
import com.kvs.samsunghealthreporter.model.payload.WaterIntake
import com.kvs.samsunghealthreporter.model.payload.WritableSample
import com.samsung.android.sdk.health.data.data.HealthDataPoint

internal val WritableSample.asOriginal: HealthDataPoint
    get() =
        when (this) {
            is HeartRate -> asOriginal()
            is BloodOxygen -> asOriginal()
            is BloodPressure -> asOriginal()
            is BloodGlucose -> asOriginal()
            is BodyComposition -> asOriginal()
            is BodyTemperature -> asOriginal()
            is Exercise -> asOriginal()
            is FloorsClimbed -> asOriginal()
            is Sleep -> asOriginal()
            is Nutrition -> asOriginal()
            is WaterIntake -> asOriginal()
        }

internal fun WritableSample.withClientDataId(clientDataId: String): WritableSample =
    when (this) {
        is HeartRate -> copy(clientDataId = clientDataId)
        is BloodOxygen -> copy(clientDataId = clientDataId)
        is BloodPressure -> copy(clientDataId = clientDataId)
        is BloodGlucose -> copy(clientDataId = clientDataId)
        is BodyComposition -> copy(clientDataId = clientDataId)
        is BodyTemperature -> copy(clientDataId = clientDataId)
        is Exercise -> copy(clientDataId = clientDataId)
        is FloorsClimbed -> copy(clientDataId = clientDataId)
        is Sleep -> copy(clientDataId = clientDataId)
        is Nutrition -> copy(clientDataId = clientDataId)
        is WaterIntake -> copy(clientDataId = clientDataId)
    }
