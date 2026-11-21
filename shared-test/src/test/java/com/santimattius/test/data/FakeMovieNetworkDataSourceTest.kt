package com.santimattius.test.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class FakeMovieNetworkDataSourceTest {

    private val first = MovieMother.createMovies().take(1)
    private val second = MovieMother.createMovies().takeLast(1)

    @Test
    fun withoutQueuedOutcomesItUsesTheDefaultSupplier() = runTest {
        val dataSource = FakeMovieNetworkDataSource(onMovies = { first })

        assertEquals(first, dataSource.getPopularMovies().getOrThrow())
    }

    @Test
    fun onMoviesCanStillBeReassigned() = runTest {
        val dataSource = FakeMovieNetworkDataSource()

        dataSource.onMovies = { second }

        assertEquals(second, dataSource.getPopularMovies().getOrThrow())
    }

    @Test
    fun queuedOutcomesAreConsumedInOrderAndTheLastOneRepeats() = runTest {
        val failure = IOException("offline")
        val dataSource = FakeMovieNetworkDataSource()
        dataSource.enqueueSuccess(first)
        dataSource.enqueueFailure(failure)
        dataSource.enqueueSuccess(second)

        assertEquals(first, dataSource.getPopularMovies().getOrThrow())
        assertSame(failure, dataSource.getPopularMovies().exceptionOrNull())
        assertEquals(second, dataSource.getPopularMovies().getOrThrow())
        assertEquals(second, dataSource.getPopularMovies().getOrThrow())
    }

    @Test
    fun callCountReflectsTheNumberOfCalls() = runTest {
        val dataSource = FakeMovieNetworkDataSource()

        dataSource.getPopularMovies()
        dataSource.getPopularMovies()

        assertEquals(2, dataSource.callCount)
    }

    @Test
    fun gateSuspendsTheCallUntilReleased() = runTest {
        val dataSource = FakeMovieNetworkDataSource()
        val gate = CompletableDeferred<Unit>()
        dataSource.gate = gate
        dataSource.enqueueSuccess(first)

        val call = async(start = CoroutineStart.UNDISPATCHED) { dataSource.getPopularMovies() }

        assertFalse(call.isCompleted)
        assertEquals(1, dataSource.callCount)

        gate.complete(Unit)

        assertEquals(first, call.await().getOrThrow())
    }

    @Test
    fun cancellingAGatedCallPropagatesCancellation() = runTest {
        val dataSource = FakeMovieNetworkDataSource()
        dataSource.gate = CompletableDeferred<Unit>()

        val call = async(start = CoroutineStart.UNDISPATCHED) { dataSource.getPopularMovies() }
        call.cancel()

        assertTrue(runCatching { call.await() }.exceptionOrNull() is CancellationException)
    }
}
