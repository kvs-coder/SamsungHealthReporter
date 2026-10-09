package com.kvs.samsunghealthreporter.example.service

import android.app.Activity
import android.content.Context
import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.SamsungHealthReporter
import com.kvs.samsunghealthreporter.example.demo.DemoRow
import com.kvs.samsunghealthreporter.example.demo.DemoSection
import com.kvs.samsunghealthreporter.model.Permission
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.error.HealthDataException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Demos of **SamsungHealthManager** and the reporter's availability check. */
class ManagerDemos(
    private val context: Context,
    private val reporter: () -> SamsungHealthReporter,
    private val errors: DemoErrors,
) : DemoPerformer {
    private val read = HealthType.entries.toSet()
    private val write = HealthType.entries.filter { it.isWritable }.toSet()

    private val available =
        DemoRow("manager.available", "Is Samsung Health installed", "SamsungHealthReporter.isAvailable")
    private val request = DemoRow("manager.request", "Request every permission", "manager.requestPermissions")
    private val requestRead =
        DemoRow("manager.requestRead", "Request every read permission", "manager.requestPermissions(read)")
    private val requestEach =
        DemoRow(
            "manager.requestEach",
            "Request each permission alone, list the rejected ones",
            "manager.requestPermissions × 36",
        )
    private val granted = DemoRow("manager.granted", "Granted permissions", "manager.grantedPermissions")
    private val authorized = DemoRow("manager.authorized", "Is every permission granted", "manager.isAuthorized")
    private val resolve = DemoRow("manager.resolve", "Resolve the last Samsung Health error", "manager.resolve")
    private val localDevice = DemoRow("manager.localDevice", "This phone as a device", "manager.localDevice")
    private val devices = DemoRow("manager.devices", "Own devices", "manager.devices")
    private val device = DemoRow("manager.device", "Look up this phone by id", "manager.device")

    override val section =
        DemoSection(
            "Manager",
            listOf(
                available,
                request,
                requestRead,
                requestEach,
                granted,
                authorized,
                resolve,
                localDevice,
                devices,
                device,
            ),
        )

    override fun perform(
        row: DemoRow,
        activity: Activity,
    ): Flow<String> =
        flow {
            val output =
                when (row) {
                    available -> "Samsung Health installed: ${SamsungHealthReporter.isAvailable(context)}"
                    request -> {
                        val result = reporter().manager.requestPermissions(activity, read, write)
                        "Granted ${result.size} of ${read.size + write.size}:\n" +
                            result.joinToString("\n") { it.label }
                    }
                    requestRead -> {
                        val result = reporter().manager.requestPermissions(activity, read)
                        "Granted ${result.size} of ${read.size}:\n" + result.joinToString("\n") { it.label }
                    }
                    requestEach -> requestEach(activity)
                    granted -> {
                        val result = reporter().manager.grantedPermissions(read, write)
                        "Granted ${result.size} of ${read.size + write.size}:\n" +
                            result.joinToString("\n") { it.label }
                    }
                    authorized -> "Authorized for everything: ${reporter().manager.isAuthorized(read, write)}"
                    resolve -> {
                        val error = errors.lastResolvable
                        when {
                            error == null -> "No resolvable error so far"
                            reporter().manager.resolve(activity, error) -> "Started the fix for: ${error.message}"
                            else -> "Samsung Health offers no fix for: ${error.message}"
                        }
                    }
                    localDevice ->
                        reporter()
                            .manager
                            .localDevice()
                            .json
                            .pretty()
                    devices ->
                        reporter()
                            .manager
                            .devices()
                            .map { it.json }
                            .prettyList()
                    device -> {
                        val id = reporter().manager.localDevice().id
                        reporter()
                            .manager
                            .device(id)
                            ?.json
                            ?.pretty() ?: "No device with id $id"
                    }
                    else -> error("Unknown row ${row.id}")
                }
            emit(output)
        }

    /** Developer mode and partner approval allow only some permissions; this finds out which. */
    private suspend fun requestEach(activity: Activity): String {
        val lines =
            read.map { type ->
                "read  ${type.identifier}: " +
                    attempt { reporter().manager.requestPermissions(activity, setOf(type)) }
            } +
                write.map { type ->
                    "write ${type.identifier}: " +
                        attempt { reporter().manager.requestPermissions(activity, emptySet(), setOf(type)) }
                }
        return lines.joinToString("\n")
    }

    private suspend fun attempt(block: suspend () -> Set<Permission>): String =
        try {
            if (block().isEmpty()) "denied" else "granted"
        } catch (exception: SamsungHealthException) {
            val code = (exception.cause as? HealthDataException)?.errorCode ?: ""
            "${exception::class.simpleName} $code ${exception.message}"
        }

    private val Permission.label: String
        get() = "${type.identifier} ${access.name.lowercase()}"
}
