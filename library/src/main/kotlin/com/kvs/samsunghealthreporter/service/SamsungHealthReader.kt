package com.kvs.samsunghealthreporter.service

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.asLocalDateFilter
import com.kvs.samsunghealthreporter.decorator.asLocalTimeFilter
import com.kvs.samsunghealthreporter.decorator.asOriginal
import com.kvs.samsunghealthreporter.decorator.collect
import com.kvs.samsunghealthreporter.decorator.dualTimeReadable
import com.kvs.samsunghealthreporter.decorator.sdkCall
import com.kvs.samsunghealthreporter.model.Ordering
import com.kvs.samsunghealthreporter.model.Page
import com.kvs.samsunghealthreporter.model.SourceFilter
import com.kvs.samsunghealthreporter.model.TimeRange
import com.kvs.samsunghealthreporter.model.payload.Sample
import com.kvs.samsunghealthreporter.model.payload.UserProfile
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.ReadDataRequest

/** **SamsungHealthReader** class for Samsung Health reading operations */
public class SamsungHealthReader internal constructor(
    internal val store: HealthDataStore,
) {
    /**
     * Reads the data points of one type.
     *
     * @param type **HealthType** a type whose [HealthType.isReadable] is true
     * @param range **TimeRange** local time range
     * @param ordering **Ordering** by start time. [Ordering.DESCENDING] by default
     * @param limit **Int?** maximum number of samples. No limit by default
     * @param source **SourceFilter?** only data from this source. All sources by default
     * @return **List<Sample>** the samples, empty when there is no data
     * @throws SamsungHealthException.InvalidType when [type] is not readable
     * @throws SamsungHealthException.InvalidValue when [limit] is less than 1
     * @throws SamsungHealthException.NotAuthorized when read permission is missing
     */
    public suspend fun read(
        type: HealthType,
        range: TimeRange,
        ordering: Ordering = Ordering.DESCENDING,
        limit: Int? = null,
        source: SourceFilter? = null,
    ): List<Sample> {
        if (limit != null && limit < 1) throw SamsungHealthException.InvalidValue("Invalid limit: $limit")
        val samples = mutableListOf<Sample>()
        var pageToken: String? = null
        do {
            val page = readPage(type, range, DEFAULT_PAGE_SIZE, pageToken, ordering, source)
            samples += page.items
            pageToken = page.nextPageToken
        } while (pageToken != null && (limit == null || samples.size < limit))
        return if (limit == null) samples else samples.take(limit)
    }

    /**
     * Reads one page of data points of one type.
     *
     * @param type **HealthType** a type whose [HealthType.isReadable] is true
     * @param range **TimeRange** local time range
     * @param pageSize **Int** maximum number of samples in the page. 100 by default
     * @param pageToken **String?** [Page.nextPageToken] of the previous page. First page by default
     * @param ordering **Ordering** by start time. [Ordering.DESCENDING] by default
     * @param source **SourceFilter?** only data from this source. All sources by default
     * @return **Page<Sample>** the samples of the page and the token of the next one
     * @throws SamsungHealthException.InvalidType when [type] is not readable
     * @throws SamsungHealthException.InvalidValue when [pageSize] is less than 1
     * @throws SamsungHealthException.NotAuthorized when read permission is missing
     */
    public suspend fun readPage(
        type: HealthType,
        range: TimeRange,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        pageToken: String? = null,
        ordering: Ordering = Ordering.DESCENDING,
        source: SourceFilter? = null,
    ): Page<Sample> {
        if (!type.isReadable) throw SamsungHealthException.InvalidType("${type.identifier} is not readable")
        if (pageSize < 1) throw SamsungHealthException.InvalidValue("Invalid pageSize: $pageSize")
        val response = sdkCall { store.readData(request(type, range, pageSize, pageToken, ordering, source)) }
        return Page(type.collect(response.dataList), response.pageToken)
    }

    /**
     * Reads the user's Samsung Health profile. Needs read permission for [HealthType.USER_PROFILE].
     *
     * @return **UserProfile?** the profile, or null when Samsung Health has none
     * @throws SamsungHealthException.NotAuthorized when read permission is missing
     */
    public suspend fun userProfile(): UserProfile? {
        val response = sdkCall { store.readData(DataTypes.USER_PROFILE.readDataRequestBuilder.build()) }
        return response.dataList.firstOrNull()?.let(UserProfile::from)
    }

    private fun request(
        type: HealthType,
        range: TimeRange,
        pageSize: Int,
        pageToken: String?,
        ordering: Ordering,
        source: SourceFilter?,
    ): ReadDataRequest<HealthDataPoint> =
        if (type == HealthType.ENERGY_SCORE) {
            val builder =
                DataTypes.ENERGY_SCORE.readDataRequestBuilder
                    .setLocalDateFilter(range.asLocalDateFilter)
                    .setOrdering(ordering.asOriginal)
                    .setPageSize(pageSize)
                    .setPageToken(pageToken)
            source?.let { builder.setSourceFilter(it.asOriginal) }
            builder.build()
        } else {
            val builder =
                type.dualTimeReadable.readDataRequestBuilder
                    .setLocalTimeFilter(range.asLocalTimeFilter)
                    .setOrdering(ordering.asOriginal)
                    .setPageSize(pageSize)
                    .setPageToken(pageToken)
            source?.let { builder.setSourceFilter(it.asOriginal) }
            builder.build()
        }

    private companion object {
        const val DEFAULT_PAGE_SIZE = 100
    }
}
