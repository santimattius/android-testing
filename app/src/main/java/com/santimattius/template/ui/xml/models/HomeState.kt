package com.santimattius.template.ui.xml.models

import com.santimattius.template.ui.models.MovieUiModel

sealed interface HomeState {
    data object Loading : HomeState
    data object Error : HomeState
    data object Refreshing : HomeState
    data object Completed : HomeState
    data object Empty : HomeState
    data class Data(val values: List<MovieUiModel>) : HomeState
}
