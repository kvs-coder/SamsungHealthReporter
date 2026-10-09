package com.kvs.samsunghealthreporter.service

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.decorator.sdkCall
import com.kvs.samsunghealthreporter.decorator.withClientDataId
import com.kvs.samsunghealthreporter.decorator.writeable
import com.kvs.samsunghealthreporter.model.payload.Sample
import com.kvs.samsunghealthreporter.model.payload.WritableSample
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.request.IdFilter
import java.util.UUID

/**
 * **SamsungHealthWriter** class for Samsung Health writing operations.
 *
 * Writes of one type are atomic: if one data point fails, none is written. Apps can only update and delete data
 * they inserted themselves. Outside developer mode, writing needs Samsung partner approval.
 */
public class SamsungHealthWriter internal constructor(
    private val store: HealthDataStore,
    private val makeClientDataId: () -> String = { UUID.randomUUID().toString() },
) {
    /**
     * Inserts samples. A sample without a client data id gets a random UUID, so it can be updated or deleted
     * later without reading it back.
     *
     * @param samples **List<WritableSample>** the samples, of one or more types
     * @return **List<WritableSample>** the inserted samples, each with its client data id
     * @throws SamsungHealthException.InvalidValue when Samsung Health rejects a sample
     * @throws SamsungHealthException.NotAuthorized when write permission is missing
     */
    public suspend fun insert(samples: List<WritableSample>): List<WritableSample> {
        val identified = samples.map { if (it.clientDataId != null) it else it.withClientDataId(makeClientDataId()) }
        identified.groupBy { it.healthType }.forEach { (type, group) ->
            val builder = type.writeable.insertDataRequestBuilder
            group.forEach { builder.addData(it.asOriginal) }
            val request = builder.build()
            sdkCall { store.insertData(request) }
        }
        return identified
    }

    /**
     * Inserts one sample.
     *
     * @param sample **WritableSample** the sample
     * @return **WritableSample** the inserted sample with its client data id
     * @throws SamsungHealthException.InvalidValue when Samsung Health rejects the sample
     * @throws SamsungHealthException.NotAuthorized when write permission is missing
     */
    public suspend fun insert(sample: WritableSample): WritableSample = insert(listOf(sample)).first()

    /**
     * Replaces stored samples, found by [Sample.uid] or, without one, by [Sample.clientDataId].
     *
     * @param samples **List<WritableSample>** the new values of samples your app inserted
     * @throws SamsungHealthException.InvalidValue when a sample has neither uid nor client data id
     * @throws SamsungHealthException.NotAuthorized when write permission is missing or another app owns a sample
     */
    public suspend fun update(samples: List<WritableSample>) {
        samples.groupBy { it.healthType }.forEach { (type, group) ->
            sdkCall {
                val builder = type.writeable.updateDataRequestBuilder
                group.forEach { sample ->
                    val uid = sample.uid
                    val clientDataId = sample.clientDataId
                    when {
                        uid != null -> builder.addDataWithUid(uid, sample.asOriginal)
                        clientDataId != null -> builder.addDataWithClientDataId(clientDataId, sample.asOriginal)
                        else -> throw SamsungHealthException.InvalidValue(
                            "Can't update a sample without uid or clientDataId",
                        )
                    }
                }
                store.updateData(builder.build())
            }
        }
    }

    /**
     * Replaces one stored sample.
     *
     * @param sample **WritableSample** the new value of a sample your app inserted
     * @throws SamsungHealthException.InvalidValue when the sample has neither uid nor client data id
     * @throws SamsungHealthException.NotAuthorized when write permission is missing or another app owns it
     */
    public suspend fun update(sample: WritableSample): Unit = update(listOf(sample))

    /**
     * Deletes stored data points by uid.
     *
     * @param type **HealthType** a type whose [HealthType.isWritable] is true
     * @param uids **List<String>** the uids, at least one
     * @throws SamsungHealthException.InvalidType when [type] is read-only
     * @throws SamsungHealthException.InvalidValue when [uids] is empty
     * @throws SamsungHealthException.NotAuthorized when write permission is missing or another app owns a point
     */
    public suspend fun delete(
        type: HealthType,
        uids: List<String>,
    ) {
        delete(type, uids) { builder, uid -> builder.addDataUid(uid) }
    }

    /**
     * Deletes stored data points by client data id.
     *
     * @param type **HealthType** a type whose [HealthType.isWritable] is true
     * @param clientDataIds **List<String>** the client data ids, at least one
     * @throws SamsungHealthException.InvalidType when [type] is read-only
     * @throws SamsungHealthException.InvalidValue when [clientDataIds] is empty
     * @throws SamsungHealthException.NotAuthorized when write permission is missing or another app owns a point
     */
    public suspend fun deleteByClientDataIds(
        type: HealthType,
        clientDataIds: List<String>,
    ) {
        delete(type, clientDataIds) { builder, id -> builder.addClientDataId(id) }
    }

    /**
     * Deletes one stored sample, found by [Sample.uid] or, without one, by [Sample.clientDataId].
     *
     * @param sample **Sample** a sample your app inserted
     * @throws SamsungHealthException.InvalidValue when the sample has neither uid nor client data id
     * @throws SamsungHealthException.NotAuthorized when write permission is missing or another app owns it
     */
    public suspend fun delete(sample: Sample) {
        val uid = sample.uid
        val clientDataId = sample.clientDataId
        when {
            uid != null -> delete(sample.healthType, listOf(uid))
            clientDataId != null -> deleteByClientDataIds(sample.healthType, listOf(clientDataId))
            else -> throw SamsungHealthException.InvalidValue("Can't delete a sample without uid or clientDataId")
        }
    }

    private suspend fun delete(
        type: HealthType,
        ids: List<String>,
        add: (IdFilter.Builder, String) -> IdFilter.Builder,
    ) {
        if (ids.isEmpty()) throw SamsungHealthException.InvalidValue("No ids to delete")
        sdkCall {
            val filter = ids.fold(IdFilter.builder(), add).build()
            store.deleteData(
                type.writeable.deleteDataRequestBuilder
                    .setIdFilter(filter)
                    .build(),
            )
        }
    }
}
