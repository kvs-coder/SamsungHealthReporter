package com.samsung.android.sdk.health.data.permission

import com.samsung.android.sdk.health.data.request.DataType

enum class AccessType { READ, WRITE }

data class Permission private constructor(
    val dataType: DataType,
    val accessType: AccessType,
) {
    companion object {
        @JvmStatic
        fun of(
            dataType: DataType,
            accessType: AccessType,
        ): Permission = Permission(dataType, accessType)
    }
}
