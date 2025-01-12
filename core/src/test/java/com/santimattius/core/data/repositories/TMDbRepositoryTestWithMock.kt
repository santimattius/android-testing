package com.santimattius.core.data.repositories

import com.santimattius.test.data.MovieMother
import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.dtoToEntity
import com.santimattius.test.rules.MainCoroutinesTestRule
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.*

class TMDbRepositoryTestWithMock {

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    private lateinit var movieNetworkDataSource: MovieNetworkDataSource
    private lateinit var movieLocalDataSource: MovieLocalDataSource
    private lateinit var repository: TMDbRepository

    @Before
    fun setUp() {
        movieNetworkDataSource = mockk(relaxed = true)
        movieLocalDataSource = mockk(relaxed = true)
        repository = TMDbRepository(movieNetworkDataSource, movieLocalDataSource)
    }

    @After
    fun tearDown() {
        clearMocks(movieNetworkDataSource, movieLocalDataSource)
    }

    @Test
    fun `Given there are movies when make the request the result is not empty`() {
        val movies = MovieMother.createMovies()
        //Given
        coEvery {
            movieNetworkDataSource.getPopularMovies()
        } returns Result.success(movies)

        coEvery {
            movieLocalDataSource.save(any())
        } returns Result.success(true)

        coEvery {
            movieLocalDataSource.getAll()
        } returns movies.dtoToEntity()

        //When
        runTest(coroutinesTestRule.testDispatcher) {
            repository.refresh()
        }

        //Then
        coVerify {
            movieNetworkDataSource.getPopularMovies()
        }
        coVerify {
            movieLocalDataSource.save(any())
        }
        coVerify {
            movieLocalDataSource.getAll()
        }
    }
}