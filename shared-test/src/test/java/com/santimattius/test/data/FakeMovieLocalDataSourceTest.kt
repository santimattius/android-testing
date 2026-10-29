package com.santimattius.test.data

import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeMovieLocalDataSourceTest {

    @Test
    fun savePublishesANewImmutableSnapshot() = runTest {
        val dataSource = FakeMovieLocalDataSource()
        val initialSnapshot = dataSource.all.first()
        val nextSnapshot = async(start = CoroutineStart.UNDISPATCHED) {
            dataSource.all.drop(1).first()
        }
        val movie = MovieEntity(
            id = 1,
            title = "Title",
            overview = "Overview",
            poster = "/poster.jpg",
        )

        dataSource.save(listOf(movie))

        assertEquals(emptyList<MovieEntity>(), initialSnapshot)
        assertEquals(listOf(movie), withTimeout(100) { nextSnapshot.await() })
    }
}
