package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthType
import com.samsung.android.sdk.healthdata.HealthPermissionManager

val HealthPermissionManager.PermissionKey.parsed: SamsungHealthType
    get() =
        SamsungHealthType.initWith(this.dataType)
