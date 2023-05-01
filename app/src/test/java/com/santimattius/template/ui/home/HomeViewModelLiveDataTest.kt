package com.santimattius.template.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.santimattius.template.data.repositories.MovieMother
import com.santimattius.template.data.repositories.dtoToUiModel
import com.santimattius.template.ui.home.fakes.FakeMovieRepository
import com.santimattius.template.ui.home.models.HomeState
import com.santimattius.template.utils.MainCoroutinesTestRule
import com.santimattius.template.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    private val movieRepository = FakeMovieRepository()
    private val viewModel = HomeViewModel(movieRepository)

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

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
    fun `fetch result fail`() {
        //Given
        movieRepository.onPopularMovies = { throw Throwable() }
        //When
        viewModel.popularMovies()
        //Then
        assertThat(viewModel.state.getOrAwaitValue(), equalTo(HomeState.Error))
    }
}