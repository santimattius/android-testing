package com.santimattius.test.data

import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.models.NetworkMovie
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.atomic.AtomicInteger

/**
 * Hand-written [MovieNetworkDataSource] with a scriptable outcome.
 *
 * Outcomes queued with [enqueueSuccess] / [enqueueFailure] are consumed in order and the last
 * one repeats. While the queue is empty, [onMovies] supplies the result.
 * Set [gate] to suspend every call until the test completes it.
 */
class FakeMovieNetworkDataSource(
    var onMovies: () -> List<NetworkMovie> = {
        MovieMother.createMovies()
    },
) : MovieNetworkDataSource {

    private val lock = Any()
    private val outcomes = mutableListOf<Result<List<NetworkMovie>>>()
    private var nextOutcome = 0
    private val calls = AtomicInteger()

    /** When set, [getPopularMovies] suspends on it before producing its outcome. */
    @Volatile
    var gate: CompletableDeferred<Unit>? = null

    /** Number of [getPopularMovies] invocations; intended for diagnosing only. */
    val callCount: Int
        get() = calls.get()

    fun enqueueSuccess(movies: List<NetworkMovie>) = enqueue(Result.success(movies))

    fun enqueueFailure(error: Throwable) = enqueue(Result.failure(error))

    override suspend fun getPopularMovies(): Result<List<NetworkMovie>> {
        calls.incrementAndGet()
        gate?.await()
        return nextQueuedOutcome() ?: runCatching { onMovies() }
    }

    private fun enqueue(outcome: Result<List<NetworkMovie>>) {
        synchronized(lock) { outcomes += outcome }
    }

    private fun nextQueuedOutcome(): Result<List<NetworkMovie>>? = synchronized(lock) {
        if (outcomes.isEmpty()) return null
        val outcome = outcomes[nextOutcome]
        if (nextOutcome < outcomes.lastIndex) nextOutcome++
        outcome
    }
}
