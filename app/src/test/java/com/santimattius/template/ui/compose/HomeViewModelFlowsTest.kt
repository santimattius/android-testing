package com.santimattius.template.ui.compose

import androidx.lifecycle.viewmodel.testing.viewModelScenario
import app.cash.turbine.test
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.template.ui.fakes.MockMovieRepository
import com.santimattius.template.ui.xml.home.models.HomeState
import com.santimattius.template.ui.xml.home.models.mapping.asUiModels
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
        viewModelScenario { HomeViewModel(repository) }.use { scenario ->
            val viewModel = scenario.viewModel
            runTest(mainCoroutinesTestRule.testDispatcher) {
                viewModel.state.test {
                    assertThat(
                        awaitItem(),
                        equalTo(HomeState.Data(movies.asUiModels()))
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

    @Test
    fun `Given result is empty When popular movies Then return empty list`() {
        val repository = MockMovieRepository(onPopularMovies = { emptyList() })
        viewModelScenario { HomeViewModel(repository) }.use {
            val viewModel = it.viewModel
            runTest(mainCoroutinesTestRule.testDispatcher) {
                viewModel.state.test {
                    assertThat(
                        awaitItem(),
                        equalTo(HomeState.Empty)

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
        viewModelScenario { HomeViewModel(repository) }.use { scenario ->
            val viewModel = scenario.viewModel
            runTest(mainCoroutinesTestRule.testDispatcher) {
                viewModel.state.test {
                    assertThat(
                        awaitItem(),
                        equalTo(HomeState.Error)
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }
}