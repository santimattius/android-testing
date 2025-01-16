package com.santimattius.core.data.repositories.mockk

import com.santimattius.test.data.MovieMother
import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.dtoToEntity
import com.santimattius.core.data.repositories.TMDbRepository
import com.santimattius.test.data.dtoToDomain
import com.santimattius.test.rules.MainCoroutinesTestRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TMDbRepositoryTest {

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
    fun `getAll fetches from network when local is empty`() = runTest {
        // Arrange
        val response = MovieMother.singleMovie()
        coEvery { movieLocalDataSource.isEmpty() } returns true
        coEvery { movieNetworkDataSource.getPopularMovies() } returns Result.success(response)
        coEvery { movieLocalDataSource.getAll() } returns response.dtoToEntity()

        // Act
        val result = repository.getAll()

        // Assert
        coVerify { movieNetworkDataSource.getPopularMovies() }
        coVerify { movieLocalDataSource.save(any()) }
        assertEquals(response.dtoToDomain(), result)
    }

    @Test
    fun `getAll returns local data when not empty`() = runTest {
        // Arrange
        val response = MovieMother.createMovies()
        coEvery { movieLocalDataSource.isEmpty() } returns false
        coEvery { movieLocalDataSource.getAll() } returns response.dtoToEntity()

        // Act
        val result = repository.getAll()

        // Assert
        coVerify(exactly = 0) { movieNetworkDataSource.getPopularMovies() }
        assertEquals(response.dtoToDomain(), result)
    }

    @Test
    fun `refresh updates local data on success`() = runTest {
        // Arrange
        val response = MovieMother.createMovies()
        coEvery { movieNetworkDataSource.getPopularMovies() } returns Result.success(response)
        coEvery { movieLocalDataSource.getAll() } returns response.dtoToEntity()

        // Act
        val result = repository.refresh()

        // Assert
        coVerify { movieLocalDataSource.save(any()) }
        assertEquals(Result.success(response.dtoToDomain()), result)
    }

    @Test
    fun `refresh returns failure on network error`() = runTest {
        // Arrange
        coEvery { movieNetworkDataSource.getPopularMovies() } returns Result.failure(Exception("Network error"))

        // Act
        val result = repository.refresh()

        // Assert
        coVerify(exactly = 0) { movieLocalDataSource.save(any()) }
        assert(result.isFailure)
    }
}