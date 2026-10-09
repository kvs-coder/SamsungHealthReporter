package com.kvs.samsunghealthreporter.service

import android.app.Activity
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.decorator.granted
import com.kvs.samsunghealthreporter.decorator.harmonized
import com.kvs.samsunghealthreporter.decorator.permissionsOf
import com.kvs.samsunghealthreporter.decorator.sdkCall
import com.kvs.samsunghealthreporter.model.Device
import com.kvs.samsunghealthreporter.model.Permission
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException

/** **SamsungHealthManager** class for Samsung Health permissions, error resolution and devices */
public class SamsungHealthManager internal constructor(
    private val store: HealthDataStore,
) {
    /**
     * Shows the Samsung Health permission screen for the permissions not granted yet.
     *
     * @param activity **Activity** the activity that shows the screen
     * @param read **Set<HealthType>** types to read
     * @param write **Set<HealthType>** types to write; each must be [HealthType.isWritable]. None by default
     * @return **Set<Permission>** the asked-for permissions that are granted afterwards
     * @throws SamsungHealthException.InvalidType when a write type is read-only
     * @throws SamsungHealthException.InvalidValue when both sets are empty
     * @throws SamsungHealthException.Resolvable when Samsung Health must be installed, updated or set up first
     */
    public suspend fun requestPermissions(
        activity: Activity,
        read: Set<HealthType>,
        write: Set<HealthType> = emptySet(),
    ): Set<Permission> {
        val permissions = permissionsOf(read, write)
        val granted =
            sdkCall { store.requestPermissions(permissions.mapTo(mutableSetOf()) { it.asOriginal }, activity) }
        return permissions.granted(granted)
    }

    /**
     * Checks which permissions are granted, without showing any screen.
     *
     * @param read **Set<HealthType>** types to read
     * @param write **Set<HealthType>** types to write; each must be [HealthType.isWritable]. None by default
     * @return **Set<Permission>** the asked-for permissions that are granted
     * @throws SamsungHealthException.InvalidType when a write type is read-only
     * @throws SamsungHealthException.InvalidValue when both sets are empty
     */
    public suspend fun grantedPermissions(
        read: Set<HealthType>,
        write: Set<HealthType> = emptySet(),
    ): Set<Permission> {
        val permissions = permissionsOf(read, write)
        val granted = sdkCall { store.getGrantedPermissions(permissions.mapTo(mutableSetOf()) { it.asOriginal }) }
        return permissions.granted(granted)
    }

    /**
     * Checks that every asked-for permission is granted.
     *
     * @param read **Set<HealthType>** types to read
     * @param write **Set<HealthType>** types to write. None by default
     * @return **Boolean** true when all are granted
     */
    public suspend fun isAuthorized(
        read: Set<HealthType>,
        write: Set<HealthType> = emptySet(),
    ): Boolean = grantedPermissions(read, write).size == read.size + write.size

    /**
     * Starts the fix for a **SamsungHealthException.Resolvable**, e.g. opens the store to install Samsung Health.
     *
     * @param activity **Activity** the activity that starts the fix
     * @param exception **SamsungHealthException.Resolvable** the exception a call threw
     * @return **Boolean** false when Samsung Health offers no fix for this problem
     */
    public fun resolve(
        activity: Activity,
        exception: SamsungHealthException.Resolvable,
    ): Boolean {
        val original = exception.cause as? ResolvablePlatformException ?: return false
        if (!original.hasResolution) return false
        original.resolve(activity)
        return true
    }

    /**
     * Reads this phone as a Samsung Health device.
     *
     * @return **Device** the local device
     * @throws SamsungHealthException.Platform when Samsung Health can't be reached
     */
    public suspend fun localDevice(): Device = sdkCall { store.getDeviceManager().getLocalDevice() }.harmonized

    /**
     * Reads the user's own devices registered in Samsung Health (phone, watch, ring, accessories).
     *
     * @return **List<Device>** the devices
     * @throws SamsungHealthException.Platform when Samsung Health can't be reached
     */
    public suspend fun devices(): List<Device> =
        sdkCall { store.getDeviceManager().getOwnDevices() }.map { it.harmonized }

    /**
     * Finds a device by id, e.g. a **DataSource.deviceId**.
     *
     * @param id **String** Samsung Health device id
     * @return **Device?** the device, or null when Samsung Health doesn't know it
     * @throws SamsungHealthException.Platform when Samsung Health can't be reached
     */
    public suspend fun device(id: String): Device? = sdkCall { store.getDeviceManager().getDevice(id) }?.harmonized
}
