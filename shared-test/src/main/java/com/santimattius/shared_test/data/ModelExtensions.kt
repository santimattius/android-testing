package com.santimattius.shared_test.data

import com.santimattius.template.data.entities.MovieDto
import com.santimattius.template.domain.entities.Movie
import com.santimattius.template.ui.androidview.home.models.MovieUiModel

fun List<MovieDto>.dtoToDomain() = this.map {
    Movie(
        id = it.id,
        title = it.title,
        overview = it.overview,
        poster = it.poster
    )
}

fun List<MovieDto>.dtoToUiModel() = this.map {
    MovieUiModel(
        id = it.id,
        title = it.title,
        imageUrl = it.poster,
    )
}