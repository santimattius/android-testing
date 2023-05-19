package com.santimattius.template.data.repositories

import com.santimattius.shared_test.data.FakeLocalDataSource
import com.santimattius.shared_test.data.FakeRemoteDataSource
import com.santimattius.shared_test.data.MovieMother
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class TMDbRepositoryTestWithFakes {

    @get:Rule
    val coroutinesTestRule = com.santimattius.shared_test.rules.MainCoroutinesTestRule()

    private val remoteDataSource = FakeRemoteDataSource()
    private val localDataSource = FakeLocalDataSource()
    private val repository: TMDbRepository = TMDbRepository(remoteDataSource, localDataSource)

    @Test
    fun `Given there are movies when make the request the result is not empty`() {
        //Given
        val movies = MovieMother.createMovies()
        remoteDataSource.onMovies = { movies }

        //When
        runTest(coroutinesTestRule.testDispatcher) {
            val result = repository.fetchPopular()
            //Then
            assertThat(result.getOrNull().isNullOrEmpty(), equalTo(false))
        }
    }
}