package com.kvs.samsunghealthreporter.model

import kotlinx.serialization.Serializable

/**
 * **Device** a device registered in Samsung Health.
 *
 * @param id **String** Samsung Health device id, as in **DataSource.deviceId**
 * @param type **String** device group (`MOBILE`, `WATCH`, `RING`, `BAND`, `ACCESSORY`, `OTHER`) or accessory type
 * (`WEIGHT_SCALE`, `HEART_RATE_MONITOR`, ...)
 * @param manufacturer **String?** manufacturer
 * @param model **String?** model
 * @param name **String?** display name
 */
@Serializable
public data class Device(
    val id: String,
    val type: String,
    val manufacturer: String? = null,
    val model: String? = null,
    val name: String? = null,
) : Payload {
    override val json: String get() = Companion.encode(this)

    /** Factory of **Device** */
    public companion object : Payload.Factory<Device>({ Device.serializer() })
}
