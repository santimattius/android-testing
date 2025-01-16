package com.santimattius.core.data.repositories.mockito// Test unitario para TMDbRepository usando Mockito Kotlin

import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.dtoToEntity
import com.santimattius.core.data.repositories.TMDbRepository
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import com.santimattius.test.rules.MainCoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class TMDbRepositoryTest {

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    private val movieNetworkDataSource: MovieNetworkDataSource = mock()
    private val movieLocalDataSource: MovieLocalDataSource = mock()
    private val repository = TMDbRepository(movieNetworkDataSource, movieLocalDataSource)

    @Test
    fun `getAll fetches from network when local is empty`() = runTest {
        // Arrange
        val response = MovieMother.singleMovie()
        whenever(movieLocalDataSource.isEmpty()).thenReturn(true)
        whenever(movieNetworkDataSource.getPopularMovies()).thenReturn(Result.success(response))
        whenever(movieLocalDataSource.getAll()).thenReturn(response.dtoToEntity())

        // Act
        val result = repository.getAll()

        // Assert
        verify(movieNetworkDataSource).getPopularMovies()
        verify(movieLocalDataSource).save(any())
        assertEquals(response.dtoToDomain(), result)
    }

    @Test
    fun `getAll returns local data when not empty`() = runTest {
        // Arrange
        val response = MovieMother.createMovies()
        whenever(movieLocalDataSource.isEmpty()).thenReturn(false)
        whenever(movieLocalDataSource.getAll()).thenReturn(response.dtoToEntity())

        // Act
        val result = repository.getAll()

        // Assert
        verify(movieNetworkDataSource, never()).getPopularMovies()
        assertEquals(response.dtoToDomain(), result)
    }

    @Test
    fun `refresh updates local data on success`() = runTest {
        // Arrange
        val movies = MovieMother.createMovies()
        whenever(movieNetworkDataSource.getPopularMovies()).thenReturn(Result.success(movies))
        whenever(movieLocalDataSource.getAll()).thenReturn(movies.dtoToEntity())

        // Act
        val result = repository.refresh()

        // Assert
        verify(movieLocalDataSource).save(any())
        assertEquals(Result.success(movies.dtoToDomain()), result)
    }

    @Test
    fun `refresh returns failure on network error`() = runTest {
        // Arrange
        whenever(movieNetworkDataSource.getPopularMovies()).thenReturn(Result.failure(Exception("Network error")))

        // Act
        val result = repository.refresh()

        // Assert
        verify(movieLocalDataSource, never()).save(any())
        assert(result.isFailure)
    }
}
