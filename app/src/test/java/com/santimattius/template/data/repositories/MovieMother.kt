package com.santimattius.template.data.repositories

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.santimattius.template.data.entities.MovieDto
import com.santimattius.template.data.entities.Response
import com.santimattius.template.domain.entities.Movie
import com.santimattius.template.ui.home.models.MovieUiModel
import com.santimattius.template.utils.JsonLoader

object MovieMother {
    private val jsonLoader = JsonLoader()
    private val gson = GsonBuilder().create()

    fun createMovies(): List<MovieDto> {
        val json = jsonLoader.load("movie_popular_response_success.json")
        return gson.fromJson<Response<MovieDto>>(
            json,
            object : TypeToken<Response<MovieDto>>() {}.type
        ).results
    }

    fun createMovie(
        id: Int = 508947,
        title: String = "Spider-Man: No Way Home",
        overview: String = "Spider-Man: No Way Home",
        poster: String = "/fOy2Jurz9k6RnJnMUMRDAgBwru2.jpg"
    ) = Movie(id, overview, title, poster)
}

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