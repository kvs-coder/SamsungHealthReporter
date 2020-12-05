package com.kvs.samsunghealthreporter.manager

import java.lang.Exception

interface SamsungHealthPermissionListener {
    fun onAcquired(success: Boolean)

    fun onException(exception: Exception)
}