package com.kvs.samsunghealthreporter.model

/**
 * **Page** one page of results.
 *
 * @param items **List<T>** the results of this page
 * @param nextPageToken **String?** pass to the next call to get the next page; null on the last page
 */
public data class Page<T>(
    val items: List<T>,
    val nextPageToken: String?,
)
