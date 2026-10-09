package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException

/** Maps between a library enum and the Samsung Health Data SDK enum that has the same entry names. */
internal inline fun <reified T : Enum<T>> Enum<*>.mapped(): T =
    enumValues<T>().firstOrNull { it.name == name }
        ?: throw SamsungHealthException.InvalidValue("Invalid ${T::class.simpleName}: $name")
