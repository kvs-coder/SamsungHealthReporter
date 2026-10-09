package com.samsung.android.sdk.health.data.request

import com.samsung.android.sdk.health.data.data.DataPoint
import com.samsung.android.sdk.health.data.data.UserDataPoint
import com.samsung.android.sdk.health.data.device.DeviceType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

enum class Ordering { ASC, DESC }

enum class LocalTimeGroupUnit { MINUTELY, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY }

enum class LocalDateGroupUnit { DAILY, WEEKLY, MONTHLY, YEARLY }

class LocalTimeFilter private constructor(
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
) {
    companion object {
        @JvmStatic
        fun of(
            startTime: LocalDateTime?,
            endTime: LocalDateTime?,
        ): LocalTimeFilter = LocalTimeFilter(startTime, endTime)
    }
}

class InstantTimeFilter private constructor(
    val startTime: Instant?,
    val endTime: Instant?,
) {
    companion object {
        @JvmStatic
        fun of(
            startTime: Instant?,
            endTime: Instant?,
        ): InstantTimeFilter = InstantTimeFilter(startTime, endTime)
    }
}

class LocalDateFilter private constructor(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
) {
    companion object {
        @JvmStatic
        fun of(
            startDate: LocalDate?,
            endDate: LocalDate?,
        ): LocalDateFilter = LocalDateFilter(startDate, endDate)
    }
}

class LocalTimeGroup private constructor(
    val timeGroupUnit: LocalTimeGroupUnit,
    val multiplier: Int,
) {
    companion object {
        @JvmStatic
        fun of(
            timeGroupUnit: LocalTimeGroupUnit,
            multiplier: Int,
        ): LocalTimeGroup = LocalTimeGroup(timeGroupUnit, multiplier)
    }
}

class LocalDateGroup private constructor(
    val dateGroupUnit: LocalDateGroupUnit,
    val multiplier: Int,
) {
    companion object {
        fun of(
            dateGroupUnit: LocalDateGroupUnit,
            multiplier: Int,
        ): LocalDateGroup = LocalDateGroup(dateGroupUnit, multiplier)
    }
}

class IdFilter private constructor(
    val dataUids: List<String>,
    val clientDataIds: List<String>,
) {
    class Builder {
        private val dataUids = mutableListOf<String>()
        private val clientDataIds = mutableListOf<String>()

        fun addDataUid(dataUid: String): Builder = apply { dataUids += dataUid }

        fun addClientDataId(clientDataId: String): Builder = apply { clientDataIds += clientDataId }

        fun build(): IdFilter = IdFilter(dataUids, clientDataIds)
    }

    companion object {
        @JvmStatic
        fun fromDataUid(dataUid: String): IdFilter = IdFilter(listOf(dataUid), emptyList())

        @JvmStatic
        fun fromClientDataId(clientDataId: String): IdFilter = IdFilter(emptyList(), listOf(clientDataId))

        @JvmStatic
        fun builder(): Builder = Builder()
    }
}

class ReadSourceFilter private constructor(
    val appId: String?,
    val deviceType: DeviceType?,
    val isLocalDevice: Boolean,
) {
    companion object {
        @JvmStatic
        fun fromApplicationId(appId: String): ReadSourceFilter = ReadSourceFilter(appId, null, false)

        @JvmStatic
        fun fromDeviceType(type: DeviceType): ReadSourceFilter = ReadSourceFilter(null, type, false)

        @JvmStatic
        fun fromLocalDevice(): ReadSourceFilter = ReadSourceFilter(null, null, true)

        @JvmStatic
        fun fromPlatform(): ReadSourceFilter = ReadSourceFilter(PLATFORM, null, false)

        @JvmStatic
        fun of(
            appId: String,
            type: DeviceType,
        ): ReadSourceFilter = ReadSourceFilter(appId, type, false)

        private const val PLATFORM = "com.sec.android.app.shealth"
    }
}

