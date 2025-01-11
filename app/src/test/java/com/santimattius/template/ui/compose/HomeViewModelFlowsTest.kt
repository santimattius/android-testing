package com.santimattius.template.ui.compose

import app.cash.turbine.test
import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToDomain
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.ui.fakes.FakeMovieRepository
import com.santimattius.template.ui.xml.home.models.HomeState
import com.santimattius.template.ui.xml.home.models.mapping.asUiModels
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

//TODO: use view model scenario
class HomeViewModelFlowsTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    @Test
    fun `Given result is success When popular movies Then return movies`() {
        val movies = MovieMother.createMovies().dtoToDomain()
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val viewModel = HomeViewModel(FakeMovieRepository())
            viewModel.state.test {
                assertThat(
                    awaitItem(),
                    equalTo(HomeState.Data(movies.asUiModels()))
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `Given result is empty When popular movies Then return empty list`() {
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val viewModel = HomeViewModel(FakeMovieRepository(onPopularMovies = { emptyList() }))
            viewModel.state.test {
                assertThat(
                    awaitItem(),
                    equalTo(HomeState.Empty)

                )
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `Given result is error When popular movies Then return error`() {
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val viewModel =
                HomeViewModel(FakeMovieRepository(onPopularMovies = { throw Exception() }))
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