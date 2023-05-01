package com.santimattius.template.data.repositories

import com.santimattius.template.data.repositories.fakes.FakeLocalDataSource
import com.santimattius.template.data.repositories.fakes.FakeRemoteDataSource
import com.santimattius.template.utils.MainCoroutinesTestRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class TMDbRepositoryTestWithFakes {

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    private val remoteDataSource = FakeRemoteDataSource()
    private val localDataSource = FakeLocalDataSource()
    private val repository: TMDbRepository = TMDbRepository(remoteDataSource, localDataSource)

    @Test
    fun `Given there are movies when make the request the result is not empty`() {
        val movies = MovieMother.createMovies()
        //Given
        remoteDataSource.onMovies = { movies }
        //When
        runTest(coroutinesTestRule.testDispatcher) {
            val result = repository.fetchPopular()
            //Then
            assertThat(result.getOrNull().isNullOrEmpty(), equalTo(false))
        }
    }
}