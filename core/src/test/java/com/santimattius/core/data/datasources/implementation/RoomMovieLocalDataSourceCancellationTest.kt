package com.santimattius.core.data.datasources.implementation

import com.santimattius.core.data.client.database.MovieDao
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class RoomMovieLocalDataSourceCancellationTest {

    @Test
    fun `isEmpty propagates cancellation from Room`() = runTest {
        val cancellation = CancellationException("cancelled")
        val dataSource = RoomMovieLocalDataSource(CancellingCountMovieDao(cancellation))

        try {
            dataSource.isEmpty()
            fail("Expected cancellation to propagate")
        } catch (actual: CancellationException) {
            assertSame(cancellation, actual)
        }
    }

    private class CancellingCountMovieDao(
        private val cancellation: CancellationException,
    ) : MovieDao {
        override fun searchAll(): Flow<List<MovieEntity>> = emptyFlow()

        override suspend fun getAll(): List<MovieEntity> = notUsed()

        override suspend fun findById(id: Long): MovieEntity? = notUsed()

        override suspend fun count(): Int = throw cancellation

        override suspend fun insertAll(vararg movies: MovieEntity): List<Long> = notUsed()

        override suspend fun delete(movies: MovieEntity): Unit = notUsed()

        override suspend fun update(movies: MovieEntity): Unit = notUsed()

        override suspend fun addToFavorite(movieId: Long): Int = notUsed()

        override suspend fun removeFromFavorite(movieId: Long): Int = notUsed()

        override suspend fun updateMovieDetails(
            id: Long,
            title: String,
            overview: String,
            poster: String,
        ): Unit = notUsed()

        private fun <T> notUsed(): T = error("Not used by this test")
    }
}
