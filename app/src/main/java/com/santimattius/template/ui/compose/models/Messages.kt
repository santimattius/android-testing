package com.santimattius.template.ui.compose.models

sealed class Messages {
    data object None : Messages()
    data class Success(val message: String) : Messages()
    data class Error(val message: String) : Messages()
}