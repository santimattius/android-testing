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
class TMDbRepositoryTest {

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    private val remoteDataSource = mockk<RemoteDataSource>()
    private val localDataSource = mockk<LocalDataSource>()
    private val repository: TMDbRepository = TMDbRepository(remoteDataSource, localDataSource)

    @After
    fun tearDown() {
        clearMocks(remoteDataSource, localDataSource)
    }

    @Test
    fun `first test example`() {
        val movies = MovieMother.getMovies()
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