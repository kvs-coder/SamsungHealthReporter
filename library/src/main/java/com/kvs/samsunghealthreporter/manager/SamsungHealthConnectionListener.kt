package com.kvs.samsunghealthreporter.manager

import com.kvs.samsunghealthreporter.SamsungHealthConnectionException

interface SamsungHealthConnectionListener {
    fun onConnected(manager: SamsungHealthManager)

    fun onConnectionFailed(
        exception: SamsungHealthConnectionException
    )

    fun onDisconnected()
}