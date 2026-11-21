package com.santimattius.core.data.repositories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.datasources.implementation.RoomMovieLocalDataSource
import com.santimattius.core.data.dtoToEntity
import com.santimattius.test.data.FakeMovieNetworkDataSource
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [30])
class RoomMovieRefreshTest {

    private lateinit var database: TheMovieDataBase
    private lateinit var local: RoomMovieLocalDataSource
    private val network = FakeMovieNetworkDataSource()
    private val movies = MovieMother.createMovies()
    private val deleteReached = CompletableDeferred<Unit>()
    private val releaseDelete = CountDownLatch(1)
    private val gateArmed = AtomicBoolean(false)
    private val gateTimedOut = AtomicBoolean(false)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TheMovieDataBase::class.java)
            .allowMainThreadQueries()
            .setQueryCallback({ sql, _ ->
                if (sql == "DELETE FROM `movie` WHERE `id` = ?" && gateArmed.compareAndSet(true, false)) {
                    deleteReached.complete(Unit)
                    if (!releaseDelete.await(10, TimeUnit.SECONDS)) gateTimedOut.set(true)
                }
            }, { command -> command.run() })
            .build()
        local = RoomMovieLocalDataSource(database)
    }

    @After
    fun tearDown() {
        releaseDelete.countDown()
        database.close()
    }

    @Test
    fun `failed stale deletion rolls back incoming content and preserves favorites`() = runTest {
        local.save(movies.dtoToEntity()).getOrThrow()
        local.addToFavorite(movies.first().id).getOrThrow()
        val repository = TMDbRepository(network, local)
        val before = repository.getAll()
        database.openHelper.writableDatabase.execSQL(
            "CREATE TRIGGER reject_delete BEFORE DELETE ON movie " +
                "BEGIN SELECT RAISE(ABORT, 'deletion rejected'); END",
        )
        network.enqueueSuccess(listOf(movies.first().copy(title = "Updated title")))

        val result = repository.refresh()

        assertTrue(result.isFailure)
        assertEquals(before, repository.getAll())
    }

    @Test
    fun `cancellation at stale deletion rolls back refresh and preserves favorites`() = runTest {
        local.save(movies.dtoToEntity()).getOrThrow()
        local.addToFavorite(movies.first().id).getOrThrow()
        val repository = TMDbRepository(network, local)
        val before = repository.getAll()
        network.enqueueSuccess(listOf(movies.first().copy(title = "Updated title")))
        gateArmed.set(true)
        val refresh = launch { repository.refresh() }

        try {
            deleteReached.await()
            refresh.cancel()
        } finally {
            releaseDelete.countDown()
            refresh.join()
        }

        assertFalse(gateTimedOut.get())
        assertTrue(refresh.isCancelled)
        assertEquals(before, repository.getAll())
    }

    @Test
    fun `successful replacement removes stale rows and retains existing favorite`() = runTest {
        local.save(movies.dtoToEntity()).getOrThrow()
        local.addToFavorite(movies.first().id).getOrThrow()
        val repository = TMDbRepository(network, local)
        network.enqueueSuccess(listOf(movies.first().copy(title = "Updated title")))

        val result = repository.refresh().getOrThrow()

        assertEquals(1, result.size)
        assertEquals("Updated title", result.single().title)
        assertTrue(result.single().favorite)
        assertEquals(result, repository.getAll())
    }

    @Test
    fun `overlapping distinct refreshes return their own committed Room snapshots`() = runTest {
        local.save(movies.dtoToEntity()).getOrThrow()
        local.addToFavorite(movies.first().id).getOrThrow()
        val firstPayload = listOf(movies.first().copy(title = "First refresh"))
        val secondPayload = listOf(movies.first().copy(title = "Second refresh"))
        network.enqueueSuccess(firstPayload)
        network.enqueueSuccess(secondPayload)
        val repository = TMDbRepository(network, local)
        gateArmed.set(true)
        val first = async { repository.refresh().getOrThrow() }
        val second = try {
            deleteReached.await()
            async { repository.refresh().getOrThrow() }.also {
                runCurrent()
                assertFalse(it.isCompleted)
            }
        } finally {
            releaseDelete.countDown()
        }

        val firstSnapshot = first.await()
        val secondSnapshot = second.await()
        val expectedFirst = firstPayload.dtoToDomain().map { it.copy(favorite = true) }
        val expectedSecond = secondPayload.dtoToDomain().map { it.copy(favorite = true) }
        assertFalse(gateTimedOut.get())
        assertEquals(expectedFirst, firstSnapshot)
        assertEquals(expectedSecond, secondSnapshot)
        assertEquals(expectedSecond, repository.getAll())
        assertEquals(expectedFirst, firstSnapshot)
    }

    @Test
    fun `empty network response clears a populated cache`() = runTest {
        local.save(movies.dtoToEntity()).getOrThrow()
        val repository = TMDbRepository(network, local)
        network.enqueueSuccess(emptyList())

        val result = repository.refresh().getOrThrow()

        assertTrue(result.isEmpty())
        assertTrue(repository.all.first().isEmpty())
    }
}
