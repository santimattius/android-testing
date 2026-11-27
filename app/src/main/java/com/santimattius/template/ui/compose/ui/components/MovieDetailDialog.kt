package com.santimattius.template.ui.compose.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.santimattius.template.R
import com.santimattius.template.ui.models.MovieUiModel

@Composable
internal fun MovieDetailDialog(movie: MovieUiModel, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = movie.title, modifier = Modifier.testTag("movie_detail_title")) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.button_text_close))
            }
        },
    )
}
