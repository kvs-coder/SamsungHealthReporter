package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.model.Permission
import com.kvs.samsunghealthreporter.model.type.AccessType
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.permission.Permission as OriginalPermission

internal val Permission.asOriginal: OriginalPermission
    get() = OriginalPermission.of(type.original, access.mapped())

private val OriginalPermission.key: Pair<String, String> get() = dataType.name to accessType.name

/** Maps granted SDK permissions back to the library permissions that were asked for. */
internal fun Set<Permission>.granted(by: Set<OriginalPermission>): Set<Permission> {
    val keys = by.map { it.key }.toSet()
    return filterTo(mutableSetOf()) { it.asOriginal.key in keys }
}

/** Builds the permissions for [read] and [write], rejecting write access to read-only types. */
internal fun permissionsOf(
    read: Set<HealthType>,
    write: Set<HealthType>,
): Set<Permission> {
    if (read.isEmpty() && write.isEmpty()) throw SamsungHealthException.InvalidValue("No types to authorize")
    write.firstOrNull { !it.isWritable }?.let {
        throw SamsungHealthException.InvalidType("${it.identifier} is read-only")
    }
    return read.map { Permission(it, AccessType.READ) }.toSet() + write.map { Permission(it, AccessType.WRITE) }
}
