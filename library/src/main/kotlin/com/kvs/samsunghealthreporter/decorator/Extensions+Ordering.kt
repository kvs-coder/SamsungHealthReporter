package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.model.Ordering
import com.samsung.android.sdk.health.data.request.Ordering as OriginalOrdering

internal val Ordering.asOriginal: OriginalOrdering
    get() =
        when (this) {
            Ordering.ASCENDING -> OriginalOrdering.ASC
            Ordering.DESCENDING -> OriginalOrdering.DESC
        }
