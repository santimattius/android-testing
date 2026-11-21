package com.santimattius.template.ui.xml

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.santimattius.template.ui.models.mapping.asUiModels
import com.santimattius.template.ui.xml.home.HomeViewModel
import com.santimattius.template.ui.xml.models.HomeState
import com.santimattius.test.data.FakeMovieRepository
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import com.santimattius.test.extensions.getOrAwaitValue
import com.santimattius.test.rules.MainCoroutinesTestRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    @Test
    fun `popular movies publishes data`() {
        val movies = MovieMother.createMovies().dtoToDomain()
        val viewModel = HomeViewModel(FakeMovieRepository(movies))

        viewModel.popularMovies()

        assertEquals(HomeState.Data(movies.asUiModels()), viewModel.state.getOrAwaitValue())
    }

    @Test
    fun `popular movies publishes error when loading throws`() {
        val repository = FakeMovieRepository().apply {
            getAllFailure = IllegalStateException("loading failed")
        }
        val viewModel = HomeViewModel(repository)

        viewModel.popularMovies()

        assertEquals(HomeState.Error, viewModel.state.getOrAwaitValue())
    }

    @Test
    fun `refresh cancels an in-flight popular movies load`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val refreshedMovies = listOf(
                MovieMother.createMovie(id = 2, title = "Refreshed"),
            )
            val gate = CompletableDeferred<Unit>()
            val repository = FakeMovieRepository().apply {
                getAllGate = gate
                refreshResult = Result.success(refreshedMovies)
            }
            val viewModel = HomeViewModel(repository)
            val observedStates = mutableListOf<HomeState>()
            val observer = Observer<HomeState> { observedStates += it }
            viewModel.state.observeForever(observer)

            try {
                viewModel.popularMovies()
                viewModel.refresh()
                gate.complete(Unit)
                advanceUntilIdle()

                assertEquals(
                    listOf(
                        HomeState.Loading,
                        HomeState.Refreshing,
                        HomeState.Data(refreshedMovies.asUiModels()),
                    ),
                    observedStates,
                )
            } finally {
                viewModel.state.removeObserver(observer)
            }
        }

    @Test
    fun `refresh publishes refreshing before refreshed data`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val movies = MovieMother.createMovies().dtoToDomain()
            val viewModel = HomeViewModel(FakeMovieRepository(movies))
            val observedStates = mutableListOf<HomeState>()
            val observer = Observer<HomeState> { observedStates += it }
            viewModel.state.observeForever(observer)

            try {
                viewModel.refresh()

                assertEquals(
                    listOf(HomeState.Refreshing, HomeState.Data(movies.asUiModels())),
                    observedStates,
                )
            } finally {
                viewModel.state.removeObserver(observer)
            }
        }
}
