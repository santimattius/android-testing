package com.santimattius.template.ui.compose

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import com.santimattius.template.R
import com.santimattius.template.ui.compose.ui.components.snackbar.SnackBarVisualsWithError
import kotlinx.coroutines.launch

/** Returns the action that asks for the notification permission and reports the answer. */
@Composable
internal fun rememberEnableNotificationsAction(snackBarHostState: SnackbarHostState): () -> Unit {
    val scope = rememberCoroutineScope()
    val grantedMessage = stringResource(R.string.message_notifications_granted)
    val deniedMessage = stringResource(R.string.message_notifications_denied)
    val disabledMessage = stringResource(R.string.message_notifications_disabled)
    val context = LocalContext.current
    fun report(message: String, isError: Boolean) {
        scope.launch { snackBarHostState.showSnackbar(SnackBarVisualsWithError(message, isError)) }
    }
    val contract = ActivityResultContracts.RequestPermission()
    val launcher = rememberLauncherForActivityResult(contract) { granted ->
        report(if (granted) grantedMessage else deniedMessage, !granted)
    }
    return {
        // The permission only exists from Android 13. Earlier versions have nothing to ask for,
        // but the user can still turn the app's notifications off in system settings.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            report(grantedMessage, false)
        } else {
            report(disabledMessage, true)
        }
    }
}
