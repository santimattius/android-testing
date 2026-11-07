package com.santimattius.test.data

import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.implementation.MovieNoExists
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory [MovieLocalDataSource] that mirrors the Room-backed implementation contract and
 * publishes an immutable snapshot on every change, so collectors never observe later mutations
 * of a list they already received.
 *
 * Like Room: [save] upserts (existing rows keep their favorite flag), and [update], [delete] and
 * the favorite operations do not fail when the id is missing. Set [failure] to make every
 * `Result`-returning operation fail with it, leaving the stored state untouched.
 */
class FakeMovieLocalDataSource : MovieLocalDataSource {

    private val mutex = Mutex()
    private val moviesFlow = MutableStateFlow<List<MovieEntity>>(emptyList())

    /** When set, every `Result`-returning operation fails with it. */
    @Volatile
    var failure: Throwable? = null

    override val all: Flow<List<MovieEntity>>
        get() = moviesFlow.asStateFlow()

    override suspend fun getAll(): List<MovieEntity> {
        return mutex.withLock { moviesFlow.value }
    }

    override suspend fun isEmpty(): Boolean {
        return mutex.withLock { moviesFlow.value.isEmpty() }
    }

    override suspend fun save(movies: List<MovieEntity>): Result<Boolean> = guarded {
        var merged = moviesFlow.value
        movies.forEach { incoming ->
            val index = merged.indexOfFirst { it.id == incoming.id }
            merged = if (index == -1) {
                merged + incoming
            } else {
                merged.toMutableList().also {
                    it[index] = incoming.copy(favorite = merged[index].favorite)
                }
            }
        }
        moviesFlow.value = merged
        true
    }

    override suspend fun find(id: Long): Result<MovieEntity> = guarded {
        moviesFlow.value.firstOrNull { it.id == id } ?: throw MovieNoExists()
    }

    override suspend fun delete(movie: MovieEntity): Result<Boolean> = guarded {
        moviesFlow.update { current -> current.filterNot { it.id == movie.id } }
        true
    }

    override suspend fun update(movie: MovieEntity): Result<Boolean> = guarded {
        moviesFlow.update { current -> current.map { if (it.id == movie.id) movie else it } }
        true
    }

    override suspend fun addToFavorite(movieId: Long): Result<Unit> =
        setFavorite(movieId, favorite = true)

    override suspend fun removeFromFavorite(movieId: Long): Result<Unit> =
        setFavorite(movieId, favorite = false)

    private suspend fun setFavorite(movieId: Long, favorite: Boolean): Result<Unit> = guarded {
        moviesFlow.update { current ->
            current.map { if (it.id == movieId) it.copy(favorite = favorite) else it }
        }
    }

    private suspend fun <T> guarded(block: () -> T): Result<T> = mutex.withLock {
        failure?.let { return@withLock Result.failure(it) }
        runCatching(block)
    }

    private fun MutableStateFlow<List<MovieEntity>>.update(
        transform: (List<MovieEntity>) -> List<MovieEntity>,
    ) {
        value = transform(value)
    }
}
