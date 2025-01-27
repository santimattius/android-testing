package com.santimattius.template.ui.compose.models

import com.santimattius.template.ui.models.MovieUiModel

data class HomeUiState(
    val movies: List<MovieUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val loadingError:Boolean = false,
    val errorMessages: Messages = Messages.None,
    val successMessages: Messages = Messages.None,
){
    val hasError: Boolean
        get() = errorMessages != Messages.None

    val hasSuccess: Boolean
        get() = successMessages != Messages.None

    val isEmpty: Boolean
        get() = movies.isEmpty()

}