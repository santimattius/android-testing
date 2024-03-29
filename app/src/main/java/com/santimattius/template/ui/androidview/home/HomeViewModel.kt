package com.santimattius.template.ui.androidview.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santimattius.template.domain.repositories.MovieRepository
import com.santimattius.template.ui.androidview.home.models.HomeState
import com.santimattius.template.ui.androidview.home.models.mapping.asUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private val _state = MutableLiveData<HomeState>()
    val state: LiveData<HomeState>
        get() = _state

    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _state.postValue(HomeState.Error)
    }

    fun popularMovies() {
        _state.postValue(HomeState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val popularMovies = movieRepository.getPopular()
            _state.postValue(HomeState.Data(values = popularMovies.asUiModels()))
        }
    }

    fun refresh() {
        _state.postValue(HomeState.Refreshing)
        fetch()
    }

    private fun fetch() {
        job?.cancel()
        job = viewModelScope.launch(exceptionHandler) {
            val result = movieRepository.fetchPopular()
            result.onSuccess { popularMovies ->
                _state.postValue(HomeState.Data(values = popularMovies.asUiModels()))
            }.onFailure {
                _state.postValue(HomeState.Error)
            }
        }
    }
}