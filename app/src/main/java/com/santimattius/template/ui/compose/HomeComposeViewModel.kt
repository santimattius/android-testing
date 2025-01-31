package com.santimattius.template.ui.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santimattius.core.domain.repositories.MovieRepository
import com.santimattius.template.ui.compose.models.HomeUiState
import com.santimattius.template.ui.compose.models.Messages
import com.santimattius.template.ui.models.MovieUiModel
import com.santimattius.template.ui.models.mapping.asUiModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeComposeViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state: StateFlow<HomeUiState> = movieRepository.all.combine(_state) { movies, state ->
        state.copy(movies = movies.asUiModels(), isLoading = false, loadingError = false)
    }.onStart {
        movieRepository.refresh()
    }.catch {
        emit(HomeUiState(loadingError = true))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _state.value
    )

    fun onFavorite(movie: MovieUiModel) {
        viewModelScope.launch {
            val result = if (movie.favorite) {
                movieRepository.removeFromFavorite(movie.id).fold(
                    onSuccess = {
                        Messages.Success("Remove ${movie.title} from favorite")
                    }, onFailure = {
                        Messages.Error("Failed to remove ${movie.title} from favorite")
                    }
                )
            } else {
                movieRepository.addToFavorite(movie.id).fold(
                    onSuccess = {
                        Messages.Success("Add ${movie.title}  to favorite")
                    }, onFailure = {
                        Messages.Error("Failed to add ${movie.title} to favorite")
                    }
                )
            }
            when (result) {
                is Messages.Error -> {
                    _state.update { it.copy(errorMessages = result) }
                }

                is Messages.Success -> {
                    _state.update { it.copy(successMessages = result) }
                }

                Messages.None -> {}
            }
        }
    }
}