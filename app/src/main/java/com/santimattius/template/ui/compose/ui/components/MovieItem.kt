package com.santimattius.template.ui.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.santimattius.template.R
import com.santimattius.template.ui.models.MovieUiModel

private const val IMAGE_ASPECT_RATIO = 0.67f

@Composable
fun MovieCard(
    modifier: Modifier = Modifier,
    item: MovieUiModel,
    onFavoriteClick: (MovieUiModel) -> Unit
) {
    Card(
        modifier = modifier
            .testTag("card_${item.title}")
            .padding(dimensionResource(R.dimen.item_movie_padding)),
    ) {
        Box {
            SubcomposeAsyncImage(
                model = item.imageUrl,
                loading = {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .aspectRatio(ratio = IMAGE_ASPECT_RATIO),
            )
            IconButton(
                onClick = { onFavoriteClick(item) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .testTag("favorite_${item.title}")
            ) {
                Icon(
                    imageVector = if (item.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Add ${item.title} to Favorite",
                    tint = if (item.favorite) Color.Red else Color.White
                )
            }
        }
    }
}