package com.kvs.samsunghealthreporter.model

import kotlinx.serialization.Serializable

/** **Ordering** of read results by start time */
@Serializable
public enum class Ordering {
    /** oldest first */
    ASCENDING,

    /** newest first */
    DESCENDING,
}
