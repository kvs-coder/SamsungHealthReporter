package com.kvs.samsunghealthreporter.model

/** **SourceFilter** limits reads to data from one source */
public sealed class SourceFilter {
    /** data written by the app with package name [appId] */
    public data class App(
        val appId: String,
    ) : SourceFilter()

    /** data measured on this phone */
    public data object LocalDevice : SourceFilter()

    /** data written by Samsung Health itself */
    public data object SamsungHealth : SourceFilter()
}
