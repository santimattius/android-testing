package com.santimattius.shared_test.data

import com.santimattius.core.data.entities.MovieDto
import com.santimattius.core.domain.entities.Movie

fun List<MovieDto>.dtoToDomain(): List<Movie> = this.map {
    Movie(
        id = it.id,
        title = it.title,
        overview = it.overview,
        poster = it.poster
    )
}