package com.santimattius.template.ui.compose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.santimattius.template.R
import com.santimattius.template.ui.models.MovieUiModel

@Composable
fun MoviesGrid(
    modifier: Modifier = Modifier,
    movies: List<MovieUiModel>,
    onItemClick: (MoviesGridAction) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(dimensionResource(R.dimen.item_min_width)),
        contentPadding = PaddingValues(dimensionResource(R.dimen.x_small)),
        modifier = modifier
    ) {
        items(movies, key = { it.id }) { item ->
            MovieCard(
                item = item,
                modifier = Modifier.clickable { onItemClick(MoviesGridAction.OnClick(item)) },
                onFavoriteClick = { onItemClick(MoviesGridAction.OnFavorite(it)) }
            )
        }
    }
}

sealed class MoviesGridAction{
    data class OnClick(val movie: MovieUiModel) : MoviesGridAction()
    data class OnFavorite(val movie: MovieUiModel) : MoviesGridAction()
}