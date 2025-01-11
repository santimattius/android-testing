package com.santimattius.test.data

import com.santimattius.core.data.models.NetworkMovie
import com.santimattius.core.domain.entities.Movie

fun List<NetworkMovie>.dtoToDomain(): List<Movie> = this.map {
    Movie(
        id = it.id,
        title = it.title,
        overview = it.overview,
        poster = it.poster
    )
}