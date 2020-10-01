package com.kvs.samsunghealthreporter

interface SamsungHealthConnectionListener {
    fun onConnected(reader: SamsungHealthReader?, writer: SamsungHealthWriter?)

    fun onConnectionFailed(
        exception: SamsungHealthConnectionException
    )

    fun onDisconnected()
}