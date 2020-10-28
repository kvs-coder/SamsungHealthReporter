package com.kvs.samsunghealthreporter.model

import com.google.gson.Gson

interface Common {
    val type: String
    //val startTime: Long
    //val timeOffset: Long
    //val uuid: String
    //val createTime: Long
    //val updateTime: Long
    //val packageName: String
    //val deviceUuid: String?
    //val custom: String?
    val json: String get() = Gson().toJson(this)
}