package com.santimattius.template.ui.compose

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santimattius.template.R
import com.santimattius.template.ui.compose.models.HomeUiState
import com.santimattius.template.ui.compose.models.Messages
import com.santimattius.template.ui.compose.ui.components.AppBar
import com.santimattius.template.ui.compose.ui.components.AppBarIcon
import com.santimattius.template.ui.compose.ui.components.AppBarIconModel
import com.santimattius.template.ui.compose.ui.components.Center
import com.santimattius.template.ui.compose.ui.components.MoviesGrid
import com.santimattius.template.ui.compose.ui.components.MoviesGridAction
import com.santimattius.template.ui.compose.ui.components.snackbar.CustomSnackBarHost
import com.santimattius.template.ui.compose.ui.components.snackbar.SnackBarVisualsWithError
import com.santimattius.template.ui.compose.ui.theme.AndroidTestingTheme
import com.santimattius.template.ui.xml.home.HomeViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTestingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeRoute()
                }
            }
        }
    }
}

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { CustomSnackBarHost(snackBarHostState) },
        topBar = {
            val context = LocalContext.current
            AppBar(
                title = stringResource(id = R.string.title_activity_home_compose),
                actions = {
                    AppBarIcon(
                        navIcon = AppBarIconModel(
                            icon = Icons.Default.Android,
                            contentDescription = "",
                        ) {
                            context.startActivity(Intent(context, HomeViewActivity::class.java))
                        },
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current
            HomeScreenContent(state = state) { action ->
                when (action) {
                    is MoviesGridAction.OnClick -> {
                        val title = action.movie.title
                        Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
                    }

                    is MoviesGridAction.OnFavorite -> {
                        viewModel.onFavorite(action.movie)
                    }
                }
            }
            LaunchedEffect(state) {
                when {
                    state.successMessages is Messages.Success -> {
                        val message = (state.successMessages as Messages.Success).message
                        snackBarHostState.showSnackbar(SnackBarVisualsWithError(message))
                    }

                    state.errorMessages is Messages.Error -> {
                        val message = (state.errorMessages as Messages.Error).message
                        snackBarHostState.showSnackbar(SnackBarVisualsWithError(message, true))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(state: HomeUiState, onMovieClicked: (MoviesGridAction) -> Unit) {
    when {
        state.isLoading -> {
            Center(modifier = Modifier.testTag("loading")) {
                CircularProgressIndicator()
            }
        }

        state.loadingError -> {
            Center {
                Text(text = stringResource(id = R.string.message_loading_error))
            }
        }

        state.isEmpty -> {
            Center {
                Text(text = stringResource(id = R.string.message_text_empty_result))
            }
        }

        else -> {

            MoviesGrid(
                movies = state.movies,
                onItemClick = onMovieClicked
            )
        }
    }
}