package com.santimattius.template.ui.fakes

import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToDomain
import com.santimattius.core.domain.entities.Movie
import com.santimattius.core.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMovieRepository(
    var onPopularMovies: () -> List<Movie> = {
        MovieMother.createMovies().dtoToDomain()
    },
    var onFetchPopularMovies: () -> List<Movie> = onPopularMovies,
) : MovieRepository {

    override val all: Flow<List<Movie>> = flow {
        emit(onPopularMovies())
    }

    override suspend fun getAll(): List<Movie> {
        return onPopularMovies()
    }

    override suspend fun refresh(): Result<List<Movie>> {
        return runCatching { onFetchPopularMovies() }
    }
}