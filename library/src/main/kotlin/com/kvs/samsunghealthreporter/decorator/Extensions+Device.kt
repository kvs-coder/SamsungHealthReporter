package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.model.Device
import com.samsung.android.sdk.health.data.device.AccessoryType
import com.samsung.android.sdk.health.data.device.DeviceGroup
import com.samsung.android.sdk.health.data.device.Device as OriginalDevice

internal val OriginalDevice.harmonized: Device
    get() =
        Device(
            id = id,
            type =
                when (val type = deviceType) {
                    is DeviceGroup -> type.name
                    is AccessoryType -> type.name
                },
            manufacturer = manufacturer,
            model = model,
            name = name,
        )
