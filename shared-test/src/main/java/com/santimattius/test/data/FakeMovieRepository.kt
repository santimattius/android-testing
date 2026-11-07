package com.santimattius.test.data

import com.santimattius.core.domain.entities.Movie
import com.santimattius.core.domain.repositories.MovieRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Hand-written [MovieRepository] backed by an in-memory list.
 *
 * [all] reflects favorite changes and successful refreshes. [refreshResult], [getAllFailure]
 * and the gates let a test script failures and hold calls open.
 */
class FakeMovieRepository(
    initialMovies: List<Movie> = MovieMother.createMovies().dtoToDomain(),
) : MovieRepository {

    private val movies = MutableStateFlow(initialMovies)

    /** Outcome of [refresh]; `null` succeeds with the current list. A success replaces the list. */
    var refreshResult: Result<List<Movie>>? = null

    /** When set, [getAll] throws it. */
    var getAllFailure: Throwable? = null

    /** When set, [refresh] suspends on it before producing its outcome. */
    @Volatile
    var refreshGate: CompletableDeferred<Unit>? = null

    /** When set, [getAll] suspends on it before producing its outcome. */
    @Volatile
    var getAllGate: CompletableDeferred<Unit>? = null

    override val all: Flow<List<Movie>> = movies.asStateFlow()

    override suspend fun getAll(): List<Movie> {
        getAllGate?.await()
        getAllFailure?.let { throw it }
        return movies.value
    }

    override suspend fun refresh(): Result<List<Movie>> {
        refreshGate?.await()
        val outcome = refreshResult ?: return Result.success(movies.value)
        outcome.onSuccess { movies.value = it }
        return outcome
    }

    override suspend fun addToFavorite(movieId: Long): Result<Unit> = setFavorite(movieId, true)

    override suspend fun removeFromFavorite(movieId: Long): Result<Unit> =
        setFavorite(movieId, false)

    private fun setFavorite(movieId: Long, favorite: Boolean): Result<Unit> {
        movies.update { current ->
            current.map { if (it.id == movieId) it.copy(favorite = favorite) else it }
        }
        return Result.success(Unit)
    }
}
