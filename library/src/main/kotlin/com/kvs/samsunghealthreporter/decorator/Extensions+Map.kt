package com.kvs.samsunghealthreporter.decorator

import com.kvs.samsunghealthreporter.SamsungHealthException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal val Map<String, Any?>.asJsonElement: JsonElement
    get() = JsonObject(mapValues { (_, value) -> value.asJsonElement })

private val Any?.asJsonElement: JsonElement
    get() =
        when (this) {
            null -> JsonNull
            is JsonElement -> this
            is String -> JsonPrimitive(this)
            is Number -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is Enum<*> -> JsonPrimitive(name)
            is Map<*, *> -> JsonObject(entries.associate { (key, value) -> key.toString() to value.asJsonElement })
            is Iterable<*> -> JsonArray(map { it.asJsonElement })
            is Array<*> -> JsonArray(map { it.asJsonElement })
            else -> throw SamsungHealthException.InvalidValue("Unsupported map value: $this")
        }
