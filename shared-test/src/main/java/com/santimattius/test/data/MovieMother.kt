package com.santimattius.test.data

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.santimattius.core.data.models.NetworkMovie
import com.santimattius.core.data.models.Response
import com.santimattius.core.domain.entities.Movie
import com.santimattius.test.data.strategies.MovieBuilder

object MovieMother {
    private val jsonLoader = com.santimattius.test.JsonLoader()
    private val gson = GsonBuilder().create()

    fun createMovies(): List<NetworkMovie> {
        val json = jsonLoader.load("movie_popular_response_success.json")
        return gson.fromJson<Response<NetworkMovie>>(
            json,
            object : TypeToken<Response<NetworkMovie>>() {}.type
        ).results
    }

    fun createDomainMovies(): List<Movie> {
        return createMovies().dtoToDomain()
    }

    fun createMovie(
        id: Int = 508947,
        title: String = "Spider-Man: No Way Home",
        overview: String = "Spider-Man: No Way Home",
        poster: String = "/fOy2Jurz9k6RnJnMUMRDAgBwru2.jpg"
    ) = Movie(id, overview, title, poster)

    fun buildMovie(
        id: Int = 508947,
        title: String = "Spider-Man: No Way Home",
        overview: String = "Spider-Man: No Way Home",
        poster: String = "/fOy2Jurz9k6RnJnMUMRDAgBwru2.jpg"
    ) = MovieBuilder()
        .withId(id)
        .withTitle(title)
        .withOverview(overview)
        .withPoster(poster)
        .build()
}

