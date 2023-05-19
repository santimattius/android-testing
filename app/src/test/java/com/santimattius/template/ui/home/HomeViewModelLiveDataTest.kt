package com.santimattius.template.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToUiModel
import com.santimattius.template.ui.home.fakes.FakeMovieRepository
import com.santimattius.template.ui.androidview.home.models.HomeState
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.shared_test.extensions.getOrAwaitValue
import com.santimattius.template.ui.androidview.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    private val movieRepository = FakeMovieRepository()
    private val viewModel = HomeViewModel(movieRepository)

    @Test
    fun `fetch result success`() {
        //Given
        val movies = MovieMother.createMovies().dtoToUiModel()
        //When
        viewModel.popularMovies()
        //Then
        assertThat(viewModel.state.getOrAwaitValue(), equalTo(HomeState.Data(movies)))
    }

    @Test
    @Suppress("TooGenericExceptionCaught")
    fun `fetch result fail`() {
        //Given
        movieRepository.onPopularMovies = { throw Throwable() }
        //When
        viewModel.popularMovies()
        //Then
        assertThat(viewModel.state.getOrAwaitValue(), equalTo(HomeState.Error))
    }
}