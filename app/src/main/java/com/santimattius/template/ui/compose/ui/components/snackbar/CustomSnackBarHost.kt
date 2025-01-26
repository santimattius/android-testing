package com.santimattius.template.ui.compose.ui.components.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.santimattius.template.ui.compose.ui.theme.confirmation_color

@Composable
fun CustomSnackBarHost(snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(snackBarHostState) { data ->
        val isError = (data.visuals as? SnackBarVisualsWithError)?.isError ?: false
        val buttonColor =
            if (isError) {
                ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            } else {
                ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.inversePrimary
                )
            }

        Snackbar(
            modifier = modifier.padding(16.dp),
            containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else confirmation_color,
            contentColor = Color.White,
            action = {
                TextButton(
                    onClick = { if (isError) data.dismiss() else data.performAction() },
                    colors = buttonColor
                ) {
                    Text(data.visuals.actionLabel ?: "")
                }
            }
        ) {
            Text(data.visuals.message)
        }
    }

}

class SnackBarVisualsWithError(
    override val message: String,
    val isError: Boolean = false
) : SnackbarVisuals {

    override val actionLabel: String
        get() = if (isError) "Error" else "OK"

    override val withDismissAction: Boolean
        get() = false

    override val duration: SnackbarDuration
        get() = SnackbarDuration.Long
}
