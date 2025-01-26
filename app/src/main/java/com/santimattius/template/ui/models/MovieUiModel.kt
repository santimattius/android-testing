package com.santimattius.template.ui.models

data class MovieUiModel(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val favorite: Boolean = false,
)