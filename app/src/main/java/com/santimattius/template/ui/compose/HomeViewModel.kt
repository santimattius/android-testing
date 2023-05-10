package com.santimattius.template.ui.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santimattius.template.domain.repositories.MovieRepository
import com.santimattius.template.ui.androidview.home.models.HomeState
import com.santimattius.template.ui.androidview.home.models.mapping.asUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _state.update { HomeState.Error }
    }

    init {
        popularMovies()
    }

    private fun popularMovies() {
        _state.update { HomeState.Loading }
        viewModelScope.launch(exceptionHandler) {
            val popularMovies = movieRepository.getPopular()
            _state.update { HomeState.Data(values = popularMovies.asUiModels()) }
        }
    }
}