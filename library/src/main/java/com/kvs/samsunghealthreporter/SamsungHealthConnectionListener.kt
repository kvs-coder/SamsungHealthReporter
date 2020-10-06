package com.kvs.samsunghealthreporter

interface SamsungHealthConnectionListener {
    fun onConnected(
        manager: SamsungHealthManager?
    )

    fun onConnectionFailed(
        exception: SamsungHealthConnectionException
    )

    fun onDisconnected()
}