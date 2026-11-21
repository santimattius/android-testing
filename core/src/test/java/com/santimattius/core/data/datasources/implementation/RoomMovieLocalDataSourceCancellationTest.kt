package com.santimattius.core.data.datasources.implementation

import com.santimattius.core.data.client.database.MovieDao
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class RoomMovieLocalDataSourceCancellationTest {

    @Test
    fun `Room wrappers propagate cancellation without changing stored movies`() = runTest {
        val cancellation = CancellationException("cancelled")
        val movie = MovieEntity(1, "Title", "Overview", "/poster.jpg", favorite = true)
        val before = listOf(movie)
        val dataSource = RoomMovieLocalDataSource(CancellingMovieDao(cancellation, before))
        val operations: List<suspend () -> Unit> = listOf(
            { dataSource.isEmpty() },
            { dataSource.save(listOf(movie)) },
            { dataSource.replace(listOf(movie)) },
            { dataSource.find(movie.id) },
            { dataSource.delete(movie) },
            { dataSource.update(movie) },
            { dataSource.addToFavorite(movie.id) },
            { dataSource.removeFromFavorite(movie.id) },
        )

        for (operation in operations) {
            try {
                operation()
                fail("Expected cancellation to propagate")
            } catch (actual: CancellationException) {
                assertSame(cancellation, actual)
            }
            assertEquals(before, dataSource.getAll())
        }
    }

    private class CancellingMovieDao(
        private val cancellation: CancellationException,
        private val movies: List<MovieEntity>,
    ) : MovieDao {
        override fun searchAll(): Flow<List<MovieEntity>> = flowOf(movies)

        override suspend fun getAll(): List<MovieEntity> = movies

        override suspend fun findById(id: Long): MovieEntity? = throw cancellation

        override suspend fun count(): Int = throw cancellation

        override suspend fun insertAll(vararg movies: MovieEntity): List<Long> = throw cancellation

        override suspend fun delete(movies: MovieEntity): Unit = throw cancellation

        override suspend fun update(movies: MovieEntity): Unit = throw cancellation

        override suspend fun addToFavorite(movieId: Long): Int = throw cancellation

        override suspend fun removeFromFavorite(movieId: Long): Int = throw cancellation

        override suspend fun updateMovieDetails(
            id: Long,
            title: String,
            overview: String,
            poster: String,
        ): Unit = throw cancellation
    }
}
