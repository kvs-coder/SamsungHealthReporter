package com.kvs.samsunghealthreporter.manager

import com.kvs.samsunghealthreporter.SamsungHealthConnectionException

interface ConnectionListener {
    fun onConnected(manager: SamsungHealthManager)

    fun onConnectionFailed(
        exception: SamsungHealthConnectionException
    )

    fun onDisconnected()
}