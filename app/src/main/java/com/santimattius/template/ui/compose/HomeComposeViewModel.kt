package com.santimattius.template.ui.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santimattius.core.domain.repositories.MovieRepository
import com.santimattius.template.ui.compose.models.HomeUiState
import com.santimattius.template.ui.compose.models.Messages
import com.santimattius.template.ui.models.MovieUiModel
import com.santimattius.template.ui.models.mapping.asUiModels
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeComposeViewModel(
    private val movieRepository: MovieRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state: StateFlow<HomeUiState> = movieRepository.all.combine(_state) { movies, state ->
        state.copy(movies = movies.asUiModels(), isLoading = false)
    }.catch {
        emit(_state.value.copy(isLoading = false, loadingError = true))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _state.value,
    )

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch(mainDispatcher) {
            movieRepository.refresh().onFailure {
                _state.update { state -> state.copy(isLoading = false, loadingError = true) }
            }
        }
    }

    fun onFavorite(movie: MovieUiModel) {
        viewModelScope.launch(mainDispatcher) {
            val result = if (movie.favorite) {
                movieRepository.removeFromFavorite(movie.id).fold(
                    onSuccess = {
                        Messages.Success("Remove ${movie.title} from favorite")
                    },
                    onFailure = {
                        Messages.Error("Failed to remove ${movie.title} from favorite")
                    },
                )
            } else {
                movieRepository.addToFavorite(movie.id).fold(
                    onSuccess = {
                        Messages.Success("Add ${movie.title}  to favorite")
                    },
                    onFailure = {
                        Messages.Error("Failed to add ${movie.title} to favorite")
                    },
                )
            }
            when (result) {
                is Messages.Error -> {
                    _state.update { it.copy(errorMessages = result) }
                }

                is Messages.Success -> {
                    _state.update { it.copy(successMessages = result) }
                }

                Messages.None -> Unit
            }
        }
    }

    fun onMessageConsumed() {
        _state.update {
            it.copy(
                errorMessages = Messages.None,
                successMessages = Messages.None,
            )
        }
    }
}
