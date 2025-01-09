package com.santimattius.template.ui.home

import app.cash.turbine.test
import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToDomain
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.ui.compose.HomeViewModel
import com.santimattius.template.ui.home.fakes.FakeMovieRepository
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

    private val movieRepository = FakeMovieRepository()

    @Test
    fun `Given result is success When popular movies Then return movies`() {
        val movies = MovieMother.createMovies().dtoToDomain()
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val viewModel = HomeViewModel(movieRepository)
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
            val viewModel = HomeViewModel(FakeMovieRepository { emptyList() })
            viewModel.state.test {
                assertThat(
                    awaitItem(),
                    equalTo(HomeState.Data(emptyList()))
                )
            }
        }
    }

    @Test
    fun `Given result is error When popular movies Then return error`() {

    }
}