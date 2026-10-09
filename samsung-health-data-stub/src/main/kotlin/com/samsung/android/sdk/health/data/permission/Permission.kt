package com.samsung.android.sdk.health.data.permission

import com.samsung.android.sdk.health.data.request.DataType

enum class AccessType { READ, WRITE }

/** Like the SDK: a final class with value equality and no copy(). */
class Permission private constructor(
    val dataType: DataType,
    val accessType: AccessType,
) {
    override fun equals(other: Any?): Boolean =
        other is Permission && other.dataType.name == dataType.name && other.accessType == accessType

    override fun hashCode(): Int = 31 * dataType.name.hashCode() + accessType.hashCode()

    companion object {
        @JvmStatic
        fun of(
            dataType: DataType,
            accessType: AccessType,
        ): Permission = Permission(dataType, accessType)
    }
}
