package com.santimattius.core.data.repositories

import com.santimattius.core.data.dtoToEntity
import com.santimattius.core.data.models.MovieEntity
import com.santimattius.core.domain.entities.Movie
import com.santimattius.test.data.FakeMovieLocalDataSource
import com.santimattius.test.data.FakeMovieNetworkDataSource
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class TMDbRepositoryTest {

    private val network = FakeMovieNetworkDataSource()
    private val local = FakeMovieLocalDataSource()
    private val repository = TMDbRepository(network, local)

    private val movies = MovieMother.createMovies()
    private val firstMovie = movies[0]
    private val secondMovie = movies[1]

    private suspend fun storedMovies(): List<MovieEntity> = local.all.first()

    // region getAll

    @Test
    fun `given empty local and successful network when getAll then returns network movies`() =
        runTest {
            network.enqueueSuccess(movies)

            val result = repository.getAll()

            assertEquals(movies.dtoToDomain(), result)
        }

    @Test
    fun `given empty local and successful network when getAll then stores network movies`() =
        runTest {
            network.enqueueSuccess(movies)

            repository.getAll()

            assertEquals(movies.dtoToEntity(), storedMovies())
        }

    @Test
    fun `given non-empty local and failing network when getAll then returns local movies`() =
        runTest {
            local.save(listOf(firstMovie).dtoToEntity())
            network.enqueueFailure(IOException("network must not be needed"))

            val result = repository.getAll()

            assertEquals(listOf(firstMovie).dtoToDomain(), result)
        }

    @Test
    fun `given non-empty local and different network movies when getAll then local data is unchanged`() =
        runTest {
            local.save(listOf(firstMovie).dtoToEntity())
            network.enqueueSuccess(listOf(secondMovie))

            repository.getAll()

            assertEquals(listOf(firstMovie).dtoToEntity(), storedMovies())
        }

    @Test
    fun `given empty local and failing network when getAll then returns empty list`() = runTest {
        network.enqueueFailure(IOException("offline"))

        val result = repository.getAll()

        assertEquals(emptyList<Movie>(), result)
    }

    @Test
    fun `given empty local and failing network when getAll then local stays empty`() = runTest {
        network.enqueueFailure(IOException("offline"))

        repository.getAll()

        assertEquals(emptyList<MovieEntity>(), storedMovies())
    }

    @Test
    fun `given network failed once when getAll is called again then it recovers with network movies`() =
        runTest {
            network.enqueueFailure(IOException("offline"))
            network.enqueueSuccess(movies)

            val first = repository.getAll()
            val second = repository.getAll()

            assertEquals(emptyList<Movie>(), first)
            assertEquals(movies.dtoToDomain(), second)
        }

    @Ignore("PR5: getAll swallows the network failure (getOrDefault(emptyList())), so callers cannot tell offline from empty")
    @Test
    fun `given empty local and failing network when getAll then failure cause is preserved`() =
        runTest {
            val cause = IOException("offline")
            network.enqueueFailure(cause)

            val outcome = runCatching { repository.getAll() }

            assertSame(cause, outcome.exceptionOrNull())
        }

    // endregion

    // region refresh

    @Test
    fun `given successful network when refresh then returns the refreshed movies`() = runTest {
        network.enqueueSuccess(movies)

        val result = repository.refresh()

        assertEquals(Result.success(movies.dtoToDomain()), result)
    }

    @Test
    fun `given successful network when refresh then all emits the refreshed movies`() = runTest {
        network.enqueueSuccess(movies)

        repository.refresh()

        assertEquals(movies.dtoToDomain(), repository.all.first())
    }

    @Test
    fun `given failing network when refresh then returns RefreshMovieFailed`() = runTest {
        network.enqueueFailure(IOException("offline"))

        val result = repository.refresh()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RefreshMovieFailed)
    }

    @Test
    fun `given stored movies and failing network when refresh then local data is unchanged`() =
        runTest {
            local.save(listOf(firstMovie).dtoToEntity())
            local.addToFavorite(firstMovie.id)
            val before = storedMovies()
            network.enqueueFailure(IOException("offline"))

            repository.refresh()

            assertEquals(before, storedMovies())
        }

    @Test
    fun `given two successful refreshes with the same movies when refresh then no duplicates`() =
        runTest {
            network.enqueueSuccess(movies)

            repository.refresh()
            val second = repository.refresh()

            assertEquals(movies.dtoToDomain(), second.getOrNull())
            assertEquals(movies.map { it.id }, storedMovies().map { it.id })
        }

    @Test
    fun `given a second refresh with updated content when refresh then second replaces first`() =
        runTest {
            val updated = firstMovie.copy(title = "Updated title")
            network.enqueueSuccess(listOf(firstMovie))
            network.enqueueSuccess(listOf(updated))

            repository.refresh()
            val second = repository.refresh()

            assertEquals(listOf(updated).dtoToDomain(), second.getOrNull())
            assertEquals(listOf(updated).dtoToEntity(), storedMovies())
        }

    @Test
    fun `given a favorite movie when refresh then favorite flag is preserved`() = runTest {
        network.enqueueSuccess(movies)
        repository.refresh()
        repository.addToFavorite(firstMovie.id)

        val result = repository.refresh()

        val refreshed = result.getOrThrow().first { it.id == firstMovie.id }
        assertTrue(refreshed.favorite)
    }

    @Test
    fun `given empty local and empty network response when refresh then returns empty list`() =
        runTest {
            network.enqueueSuccess(emptyList())

            val result = repository.refresh()

            assertEquals(Result.success(emptyList<Movie>()), result)
            assertEquals(emptyList<MovieEntity>(), storedMovies())
        }

    @Test
    fun `given stored movies and empty network response when refresh then stored movies are kept`() =
        runTest {
            network.enqueueSuccess(movies)
            repository.refresh()
            network.enqueueSuccess(emptyList())

            val result = repository.refresh()

            assertEquals(movies.dtoToDomain(), result.getOrNull())
        }

    @Ignore("PR5: refresh ignores the Result of local save, so a local write failure is reported as success with stale data")
    @Test
    fun `given local save failure when refresh then returns failure`() = runTest {
        network.enqueueSuccess(movies)
        local.failure = IOException("disk full")

        val result = repository.refresh()

        assertTrue(result.isFailure)
    }

    // endregion

    // region favorites

    @Test
    fun `given stored movie when addToFavorite then movie is favorite in all and getAll`() =
        runTest {
            network.enqueueSuccess(movies)
            repository.getAll()

            val result = repository.addToFavorite(firstMovie.id)

            assertTrue(result.isSuccess)
            assertTrue(repository.all.first().first { it.id == firstMovie.id }.favorite)
            assertTrue(repository.getAll().first { it.id == firstMovie.id }.favorite)
        }

    @Test
    fun `given stored movie when addToFavorite then other movies are not favorite`() = runTest {
        network.enqueueSuccess(movies)
        repository.getAll()

        repository.addToFavorite(firstMovie.id)

        val others = repository.all.first().filter { it.id != firstMovie.id }
        assertTrue(others.isNotEmpty())
        assertTrue(others.none { it.favorite })
    }

    @Test
    fun `given favorite movie when addToFavorite again then it stays favorite`() = runTest {
        network.enqueueSuccess(movies)
        repository.getAll()
        repository.addToFavorite(firstMovie.id)
        val before = repository.all.first()

        val result = repository.addToFavorite(firstMovie.id)

        assertTrue(result.isSuccess)
        assertEquals(before, repository.all.first())
    }

    @Test
    fun `given favorite movie when removeFromFavorite then it is no longer favorite`() = runTest {
        network.enqueueSuccess(movies)
        repository.getAll()
        repository.addToFavorite(firstMovie.id)

        val result = repository.removeFromFavorite(firstMovie.id)

        assertTrue(result.isSuccess)
        assertFalse(repository.all.first().first { it.id == firstMovie.id }.favorite)
        assertFalse(repository.getAll().first { it.id == firstMovie.id }.favorite)
    }

    @Test
    fun `given favorite movie when removeFromFavorite twice then it stays not favorite`() =
        runTest {
            network.enqueueSuccess(movies)
            repository.getAll()
            repository.addToFavorite(firstMovie.id)
            repository.removeFromFavorite(firstMovie.id)
            val before = repository.all.first()

            val result = repository.removeFromFavorite(firstMovie.id)

            assertTrue(result.isSuccess)
            assertEquals(before, repository.all.first())
        }

    @Test
    fun `given unknown id when addToFavorite then succeeds and other movies are unchanged`() =
        runTest {
            network.enqueueSuccess(movies)
            repository.getAll()
            val before = repository.all.first()

            val result = repository.addToFavorite(UNKNOWN_ID)

            assertTrue(result.isSuccess)
            assertEquals(before, repository.all.first())
        }

    @Test
    fun `given unknown id when removeFromFavorite then succeeds and other movies are unchanged`() =
        runTest {
            network.enqueueSuccess(movies)
            repository.getAll()
            repository.addToFavorite(firstMovie.id)
            val before = repository.all.first()

            val result = repository.removeFromFavorite(UNKNOWN_ID)

            assertTrue(result.isSuccess)
            assertEquals(before, repository.all.first())
        }

    @Test
    fun `given local failure when addToFavorite then returns failure and data is unchanged`() =
        runTest {
            network.enqueueSuccess(movies)
            repository.getAll()
            val before = storedMovies()
            val cause = IOException("disk error")
            local.failure = cause

            val result = repository.addToFavorite(firstMovie.id)

            assertSame(cause, result.exceptionOrNull())
            assertEquals(before, storedMovies())
        }

    @Test
    fun `given local failure when removeFromFavorite then returns failure and data is unchanged`() =
        runTest {
            network.enqueueSuccess(movies)
            repository.getAll()
            repository.addToFavorite(firstMovie.id)
            val before = storedMovies()
            val cause = IOException("disk error")
            local.failure = cause

            val result = repository.removeFromFavorite(firstMovie.id)

            assertSame(cause, result.exceptionOrNull())
            assertEquals(before, storedMovies())
        }

    // endregion

    // region all

    @Test
    fun `given empty local when collecting all then emits an empty list`() = runTest {
        assertEquals(emptyList<Movie>(), repository.all.first())
    }

    @Test
    fun `given stored entities when collecting all then maps every field to domain movies`() =
        runTest {
            local.save(
                listOf(
                    MovieEntity(1, "Title 1", "Overview 1", "/poster1.jpg", favorite = true),
                    MovieEntity(2, "Title 2", "Overview 2", "/poster2.jpg"),
                )
            )

            val result = repository.all.first()

            assertEquals(
                listOf(
                    Movie(1, "Overview 1", "Title 1", "/poster1.jpg", favorite = true),
                    Movie(2, "Overview 2", "Title 2", "/poster2.jpg", favorite = false),
                ),
                result,
            )
        }

    @Test
    fun `given a collector when data is saved and favorited then all emits each change`() =
        runTest {
            val emissions = mutableListOf<List<Movie>>()
            backgroundScope.launch { repository.all.toList(emissions) }
            runCurrent()
            network.enqueueSuccess(listOf(firstMovie))

            repository.refresh()
            runCurrent()
            repository.addToFavorite(firstMovie.id)
            runCurrent()

            val domain = listOf(firstMovie).dtoToDomain()
            assertEquals(
                listOf(
                    emptyList(),
                    domain,
                    domain.map { it.copy(favorite = true) },
                ),
                emissions,
            )
        }

    // endregion

    // region concurrency

    @Test
    fun `given gated network when two refreshes run concurrently then both complete without duplicates`() =
        runTest {
            val gate = CompletableDeferred<Unit>()
            network.gate = gate
            network.enqueueSuccess(movies)
            val first = async { repository.refresh() }
            val second = async { repository.refresh() }
            runCurrent()

            assertFalse(first.isCompleted)
            assertFalse(second.isCompleted)
            assertEquals(emptyList<MovieEntity>(), storedMovies())

            gate.complete(Unit)
            advanceUntilIdle()

            assertEquals(movies.dtoToDomain(), first.await().getOrNull())
            assertEquals(movies.dtoToDomain(), second.await().getOrNull())
            assertEquals(movies.map { it.id }, storedMovies().map { it.id })
        }

    @Test
    fun `given gated network when refresh is cancelled then it is cancelled and local data is unchanged`() =
        runTest {
            local.save(listOf(firstMovie).dtoToEntity())
            val before = storedMovies()
            network.gate = CompletableDeferred()
            network.enqueueSuccess(movies)
            val job = launch { repository.refresh() }
            runCurrent()
            assertTrue(job.isActive)

            job.cancel()
            advanceUntilIdle()

            assertTrue(job.isCancelled)
            assertTrue(job.isCompleted)
            assertEquals(before, storedMovies())
        }

    // endregion

    private companion object {
        const val UNKNOWN_ID = -1L
    }
}
