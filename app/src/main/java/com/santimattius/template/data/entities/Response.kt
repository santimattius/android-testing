package com.santimattius.template.data.entities

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("page") val page: Int = 1,
    @SerializedName("results") val results: List<T> = emptyList(),
    @SerializedName("total_pages") val totalPages: Int = 1,
    @SerializedName("total_results") val totalResults: Int = 1,
)