package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.model.type.Aggregation
import kotlinx.serialization.Serializable

/**
 * **Aggregate** one aggregated value for a time bucket.
 *
 * @param aggregation **Aggregation** the operation
 * @param startTimestamp **Long** bucket start, epoch milliseconds
 * @param endTimestamp **Long** bucket end, epoch milliseconds
 * @param value **Double?** the value in [unit]; null when Samsung Health has no data for the bucket
 * @param unit **String** unit of [value]
 */
@Serializable
public data class Aggregate(
    val aggregation: Aggregation,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val value: Double?,
    val unit: String = aggregation.unit,
) : Payload {
    override val json: String get() = Companion.encode(this)

    /** Factory of **Aggregate** */
    public companion object : Payload.Factory<Aggregate>({ Aggregate.serializer() })
}
