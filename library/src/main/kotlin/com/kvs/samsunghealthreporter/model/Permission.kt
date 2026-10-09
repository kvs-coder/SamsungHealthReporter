package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.model.type.AccessType
import com.kvs.samsunghealthreporter.model.type.HealthType
import kotlinx.serialization.Serializable

/**
 * **Permission** access to one [type].
 *
 * @param type **HealthType** the data type
 * @param access **AccessType** read or write
 */
@Serializable
public data class Permission(
    val type: HealthType,
    val access: AccessType,
)
