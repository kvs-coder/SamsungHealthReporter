package com.kvs.samsunghealthreporter

interface SamsungHealthConnectionListener {
    fun onConnected()

    fun onConnectionFailed(exception: SamsungHealthConnectionException)

    fun onDisconnected()
}