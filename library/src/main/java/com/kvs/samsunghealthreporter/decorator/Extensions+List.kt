package com.kvs.samsunghealthreporter.decorator

import com.google.gson.Gson

fun<T> List<T>.toJson(): String {
    return Gson().toJson(this)
}