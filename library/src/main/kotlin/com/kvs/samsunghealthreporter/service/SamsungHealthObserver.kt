package com.kvs.samsunghealthreporter.service

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.changeReadable
import com.kvs.samsunghealthreporter.decorator.sample
import com.kvs.samsunghealthreporter.decorator.sdkCall
import com.kvs.samsunghealthreporter.model.ChangeSet
import com.kvs.samsunghealthreporter.model.payload.Sample
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.ChangeType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.InstantTimeFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * **SamsungHealthObserver** class for observing Samsung Health changes.
 *
 * Samsung Health records every insert, update and delete with its time. Ask for the changes since your last sync
 * and persist [ChangeSet.until] for the next call.
 */
public class SamsungHealthObserver internal constructor(
    private val store: HealthDataStore,
    private val now: () -> Long = System::currentTimeMillis,
) {
    /**
     * Reads the changes of one type in a time window.
     *
     * @param type **HealthType** a type whose [HealthType.isObservable] is true
     * @param since **Long** window start, epoch milliseconds; usually the previous [ChangeSet.until]
     * @param until **Long** window end, epoch milliseconds. Now by default
     * @return **ChangeSet** the upserted samples and deleted uids
     * @throws SamsungHealthException.InvalidType when [type] is not observable
     * @throws SamsungHealthException.InvalidValue when [until] is before [since]
     * @throws SamsungHealthException.NotAuthorized when read permission is missing
     */
    public suspend fun changes(
        type: HealthType,
        since: Long,
        until: Long = now(),
    ): ChangeSet {
        if (!type.isObservable) throw SamsungHealthException.InvalidType("${type.identifier} is not observable")
        if (until < since) throw SamsungHealthException.InvalidValue("Invalid window: until $until is before $since")
        val upserted = mutableListOf<Sample>()
        val deleted = mutableListOf<String>()
        var pageToken: String? = null
        do {
            pageToken = readChangesPage(type, since, until, pageToken, upserted, deleted)
        } while (pageToken != null)
        return ChangeSet(type, since, until, upserted, deleted)
    }

    /** Reads one page of changes into [upserted] and [deleted] and returns the next page token. */
    private suspend fun readChangesPage(
        type: HealthType,
        since: Long,
        until: Long,
        pageToken: String?,
        upserted: MutableList<Sample>,
        deleted: MutableList<String>,
    ): String? {
        val response =
            sdkCall {
                val request =
                    type.changeReadable.changedDataRequestBuilder
                        .setChangeTimeFilter(
                            InstantTimeFilter.of(Instant.ofEpochMilli(since), Instant.ofEpochMilli(until)),
                        ).setPageToken(pageToken)
                        .build()
                store.readChanges(request)
            }
        response.dataList.forEach { change ->
            when (change.changeType) {
                ChangeType.UPSERT -> type.sampleOrNull(change.upsertDataPoint)?.let { upserted += it }
                ChangeType.DELETE -> change.deleteDataUid?.let { deleted += it }
            }
        }
        return response.pageToken
    }

    /** A point that can't be converted is skipped, like in reads. */
    private fun HealthType.sampleOrNull(point: HealthDataPoint): Sample? =
        try {
            sample(point)
        } catch (_: SamsungHealthException.InvalidValue) {
            null
        }

    /**
     * Polls the changes of one type. The flow emits only non-empty change sets, never completes on its own and
     * stops when its collector is cancelled.
     *
     * @param type **HealthType** a type whose [HealthType.isObservable] is true
     * @param since **Long** start of the first window, epoch milliseconds
     * @param interval **Duration** pause between polls. 1 minute by default
     * @return **Flow<ChangeSet>** cold flow of change sets; errors end it with a **SamsungHealthException**
     * @throws SamsungHealthException.InvalidType when [type] is not observable
     */
    public fun observe(
        type: HealthType,
        since: Long,
        interval: Duration = 1.minutes,
    ): Flow<ChangeSet> {
        if (!type.isObservable) throw SamsungHealthException.InvalidType("${type.identifier} is not observable")
        return flow {
            var from = since
            while (true) {
                val changeSet = changes(type, from)
                if (!changeSet.isEmpty) emit(changeSet)
                from = changeSet.until
                delay(interval)
            }
        }
    }
}
