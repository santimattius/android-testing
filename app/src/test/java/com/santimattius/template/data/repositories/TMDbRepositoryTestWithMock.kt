package com.santimattius.template.data.repositories

import com.santimattius.template.data.datasources.LocalDataSource
import com.santimattius.template.data.datasources.RemoteDataSource
import com.santimattius.template.data.dtoToEntity
import com.santimattius.template.utils.MainCoroutinesTestRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class TMDbRepositoryTestWithMock {

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    private val remoteDataSource: RemoteDataSource = mockk(relaxed = true)
    private val localDataSource: LocalDataSource = mockk(relaxed = true)
    private val repository: TMDbRepository = TMDbRepository(remoteDataSource, localDataSource)

    @After
    fun tearDown() {
        clearMocks(remoteDataSource, localDataSource)
    }

    @Test
    fun `Given there are movies when make the request the result is not empty`() {
        val movies = MovieMother.createMovies()
        //Given
        coEvery {
            remoteDataSource.getPopularMovies()
        } returns Result.success(movies)

        coEvery {
            localDataSource.save(any())
        } returns Result.success(true)

        coEvery {
            localDataSource.getAll()
        } returns movies.dtoToEntity()

        //When
        runTest(coroutinesTestRule.testDispatcher) {
            repository.fetchPopular()
        }

        //Then
        coVerify {
            remoteDataSource.getPopularMovies()
        }
        coVerify {
            localDataSource.save(any())
        }
        coVerify {
            localDataSource.getAll()
        }
    }
}