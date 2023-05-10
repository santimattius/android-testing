package com.santimattius.template.ui.home

import app.cash.turbine.test
import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToUiModel
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.ui.androidview.home.models.HomeState
import com.santimattius.template.ui.compose.HomeViewModel
import com.santimattius.template.ui.home.fakes.FakeMovieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelFlowsTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    private val movieRepository = FakeMovieRepository()

    @Test
    fun popularMovies() {
        val movies = MovieMother.createMovies().dtoToUiModel()
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val viewModel = HomeViewModel(movieRepository)
            viewModel.state.test {
                assertThat(
                    awaitItem(),
                    equalTo(HomeState.Data(movies))
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
    }


}