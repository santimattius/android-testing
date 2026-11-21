package com.santimattius.template.ui.xml.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santimattius.core.domain.repositories.MovieRepository
import com.santimattius.template.ui.models.mapping.asUiModels
import com.santimattius.template.ui.xml.models.HomeState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val movieRepository: MovieRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
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
        launchReplacing {
            val popularMovies = movieRepository.getAll()
            _state.postValue(HomeState.Data(values = popularMovies.asUiModels()))
        }
    }

    fun refresh() {
        _state.postValue(HomeState.Refreshing)
        launchReplacing {
            movieRepository.refresh()
                .onSuccess { popularMovies ->
                    _state.postValue(HomeState.Data(values = popularMovies.asUiModels()))
                }
                .onFailure {
                    _state.postValue(HomeState.Error)
                }
        }
    }

    private fun launchReplacing(block: suspend () -> Unit) {
        job?.cancel()
        job = viewModelScope.launch(mainDispatcher + exceptionHandler) {
            block()
        }
    }
}
