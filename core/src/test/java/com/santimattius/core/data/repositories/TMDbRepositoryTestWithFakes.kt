package com.santimattius.core.data.repositories

import com.santimattius.test.data.FakeMovieLocalDataSource
import com.santimattius.test.data.FakeMovieNetworkDataSource
import com.santimattius.test.data.MovieMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class TMDbRepositoryTestWithFakes {

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    private val remoteDataSource = FakeMovieNetworkDataSource()
    private val localDataSource = FakeMovieLocalDataSource()
    private val repository: TMDbRepository = TMDbRepository(remoteDataSource, localDataSource)

    @Test
    fun `Given there are movies when make the request the result is not empty`() {
        //Given
        val movies = MovieMother.createMovies()
        remoteDataSource.onMovies = { movies }

        //When
        runTest(coroutinesTestRule.testDispatcher) {
            val result = repository.refresh()
            //Then
            assertThat(result.getOrNull().isNullOrEmpty(), equalTo(false))
        }
    }
}