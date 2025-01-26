package com.santimattius.core.domain.entities

data class Movie(
    val id: Long,
    val overview: String,
    val title: String,
    val poster: String,
    val favorite: Boolean = false,
)