package com.santimattius.core.data.client.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var database: TheMovieDataBase
    private lateinit var dao: MovieDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TheMovieDataBase::class.java).build()
        dao = database.dao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertedMovieCanBeFoundById() = runTest {
        dao.insertAll(movie(id = 1))

        assertEquals(movie(id = 1), dao.findById(1))
        assertNull(dao.findById(2))
    }

    @Test
    fun insertIgnoresExistingIds() = runTest {
        dao.insertAll(movie(id = 1, title = "Original"))

        val result = dao.insertAll(movie(id = 1, title = "Duplicate"))

        assertEquals(listOf(-1L), result)
        assertEquals("Original", dao.findById(1)?.title)
    }

    @Test
    fun upsertUpdatesDetailsAndKeepsFavorite() = runTest {
        dao.insertAll(movie(id = 1, title = "Old"))
        dao.addToFavorite(1)

        dao.upsert(listOf(movie(id = 1, title = "New"), movie(id = 2)))

        assertEquals("New", dao.findById(1)?.title)
        assertEquals(true, dao.findById(1)?.favorite)
        assertEquals(2, dao.count())
    }

    @Test
    fun favoriteCanBeToggled() = runTest {
        dao.insertAll(movie(id = 1))

        assertEquals(1, dao.addToFavorite(1))
        assertEquals(true, dao.findById(1)?.favorite)
        assertEquals(1, dao.removeFromFavorite(1))
        assertEquals(false, dao.findById(1)?.favorite)
    }

    @Test
    fun favoriteOnMissingMovieAffectsNoRows() = runTest {
        assertEquals(0, dao.addToFavorite(99))
    }

    @Test
    fun deleteRemovesTheMovie() = runTest {
        dao.insertAll(movie(id = 1), movie(id = 2))

        dao.delete(movie(id = 1))

        assertEquals(listOf(movie(id = 2)), dao.getAll())
    }

    @Test
    fun searchAllEmitsWhenDataChanges() = runTest {
        dao.searchAll().test {
            assertEquals(emptyList<MovieEntity>(), awaitItem())

            dao.insertAll(movie(id = 1))

            assertEquals(listOf(movie(id = 1)), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun movie(id: Long, title: String = "Title $id") = MovieEntity(
        id = id,
        title = title,
        overview = "Overview $id",
        poster = "/poster$id.jpg",
    )
}
