package com.santimattius.template.ui.home

import app.cash.turbine.test
import com.santimattius.template.data.repositories.MovieMother
import com.santimattius.template.data.repositories.dtoToUiModel
import com.santimattius.template.ui.home.fakes.FakeMovieRepository
import com.santimattius.template.ui.home.models.HomeState
import com.santimattius.template.utils.MainCoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelFlowsTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    private val movieRepository = FakeMovieRepository()
    private val viewModel = HomeViewModel2(movieRepository)

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun popularMovies() {
        val movies = MovieMother.createMovies().dtoToUiModel()
        runTest(mainCoroutinesTestRule.testDispatcher) {
            viewModel.popularMovies()
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