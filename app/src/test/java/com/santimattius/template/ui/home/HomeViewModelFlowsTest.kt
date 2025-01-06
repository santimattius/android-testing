package com.santimattius.template.ui.home

import app.cash.turbine.test
import com.santimattius.core.domain.entities.Movie
import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToDomain
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.ui.compose.HomeViewModel
import com.santimattius.template.ui.home.fakes.FakeMovieRepository
import com.santimattius.template.ui.xml.home.models.HomeState
import com.santimattius.template.ui.xml.home.models.MovieUiModel
import com.santimattius.template.ui.xml.home.models.mapping.asUiModels
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class HomeViewModelFlowsTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    private val movieRepository = FakeMovieRepository()

    @Test
    fun popularMovies() {
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
}