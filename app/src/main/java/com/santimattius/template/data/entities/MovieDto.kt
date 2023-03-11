package com.santimattius.template.data.entities

import com.google.gson.annotations.SerializedName

data class

MovieDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("overview")
    val overview: String = "",
    @SerializedName("original_language")
    val originalLanguage: String = "",
    @SerializedName("original_title")
    val originalTitle: String = "",
    @SerializedName("video")
    val video: Boolean = false,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @SerializedName("poster_path")
    private val posterPath: String? = null,
    @SerializedName("backdrop_path")
    val backdropPath: String? = null,
    @SerializedName("release_date")
    val releaseDate: String = "",
    @SerializedName("popularity")
    val popularity: Double = 0.0,
    @SerializedName("vote_average")
    val voteAverage: Double = 0.0,
    @SerializedName("adult")
    val adult: Boolean = false,
    @SerializedName("vote_count")
    val voteCount: Int = 0,
) {
    val poster: String
        get() = "https://image.tmdb.org/t/p/w500${posterPath}"
}