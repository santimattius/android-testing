package com.santimattius.template.ui.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santimattius.core.domain.repositories.MovieRepository
import com.santimattius.template.ui.xml.home.models.HomeState
import com.santimattius.template.ui.xml.home.models.MovieUiModel
import com.santimattius.template.ui.xml.home.models.mapping.asUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    val state: StateFlow<HomeState> = movieRepository.all
        .map { it.asUiModels() }
        .map {
            if (it.isEmpty()) {
                HomeState.Empty
            } else {
                HomeState.Data(it)
            }
        }
        .onStart {
            emit(HomeState.Loading)
            movieRepository.refresh()
        }.catch {
            emit(HomeState.Error)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeState.Loading
        )
}