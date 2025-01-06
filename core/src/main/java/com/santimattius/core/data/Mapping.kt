package com.santimattius.core.data

import com.santimattius.core.data.entities.MovieDto
import com.santimattius.core.data.entities.MovieEntity
import com.santimattius.core.domain.entities.Movie

/**
 * map entity to domain model
 *
 * @return movie domain model
 */
internal fun List<MovieEntity>.entityToDomain(): List<Movie> {
    return this.map {
        Movie(
            id = it.id,
            title = it.title,
            overview = it.overview,
            poster = it.poster
        )
    }
}

internal fun List<MovieDto>.dtoToEntity(): List<MovieEntity> {
    return map {
        MovieEntity(
            id = it.id,
            title = it.title,
            overview = it.overview,
            poster = it.poster
        )
    }
}