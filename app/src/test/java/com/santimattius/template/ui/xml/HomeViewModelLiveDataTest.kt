package com.santimattius.template.ui.xml

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.dtoToDomain
import com.santimattius.test.extensions.getOrAwaitValue
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.template.ui.fakes.FakeMovieRepository
import com.santimattius.template.ui.xml.home.HomeViewModel
import com.santimattius.template.ui.xml.home.models.HomeState
import com.santimattius.template.ui.xml.home.models.mapping.asUiModels
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

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
        val movies = MovieMother.createMovies().dtoToDomain()
        //When
        viewModel.popularMovies()
        //Then
        assertThat(
            viewModel.state.getOrAwaitValue(),
            equalTo(HomeState.Data(movies.asUiModels()))
        )
    }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `fetch result fail`() {
        //Given
        movieRepository.onPopularMovies = { throw Throwable() }
        //When
        viewModel.popularMovies()
        //Then
        assertThat(viewModel.state.getOrAwaitValue(), equalTo(HomeState.Error))
    }
}