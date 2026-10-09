package com.samsung.android.sdk.health.data.data

import android.os.Parcel
import android.os.Parcelable
import com.samsung.android.sdk.health.data.error.InvalidRequestException
import com.samsung.android.sdk.health.data.request.AggregateRequest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

interface DataPoint : Parcelable

/** Stub only: the SDK types are Parcelable; the stub never parcels them. */
abstract class StubParcelable : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ): Unit = Unit
}

class Field<T>(
    val name: String,
    val typeName: String,
)

class DataSource(
    val appId: String,
    val deviceId: String,
)

enum class ChangeType { UPSERT, DELETE }

class Change<T : DataPoint?>(
    val changeType: ChangeType,
    val changeTime: Instant,
    val deleteDataUid: String?,
    val upsertDataPoint: T,
) : StubParcelable()

class AggregatedData<T : Any>(
    val startTime: Instant,
    val endTime: Instant,
    val value: T?,
) : StubParcelable() {
    fun getValueOrDefault(defaultValue: T): T = value ?: defaultValue
}

class AggregateOperation<T : Any, S : AggregateRequest.Builder<T>?>(
    private val builder: () -> S,
) {
    val requestBuilder: S get() = builder()
}

class UserDataPoint(
    private val values: Map<Field<*>, Any?>,
) : StubParcelable(),
    DataPoint {
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(field: Field<T>): T? = values[field] as T?

    fun <T> getValueOrDefault(
        field: Field<T>,
        defaultValue: T,
    ): T = getValue(field) ?: defaultValue
}

class HealthDataPoint private constructor(
    val uid: String,
    val startTime: Instant,
    val endTime: Instant?,
    val zoneOffset: ZoneOffset?,
    val dataSource: DataSource?,
    val updateTime: Instant?,
    val clientDataId: String?,
    val clientVersion: Int?,
    private val values: Map<Field<*>, Any?>,
) : StubParcelable(),
    DataPoint {
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(field: Field<T>): T? = values[field] as T?

    fun <T> getValueOrDefault(
        field: Field<T>,
        defaultValue: T,
    ): T = getValue(field) ?: defaultValue

    fun getStartLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(startTime, zoneOffset ?: ZoneId.systemDefault())

    fun getEndLocalDateTime(): LocalDateTime? =
        endTime?.let {
            LocalDateTime.ofInstant(
                it,
                zoneOffset ?: ZoneId.systemDefault(),
            )
        }

    class Builder {
        private var uid: String = ""
        private var startTime: Instant? = null
        private var endTime: Instant? = null
        private var zoneOffset: ZoneOffset? = null
        private var dataSource: DataSource? = null
        private var updateTime: Instant? = null
        private var clientDataId: String? = null
        private var clientVersion: Int? = null
        private val values = mutableMapOf<Field<*>, Any?>()

        fun setStartTime(
            startTime: Instant,
            zoneOffset: ZoneOffset? = null,
        ): Builder =
            apply {
                this.startTime = startTime
                this.zoneOffset = zoneOffset
            }

        fun setEndTime(
            endTime: Instant,
            zoneOffset: ZoneOffset? = null,
        ): Builder =
            apply {
                this.endTime = endTime
                if (zoneOffset != null) this.zoneOffset = zoneOffset
            }

        fun setClientDataId(clientDataId: String?): Builder = apply { this.clientDataId = clientDataId }

        fun setClientVersion(clientVersion: Int?): Builder = apply { this.clientVersion = clientVersion }

        fun setDeviceId(deviceId: String): Builder = apply { dataSource = DataSource("", deviceId) }

        fun <T> addFieldData(
            field: Field<T>,
            value: T?,
        ): Builder = apply { values[field] = value }

        fun build(): HealthDataPoint {
            val start = startTime ?: throw InvalidRequestException(1001, "startTime is null")
            if (endTime?.isBefore(start) == true) throw InvalidRequestException(1001, "endTime is before startTime")
            if (values.isEmpty()) throw InvalidRequestException(1001, "At least one data must be inserted")
            return HealthDataPoint(
                uid,
                start,
                endTime,
                zoneOffset,
                dataSource,
                updateTime,
                clientDataId,
                clientVersion,
                values.toMap(),
            )
        }
    }

    companion object {
        fun builder(): Builder = Builder()
    }
}
