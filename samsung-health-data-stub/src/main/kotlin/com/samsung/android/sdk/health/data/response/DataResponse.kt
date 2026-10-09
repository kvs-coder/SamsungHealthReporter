package com.samsung.android.sdk.health.data.response

import android.os.Parcelable

class DataResponse<T : Parcelable>(
    val dataList: List<T>,
    val pageToken: String?,
)
