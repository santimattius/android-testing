package com.santimattius.template.ui.compose

import androidx.lifecycle.viewmodel.testing.viewModelScenario
import app.cash.turbine.test
import com.santimattius.template.ui.compose.models.HomeUiState
import com.santimattius.template.ui.fakes.MockMovieRepository
import com.santimattius.template.ui.models.mapping.asUiModels
import com.santimattius.template.ui.xml.models.HomeState
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import com.santimattius.test.rules.MainCoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class HomeViewModelFlowsTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    @Test
    fun `Given result is success When popular movies Then return movies`() {
        val movies = MovieMother.createMovies().dtoToDomain()
        val repository = MockMovieRepository(onPopularMovies = { movies })
        val expectedState = HomeUiState(movies = movies.asUiModels())
        viewModelScenario { HomeComposeViewModel(repository) }.use { scenario ->
            val viewModel = scenario.viewModel
            runTest(mainCoroutinesTestRule.testDispatcher) {
                viewModel.state.test {
                    assertThat(
                        awaitItem(),
                        equalTo(expectedState)
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

    @Test
    fun `Given result is empty When popular movies Then return empty list`() {
        val repository = MockMovieRepository(onPopularMovies = { emptyList() })
        viewModelScenario { HomeComposeViewModel(repository) }.use {
            val viewModel = it.viewModel
            runTest(mainCoroutinesTestRule.testDispatcher) {
                viewModel.state.test {
                    assertThat(
                        awaitItem().isEmpty,
                        equalTo(true)

                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

    @Suppress("TooGenericExceptionThrown")
    @Test
    fun `Given result is error When popular movies Then return error`() {
        val repository = MockMovieRepository(onPopularMovies = { throw Exception() })
        viewModelScenario { HomeComposeViewModel(repository) }.use { scenario ->
            val viewModel = scenario.viewModel
            runTest(mainCoroutinesTestRule.testDispatcher) {
                viewModel.state.test {
                    val currentState = awaitItem()
                    assertThat(
                        currentState.loadingError,
                        equalTo(true)
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }
}