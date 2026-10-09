package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/** **AccessType** what a permission allows */
@Serializable
public enum class AccessType {
    /** read, aggregate and read changes */
    READ,

    /** insert, update and delete */
    WRITE,
}
