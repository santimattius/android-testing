package com.santimattius.template.ui.home.models

sealed interface HomeState {
    object Loading : HomeState
    object Error : HomeState
    object Refreshing : HomeState
    object Completed : HomeState
    data class Data(val values: List<MovieUiModel>) : HomeState
}
