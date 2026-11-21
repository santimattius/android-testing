package com.santimattius.template.ui.compose

import androidx.lifecycle.viewmodel.testing.viewModelScenario
import app.cash.turbine.test
import com.santimattius.template.ui.compose.models.HomeUiState
import com.santimattius.template.ui.models.mapping.asUiModels
import com.santimattius.test.data.FakeMovieRepository
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import com.santimattius.test.rules.MainCoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelFlowsTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    @Test
    fun `movies replace loading state when collection starts`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val movies = MovieMother.createMovies().dtoToDomain()
            val repository = FakeMovieRepository(movies)

            viewModelScenario { HomeComposeViewModel(repository) }.use { scenario ->
                scenario.viewModel.state.test {
                    assertEquals(HomeUiState(movies = movies.asUiModels()), awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

    @Test
    fun `empty movies clear loading when collection starts`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val repository = FakeMovieRepository(emptyList())

            viewModelScenario { HomeComposeViewModel(repository) }.use { scenario ->
                scenario.viewModel.state.test {
                    val loadedState = awaitItem()
                    assertTrue(loadedState.isEmpty)
                    assertFalse(loadedState.isLoading)
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

    @Test
    fun `refresh failure keeps cached movies and exposes loading error`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val movies = MovieMother.createMovies().dtoToDomain()
            val repository = FakeMovieRepository(movies).apply {
                refreshResult = Result.failure(IllegalStateException("refresh failed"))
            }

            viewModelScenario { HomeComposeViewModel(repository) }.use { scenario ->
                scenario.viewModel.state.test {
                    val errorState = awaitItem()
                    assertEquals(movies.asUiModels(), errorState.movies)
                    assertTrue(errorState.loadingError)
                    assertFalse(errorState.isLoading)
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

    @Test
    fun `refresh starts before the state has a collector`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val refreshedMovies = MovieMother.createMovies().dtoToDomain()
            val repository = FakeMovieRepository(emptyList()).apply {
                refreshResult = Result.success(refreshedMovies)
            }

            viewModelScenario { HomeComposeViewModel(repository) }.use { scenario ->
                scenario.viewModel
                runCurrent()
                assertEquals(refreshedMovies, repository.getAll())
            }
        }

    @Test
    fun `movie stream failure clears loading and exposes loading error`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val failure = IllegalStateException("movie stream failed")
            val repository = FakeMovieRepository().apply { allFailure = failure }

            viewModelScenario { HomeComposeViewModel(repository) }.use { scenario ->
                scenario.viewModel.state.test {
                    val errorState = awaitItem()
                    assertTrue(errorState.loadingError)
                    assertFalse(errorState.isLoading)
                    assertTrue(errorState.movies.isEmpty())
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
}
