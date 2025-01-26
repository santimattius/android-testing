package com.santimattius.template.ui.fakes

import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import com.santimattius.core.domain.entities.Movie
import com.santimattius.core.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockMovieRepository(
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

    override suspend fun addToFavorite(movieId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromFavorite(movieId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }
}