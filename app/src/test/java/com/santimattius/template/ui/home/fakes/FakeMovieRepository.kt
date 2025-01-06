package com.santimattius.template.ui.home.fakes

import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToDomain
import com.santimattius.core.domain.entities.Movie
import com.santimattius.core.domain.repositories.MovieRepository

class FakeMovieRepository(
    var onPopularMovies: () -> List<Movie> = {
        MovieMother.createMovies().dtoToDomain()
    },
    var onFetchPopularMovies: () -> List<Movie> = onPopularMovies,
) : MovieRepository {

    override suspend fun getAll(): List<Movie> {
        return onPopularMovies()
    }

    override suspend fun refresh(): Result<List<Movie>> {
        return runCatching { onFetchPopularMovies() }
    }
}