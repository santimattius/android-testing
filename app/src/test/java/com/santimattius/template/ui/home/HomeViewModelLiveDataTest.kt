package com.santimattius.template.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.santimattius.core.domain.entities.Movie
import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToDomain
import com.santimattius.shared_test.extensions.getOrAwaitValue
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.ui.home.fakes.FakeMovieRepository
import com.santimattius.template.ui.xml.home.HomeViewModel
import com.santimattius.template.ui.xml.home.models.HomeState
import com.santimattius.template.ui.xml.home.models.MovieUiModel
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