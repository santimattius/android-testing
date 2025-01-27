package com.santimattius.template.ui.models.mapping

import com.santimattius.core.domain.entities.Movie
import com.santimattius.template.ui.models.MovieUiModel

fun List<Movie>.asUiModels() = map { it.asUiModel() }

fun Movie.asUiModel() = MovieUiModel(
    id = this.id,
    title = this.title,
    imageUrl = this.poster,
    favorite = this.favorite
)