class AggregateSourceFilter private constructor() {
    companion object {
        @JvmStatic
        fun fromPlatform(): AggregateSourceFilter = AggregateSourceFilter()
    }
}

/** Stub only: the state every request builder collects, so tests can inspect it. */
class StubQuery {
    var localTimeFilter: LocalTimeFilter? = null
    var instantTimeFilter: InstantTimeFilter? = null
    var localDateFilter: LocalDateFilter? = null
    var localTimeGroup: LocalTimeGroup? = null
    var localDateGroup: LocalDateGroup? = null
    var idFilter: IdFilter? = null
    var limit: Int? = null
    var ordering: Ordering? = null
    var pageSize: Int? = null
    var pageToken: String? = null
    var readSourceFilter: ReadSourceFilter? = null
    var aggregateSourceFilter: AggregateSourceFilter? = null
    var allData: Boolean = false
}

class ReadDataRequest<T : DataPoint>(
    val dataType: DataType,
    val stub: StubQuery,
) {
    interface Builder<T : DataPoint> {
        fun build(): ReadDataRequest<T>
    }

    class DualTimeBuilder<T : DataPoint>(
        private val dataType: DataType,
    ) : Builder<T> {
        private val query = StubQuery()

        override fun build(): ReadDataRequest<T> = ReadDataRequest(dataType, query)

        fun setIdFilter(idFilter: IdFilter): DualTimeBuilder<T> = apply { query.idFilter = idFilter }

        fun setInstantTimeFilter(instantTimeFilter: InstantTimeFilter): DualTimeBuilder<T> =
            apply { query.instantTimeFilter = instantTimeFilter }

        fun setLimit(limit: Int): DualTimeBuilder<T> = apply { query.limit = limit }

        fun setLocalTimeFilter(localTimeFilter: LocalTimeFilter): DualTimeBuilder<T> =
            apply {
                query.localTimeFilter =
                    localTimeFilter
            }

        fun setOrdering(ordering: Ordering): DualTimeBuilder<T> = apply { query.ordering = ordering }

        fun setPageSize(pageSize: Int): DualTimeBuilder<T> = apply { query.pageSize = pageSize }

        fun setPageToken(pageToken: String?): DualTimeBuilder<T> = apply { query.pageToken = pageToken }

        fun setSourceFilter(sourceFilter: ReadSourceFilter): DualTimeBuilder<T> =
            apply {
                query.readSourceFilter =
                    sourceFilter
            }
    }

    class LocalDateBuilder<T : DataPoint>(
        private val dataType: DataType,
    ) : Builder<T> {
        private val query = StubQuery()

        override fun build(): ReadDataRequest<T> = ReadDataRequest(dataType, query)

        fun setIdFilter(idFilter: IdFilter): LocalDateBuilder<T> = apply { query.idFilter = idFilter }

        fun setLimit(limit: Int): LocalDateBuilder<T> = apply { query.limit = limit }

        fun setLocalDateFilter(localDateFilter: LocalDateFilter): LocalDateBuilder<T> =
            apply {
                query.localDateFilter =
                    localDateFilter
            }

        fun setOrdering(ordering: Ordering): LocalDateBuilder<T> = apply { query.ordering = ordering }

        fun setPageSize(pageSize: Int): LocalDateBuilder<T> = apply { query.pageSize = pageSize }

        fun setPageToken(pageToken: String?): LocalDateBuilder<T> = apply { query.pageToken = pageToken }

        fun setSourceFilter(sourceFilter: ReadSourceFilter): LocalDateBuilder<T> =
            apply {
                query.readSourceFilter =
                    sourceFilter
            }
    }

    class UserProfileBuilder(
        private val dataType: DataType,
    ) : Builder<UserDataPoint> {
        override fun build(): ReadDataRequest<UserDataPoint> = ReadDataRequest(dataType, StubQuery())
    }
}

