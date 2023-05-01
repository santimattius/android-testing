package com.santimattius.template.ui.home.fakes

import com.santimattius.template.data.repositories.MovieMother
import com.santimattius.template.data.repositories.dtoToDomain
import com.santimattius.template.domain.entities.Movie
import com.santimattius.template.domain.repositories.MovieRepository

class FakeMovieRepository(
    var onPopularMovies: () -> List<Movie> = {
        MovieMother.createMovies().dtoToDomain()
    },
    var onFetchPopularMovies: () -> List<Movie> = onPopularMovies,
) : MovieRepository {

    override suspend fun getPopular(): List<Movie> {
        return onPopularMovies()
    }

    override suspend fun fetchPopular(): Result<List<Movie>> {
        return runCatching { onFetchPopularMovies() }
    }
}