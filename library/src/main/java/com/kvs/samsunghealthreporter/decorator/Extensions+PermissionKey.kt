package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.HealthType
import com.samsung.android.sdk.healthdata.HealthPermissionManager

val HealthPermissionManager.PermissionKey.parsed: HealthType
    get() =
        HealthType.initWith(this.dataType)
