package com.santimattius.test.data

import com.santimattius.core.data.datasources.implementation.MovieNoExists
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeMovieLocalDataSourceTest {

    private val dataSource = FakeMovieLocalDataSource()

    private fun movie(id: Long, favorite: Boolean = false, title: String = "Title $id") =
        MovieEntity(
            id = id,
            title = title,
            overview = "Overview $id",
            poster = "/poster$id.jpg",
            favorite = favorite,
        )

    @Test
    fun savePublishesANewImmutableSnapshot() = runTest {
        val initialSnapshot = dataSource.all.first()
        val nextSnapshot = async(start = CoroutineStart.UNDISPATCHED) {
            dataSource.all.drop(1).first()
        }
        val movie = movie(1)

        dataSource.save(listOf(movie))

        assertEquals(emptyList<MovieEntity>(), initialSnapshot)
        assertEquals(listOf(movie), withTimeout(100) { nextSnapshot.await() })
    }

    @Test
    fun saveUpsertsByIdKeepingTheStoredFavoriteFlag() = runTest {
        dataSource.save(listOf(movie(1, favorite = true), movie(2)))

        val result = dataSource.save(
            listOf(movie(1, favorite = false, title = "Renamed"), movie(3)),
        )

        assertTrue(result.getOrThrow())
        assertEquals(
            listOf(movie(1, favorite = true, title = "Renamed"), movie(2), movie(3)),
            dataSource.getAll(),
        )
    }

    @Test
    fun saveWithEmptyListSucceedsWithoutChangingState() = runTest {
        dataSource.save(listOf(movie(1)))

        val result = dataSource.save(emptyList())

        assertTrue(result.getOrThrow())
        assertEquals(listOf(movie(1)), dataSource.getAll())
    }

    @Test
    fun addToFavoriteMarksTheStoredMovieAndPublishesIt() = runTest {
        dataSource.save(listOf(movie(1), movie(2)))

        val result = dataSource.addToFavorite(1)

        assertEquals(Unit, result.getOrThrow())
        assertEquals(listOf(movie(1, favorite = true), movie(2)), dataSource.all.first())
    }

    @Test
    fun removeFromFavoriteClearsTheStoredFlag() = runTest {
        dataSource.save(listOf(movie(1, favorite = true)))
        dataSource.addToFavorite(1)

        val result = dataSource.removeFromFavorite(1)

        assertEquals(Unit, result.getOrThrow())
        assertFalse(dataSource.find(1).getOrThrow().favorite)
    }

    @Test
    fun favoriteOperationsOnMissingIdsSucceedWithoutChangingState() = runTest {
        dataSource.save(listOf(movie(1)))

        assertTrue(dataSource.addToFavorite(99).isSuccess)
        assertTrue(dataSource.removeFromFavorite(99).isSuccess)

        assertEquals(listOf(movie(1)), dataSource.getAll())
    }

    @Test
    fun findOfMissingIdFailsWithMovieNoExists() = runTest {
        val result = dataSource.find(1)

        assertTrue(result.exceptionOrNull() is MovieNoExists)
    }

    @Test
    fun updateAndDeleteOfMissingMoviesSucceedWithoutChangingState() = runTest {
        dataSource.save(listOf(movie(1)))

        assertTrue(dataSource.update(movie(99)).getOrThrow())
        assertTrue(dataSource.delete(movie(99)).getOrThrow())

        assertEquals(listOf(movie(1)), dataSource.getAll())
    }

    @Test
    fun deleteRemovesTheStoredMovie() = runTest {
        dataSource.save(listOf(movie(1), movie(2)))

        dataSource.delete(movie(1))

        assertEquals(listOf(movie(2)), dataSource.getAll())
        assertFalse(dataSource.isEmpty())
    }

    @Test
    fun injectedFailureMakesResultOperationsFailAndKeepsState() = runTest {
        dataSource.save(listOf(movie(1)))
        val failure = IllegalStateException("db down")
        dataSource.failure = failure

        assertSame(failure, dataSource.save(listOf(movie(2))).exceptionOrNull())
        assertSame(failure, dataSource.find(1).exceptionOrNull())
        assertSame(failure, dataSource.update(movie(1, title = "X")).exceptionOrNull())
        assertSame(failure, dataSource.delete(movie(1)).exceptionOrNull())
        assertSame(failure, dataSource.addToFavorite(1).exceptionOrNull())
        assertSame(failure, dataSource.removeFromFavorite(1).exceptionOrNull())

        dataSource.failure = null
        assertEquals(listOf(movie(1)), dataSource.getAll())
        assertTrue(dataSource.addToFavorite(1).isSuccess)
    }
}
