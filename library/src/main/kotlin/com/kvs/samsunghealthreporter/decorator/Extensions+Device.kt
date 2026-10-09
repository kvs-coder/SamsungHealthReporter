package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.model.Device
import com.samsung.android.sdk.health.data.device.Device as OriginalDevice

internal val OriginalDevice.harmonized: Device
    get() =
        Device(
            id = id,
            type = (deviceType as? Enum<*>)?.name ?: deviceType.toString(),
            manufacturer = manufacturer,
            model = model,
            name = name,
        )
