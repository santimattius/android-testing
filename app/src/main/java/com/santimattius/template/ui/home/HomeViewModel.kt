package com.santimattius.template.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.santimattius.template.data.repositories.TMDbRepository
import com.santimattius.template.ui.home.models.HomeState
import com.santimattius.template.ui.home.models.mapping.asUiModels
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val movieRepository = TMDbRepository(application)

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