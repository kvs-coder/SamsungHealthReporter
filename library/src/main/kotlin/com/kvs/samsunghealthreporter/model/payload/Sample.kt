package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.Payload
import com.kvs.samsunghealthreporter.model.type.HealthType
import kotlinx.serialization.Serializable

/**
 * **Sample** one data point stored in Samsung Health.
 *
 * In JSON a sample carries a `type` discriminator with its [healthType] identifier.
 */
@Serializable
public sealed interface Sample : Payload {
    /** **String?** Samsung Health id of the stored data point; null until it is inserted */
    public val uid: String?

    /** **String?** your own id for the data point (1–36 characters), kept by Samsung Health */
    public val clientDataId: String?

    /** **Long** start time, epoch milliseconds */
    public val startTimestamp: Long

    /** **Long?** end time, epoch milliseconds; null for instantaneous measurements */
    public val endTimestamp: Long?

    /** **Int?** zone offset of the measurement in seconds; null means the device's current offset */
    public val zoneOffsetSeconds: Int?

    /** **DataSource?** the app and device that wrote the data point; null until it is inserted */
    public val dataSource: DataSource?

    /** **HealthType** the data type of this sample */
    public val healthType: HealthType
}

/** **WritableSample** a sample type apps may insert, update and delete with **SamsungHealthWriter** */
@Serializable
public sealed interface WritableSample : Sample
