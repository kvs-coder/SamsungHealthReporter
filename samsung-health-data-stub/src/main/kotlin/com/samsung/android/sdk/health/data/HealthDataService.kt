package com.samsung.android.sdk.health.data

import android.app.Activity
import android.content.Context
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.data.Change
import com.samsung.android.sdk.health.data.data.DataPoint
import com.samsung.android.sdk.health.data.device.Device
import com.samsung.android.sdk.health.data.device.DeviceType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.AggregateRequest
import com.samsung.android.sdk.health.data.request.ChangedDataRequest
import com.samsung.android.sdk.health.data.request.DeleteDataRequest
import com.samsung.android.sdk.health.data.request.InsertDataRequest
import com.samsung.android.sdk.health.data.request.ReadDataRequest
import com.samsung.android.sdk.health.data.request.UpdateDataRequest
import com.samsung.android.sdk.health.data.response.DataResponse

object HealthDataService {
    @JvmStatic
    fun getStore(context: Context): HealthDataStore = throw UnsupportedOperationException("SDK stub")
}

interface HealthDataStore {
    suspend fun <T : DataPoint> readData(request: ReadDataRequest<T>): DataResponse<T>

    suspend fun <T : Any> aggregateData(request: AggregateRequest<T>): DataResponse<AggregatedData<T>>

    suspend fun <T : DataPoint> insertData(request: InsertDataRequest<T>)

    suspend fun <T : DataPoint> updateData(request: UpdateDataRequest<T>)

    suspend fun deleteData(request: DeleteDataRequest)

    suspend fun <T : DataPoint> readChanges(request: ChangedDataRequest<T>): DataResponse<Change<T>>

    suspend fun getGrantedPermissions(permissions: Set<Permission>): Set<Permission>

    suspend fun requestPermissions(
        permissions: Set<Permission>,
        activity: Activity,
    ): Set<Permission>

    fun getDeviceManager(): DeviceManager
}

interface DeviceManager {
    suspend fun getLocalDevice(): Device

    suspend fun getOwnDevices(): List<Device>

    suspend fun getDevices(deviceType: DeviceType): List<Device>

    suspend fun getDevice(deviceId: String): Device?
}
