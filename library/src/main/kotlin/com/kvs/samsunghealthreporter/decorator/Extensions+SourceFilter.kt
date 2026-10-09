package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.model.SourceFilter
import com.samsung.android.sdk.health.data.request.ReadSourceFilter

internal val SourceFilter.asOriginal: ReadSourceFilter
    get() =
        when (this) {
            is SourceFilter.App -> ReadSourceFilter.fromApplicationId(appId)
            SourceFilter.LocalDevice -> ReadSourceFilter.fromLocalDevice()
            SourceFilter.SamsungHealth -> ReadSourceFilter.fromPlatform()
        }
