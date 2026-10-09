package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.asJsonElement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal val payloadJson =
    Json {
        encodeDefaults = true
        explicitNulls = false
        ignoreUnknownKeys = true
    }

/**
 * A library value that can be encoded to JSON and created from a map, e.g. one sent by a Flutter plugin.
 *
 * Map keys and JSON names are the property names (or their `@SerialName`) and are part of the public contract.
 */
public interface Payload {
    /** **String** JSON representation of the payload */
    public val json: String

    /**
     * Creates payloads of type [T] from maps.
     *
     * @param serializer provides the **KSerializer** of [T]
     */
    public abstract class Factory<T>(
        private val serializer: () -> KSerializer<T>,
    ) {
        /**
         * Creates a payload from a map shaped like its JSON representation.
         *
         * @param map **Map<String, Any?>** with the payload's property names as keys
         * @return **T** the payload
         * @throws SamsungHealthException.InvalidValue when a required key is missing or has the wrong type
         */
        public fun make(from: Map<String, Any?>): T =
            try {
                payloadJson.decodeFromJsonElement(serializer(), from.asJsonElement)
            } catch (exception: SerializationException) {
                throw SamsungHealthException.InvalidValue("Invalid map: $from", exception)
            } catch (exception: IllegalArgumentException) {
                throw SamsungHealthException.InvalidValue("Invalid map: $from", exception)
            }

        /**
         * Creates payloads from maps, skipping the maps that can't be converted.
         *
         * @param maps **List<Map<String, Any?>>** maps shaped like the payload's JSON representation
         * @return **List<T>** the payloads that could be created
         */
        public fun collect(from: List<Map<String, Any?>>): List<T> =
            from.mapNotNull { map ->
                try {
                    make(map)
                } catch (_: SamsungHealthException.InvalidValue) {
                    null
                }
            }

        /**
         * Decodes a payload from its JSON representation.
         *
         * @param json **String** JSON produced by [Payload.json]
         * @return **T** the payload
         * @throws SamsungHealthException.InvalidValue when the JSON doesn't describe a payload
         */
        public fun decode(json: String): T =
            try {
                payloadJson.decodeFromString(serializer(), json)
            } catch (exception: SerializationException) {
                throw SamsungHealthException.InvalidValue("Invalid json: $json", exception)
            } catch (exception: IllegalArgumentException) {
                throw SamsungHealthException.InvalidValue("Invalid json: $json", exception)
            }

        internal fun encode(value: T): String = payloadJson.encodeToString(serializer(), value)
    }
}
