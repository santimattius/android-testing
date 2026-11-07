package com.santimattius.test.data

import com.santimattius.core.domain.entities.Movie
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FakeMovieRepositoryTest {

    private fun movie(id: Long, favorite: Boolean = false) = Movie(
        id = id,
        overview = "Overview $id",
        title = "Title $id",
        poster = "/poster$id.jpg",
        favorite = favorite,
    )

    @Test
    fun defaultsToTheMotherMovies() = runTest {
        val repository = FakeMovieRepository()

        assertEquals(MovieMother.createMovies().dtoToDomain(), repository.getAll())
    }

    @Test
    fun allEmitsTheInitialMovies() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1)))

        assertEquals(listOf(movie(1)), repository.all.first())
    }

    @Test
    fun addToFavoriteUpdatesTheEmittedList() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1), movie(2)))

        val result = repository.addToFavorite(2)

        assertEquals(Unit, result.getOrThrow())
        assertEquals(listOf(movie(1), movie(2, favorite = true)), repository.all.first())
    }

    @Test
    fun removeFromFavoriteUpdatesTheEmittedList() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1, favorite = true)))

        val result = repository.removeFromFavorite(1)

        assertEquals(Unit, result.getOrThrow())
        assertEquals(listOf(movie(1)), repository.getAll())
    }

    @Test
    fun favoriteOperationsOnMissingIdsSucceedWithoutChangingTheList() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1)))

        assertTrue(repository.addToFavorite(99).isSuccess)
        assertTrue(repository.removeFromFavorite(99).isSuccess)

        assertEquals(listOf(movie(1)), repository.getAll())
    }

    @Test
    fun refreshWithoutConfiguredOutcomeReturnsTheCurrentList() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1)))

        assertEquals(listOf(movie(1)), repository.refresh().getOrThrow())
    }

    @Test
    fun refreshSuccessReplacesTheEmittedList() = runTest {
        val repository = FakeMovieRepository(emptyList())
        repository.refreshResult = Result.success(listOf(movie(5)))

        assertEquals(listOf(movie(5)), repository.refresh().getOrThrow())
        assertEquals(listOf(movie(5)), repository.all.first())
    }

    @Test
    fun refreshFailureIsReturnedAndKeepsTheList() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1)))
        val failure = IllegalStateException("refresh failed")
        repository.refreshResult = Result.failure(failure)

        assertSame(failure, repository.refresh().exceptionOrNull())
        assertEquals(listOf(movie(1)), repository.all.first())
    }

    @Test
    fun getAllFailureIsThrown() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1)))
        val failure = IllegalStateException("db down")
        repository.getAllFailure = failure

        try {
            repository.getAll()
            fail("Expected getAll to throw")
        } catch (thrown: IllegalStateException) {
            assertSame(failure, thrown)
        }
    }

    @Test
    fun refreshGateSuspendsUntilReleased() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1)))
        val gate = CompletableDeferred<Unit>()
        repository.refreshGate = gate

        val call = async(start = CoroutineStart.UNDISPATCHED) { repository.refresh() }
        assertFalse(call.isCompleted)

        gate.complete(Unit)

        assertEquals(listOf(movie(1)), call.await().getOrThrow())
    }

    @Test
    fun getAllGateSuspendsUntilReleased() = runTest {
        val repository = FakeMovieRepository(listOf(movie(1)))
        val gate = CompletableDeferred<Unit>()
        repository.getAllGate = gate

        val call = async(start = CoroutineStart.UNDISPATCHED) { repository.getAll() }
        assertFalse(call.isCompleted)

        gate.complete(Unit)

        assertEquals(listOf(movie(1)), call.await())
    }
}