class AggregateRequest<T : Any>(
    val name: String,
    val stub: StubQuery,
) {
    interface Builder<T : Any> {
        fun build(): AggregateRequest<T>
    }

    class LocalTimeBuilder<T : Any>(
        private val name: String,
    ) : Builder<T> {
        private val query = StubQuery()

        override fun build(): AggregateRequest<T> = AggregateRequest(name, query)

        fun setLocalTimeFilter(localTimeFilter: LocalTimeFilter): LocalTimeBuilder<T> =
            apply {
                query.localTimeFilter =
                    localTimeFilter
            }

        fun setLocalTimeFilterWithGroup(
            localTimeFilter: LocalTimeFilter?,
            localTimeGroup: LocalTimeGroup?,
        ): LocalTimeBuilder<T> =
            apply {
                query.localTimeFilter = localTimeFilter
                query.localTimeGroup = localTimeGroup
            }

        fun setOrdering(ordering: Ordering): LocalTimeBuilder<T> = apply { query.ordering = ordering }

        fun setPageSize(pageSize: Int): LocalTimeBuilder<T> = apply { query.pageSize = pageSize }

        fun setPageToken(pageToken: String?): LocalTimeBuilder<T> = apply { query.pageToken = pageToken }

        fun setSourceFilter(sourceFilter: AggregateSourceFilter): LocalTimeBuilder<T> =
            apply { query.aggregateSourceFilter = sourceFilter }
    }

    class DualTimeBuilder<T : Any>(
        private val name: String,
    ) : Builder<T> {
        private val query = StubQuery()

        override fun build(): AggregateRequest<T> = AggregateRequest(name, query)

        fun setInstantTimeFilter(instantTimeFilter: InstantTimeFilter): DualTimeBuilder<T> =
            apply { query.instantTimeFilter = instantTimeFilter }

        fun setLocalTimeFilter(localTimeFilter: LocalTimeFilter): DualTimeBuilder<T> =
            apply {
                query.localTimeFilter =
                    localTimeFilter
            }

        fun setLocalTimeFilterWithGroup(
            localTimeFilter: LocalTimeFilter?,
            localTimeGroup: LocalTimeGroup?,
        ): DualTimeBuilder<T> =
            apply {
                query.localTimeFilter = localTimeFilter
                query.localTimeGroup = localTimeGroup
            }

        fun setOrdering(ordering: Ordering): DualTimeBuilder<T> = apply { query.ordering = ordering }

        fun setPageSize(pageSize: Int): DualTimeBuilder<T> = apply { query.pageSize = pageSize }

        fun setPageToken(pageToken: String?): DualTimeBuilder<T> = apply { query.pageToken = pageToken }

        fun setSourceFilter(sourceFilter: AggregateSourceFilter): DualTimeBuilder<T> =
            apply { query.aggregateSourceFilter = sourceFilter }
    }

    class LocalDateBuilder<T : Any>(
        private val name: String,
    ) : Builder<T> {
        private val query = StubQuery()

        override fun build(): AggregateRequest<T> = AggregateRequest(name, query)

        fun setLocalDateFilter(localDateFilter: LocalDateFilter): LocalDateBuilder<T> =
            apply {
                query.localDateFilter =
                    localDateFilter
            }

        fun setLocalDateFilterWithGroup(
            localDateFilter: LocalDateFilter?,
            localDateGroup: LocalDateGroup?,
        ): LocalDateBuilder<T> =
            apply {
                query.localDateFilter = localDateFilter
                query.localDateGroup = localDateGroup
            }

        fun setOrdering(ordering: Ordering): LocalDateBuilder<T> = apply { query.ordering = ordering }

        fun setPageSize(pageSize: Int): LocalDateBuilder<T> = apply { query.pageSize = pageSize }

        fun setPageToken(pageToken: String?): LocalDateBuilder<T> = apply { query.pageToken = pageToken }

        fun setSourceFilter(sourceFilter: AggregateSourceFilter): LocalDateBuilder<T> =
            apply { query.aggregateSourceFilter = sourceFilter }
    }

    class AllSourceLocalDateBuilder<T : Any>(
        private val name: String,
    ) : Builder<T> {
        private val query = StubQuery()

        override fun build(): AggregateRequest<T> = AggregateRequest(name, query)

        fun setLocalDateFilter(localDateFilter: LocalDateFilter): AllSourceLocalDateBuilder<T> =
            apply { query.localDateFilter = localDateFilter }

        fun setLocalDateFilterWithGroup(
            localDateFilter: LocalDateFilter?,
            localDateGroup: LocalDateGroup?,
        ): AllSourceLocalDateBuilder<T> =
            apply {
                query.localDateFilter = localDateFilter
                query.localDateGroup = localDateGroup
            }

        fun setOrdering(ordering: Ordering): AllSourceLocalDateBuilder<T> = apply { query.ordering = ordering }

        fun setPageSize(pageSize: Int): AllSourceLocalDateBuilder<T> = apply { query.pageSize = pageSize }

        fun setPageToken(pageToken: String?): AllSourceLocalDateBuilder<T> = apply { query.pageToken = pageToken }
    }
}

