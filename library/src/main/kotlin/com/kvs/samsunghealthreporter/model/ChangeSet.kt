package com.kvs.samsunghealthreporter.model

import com.kvs.samsunghealthreporter.model.payload.Sample
import com.kvs.samsunghealthreporter.model.type.HealthType
import kotlinx.serialization.Serializable

/**
 * **ChangeSet** the changes Samsung Health recorded for one type in a time window.
 *
 * Persist [until] and pass it as `since` to the next **SamsungHealthObserver.changes** call.
 *
 * @param type **HealthType** the observed type
 * @param since **Long** window start, epoch milliseconds
 * @param until **Long** window end, epoch milliseconds
 * @param upserted **List<Sample>** inserted or updated data points
 * @param deletedUids **List<String>** uids of deleted data points
 */
@Serializable
public data class ChangeSet(
    val type: HealthType,
    val since: Long,
    val until: Long,
    val upserted: List<Sample>,
    val deletedUids: List<String>,
) : Payload {
    /** **Boolean** true when nothing changed */
    val isEmpty: Boolean get() = upserted.isEmpty() && deletedUids.isEmpty()

    override val json: String get() = Companion.encode(this)

    /** Factory of **ChangeSet** */
    public companion object : Payload.Factory<ChangeSet>({ ChangeSet.serializer() })
}
