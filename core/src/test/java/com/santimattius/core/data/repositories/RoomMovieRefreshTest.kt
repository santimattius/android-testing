package com.santimattius.core.data.repositories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.datasources.implementation.RoomMovieLocalDataSource
import com.santimattius.core.data.dtoToEntity
import com.santimattius.test.data.FakeMovieNetworkDataSource
import com.santimattius.test.data.MovieMother
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [30])
class RoomMovieRefreshTest {

    private lateinit var database: TheMovieDataBase
    private lateinit var local: RoomMovieLocalDataSource
    private val network = FakeMovieNetworkDataSource()
    private val movies = MovieMother.createMovies()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TheMovieDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        local = RoomMovieLocalDataSource(database)
    }

    @After
    fun tearDown() {
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
    fun `empty network response clears a populated cache`() = runTest {
        local.save(movies.dtoToEntity()).getOrThrow()
        val repository = TMDbRepository(network, local)
        network.enqueueSuccess(emptyList())

        val result = repository.refresh().getOrThrow()

        assertTrue(result.isEmpty())
        assertTrue(repository.all.first().isEmpty())
    }
}