class InsertDataRequest<T : DataPoint>(
    val dataType: DataType,
    val data: List<T>,
) {
    class BasicBuilder<T : DataPoint>(
        private val dataType: DataType,
    ) {
        private val data = mutableListOf<T>()

        fun addData(data: T): BasicBuilder<T> = apply { this.data += data }

        fun build(): InsertDataRequest<T> = InsertDataRequest(dataType, data)
    }
}

class UpdateDataRequest<T : DataPoint>(
    val dataType: DataType,
    val entries: List<Entry<T>>,
) {
    class Entry<T : DataPoint>(
        val uid: String?,
        val clientDataId: String?,
        val data: T,
    )

    class BasicBuilder<T : DataPoint>(
        private val dataType: DataType,
    ) {
        private val entries = mutableListOf<Entry<T>>()

        fun addDataWithUid(
            uid: String,
            data: T,
        ): BasicBuilder<T> = apply { entries += Entry(uid, null, data) }

        fun addDataWithClientDataId(
            clientDataId: String,
            data: T,
        ): BasicBuilder<T> = apply { entries += Entry(null, clientDataId, data) }

        fun build(): UpdateDataRequest<T> = UpdateDataRequest(dataType, entries)
    }
}

class DeleteDataRequest(
    val dataType: DataType,
    val stub: StubQuery,
) {
    class BasicBuilder(
        private val dataType: DataType,
    ) {
        private val query = StubQuery()

        fun setAllData(): BasicBuilder = apply { query.allData = true }

        fun setIdFilter(idFilter: IdFilter): BasicBuilder = apply { query.idFilter = idFilter }

        fun setInstantTimeFilter(instantTimeFilter: InstantTimeFilter): BasicBuilder =
            apply {
                query.instantTimeFilter =
                    instantTimeFilter
            }

        fun setLocalTimeFilter(localTimeFilter: LocalTimeFilter): BasicBuilder =
            apply {
                query.localTimeFilter =
                    localTimeFilter
            }

        fun build(): DeleteDataRequest = DeleteDataRequest(dataType, query)
    }
}

class ChangedDataRequest<T : DataPoint>(
    val dataType: DataType,
    val stub: StubQuery,
) {
    class BasicBuilder<T : DataPoint>(
        private val dataType: DataType,
    ) {
        private val query = StubQuery()

        fun setChangeTimeFilter(changeTimeFilter: InstantTimeFilter): BasicBuilder<T> =
            apply {
                query.instantTimeFilter =
                    changeTimeFilter
            }

        fun setPageSize(pageSize: Int): BasicBuilder<T> = apply { query.pageSize = pageSize }

        fun setPageToken(pageToken: String?): BasicBuilder<T> = apply { query.pageToken = pageToken }

        fun build(): ChangedDataRequest<T> = ChangedDataRequest(dataType, query)
    }
}
