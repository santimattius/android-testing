package com.santimattius.template.ui.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santimattius.template.R
import com.santimattius.template.ui.androidview.home.models.HomeState
import com.santimattius.template.ui.androidview.home.models.MovieUiModel
import com.santimattius.template.ui.compose.ui.components.Center
import com.santimattius.template.ui.compose.ui.components.MoviesGrid
import com.santimattius.template.ui.compose.ui.theme.AndroidtestingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidtestingTheme {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current
            HomeScreenContent(state = state) {
                Toast.makeText(context, it.title, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun HomeScreenContent(state: HomeState, onMovieClicked: (MovieUiModel) -> Unit) {
    when (state) {
        is HomeState.Data -> {
            MoviesGrid(
                movies = state.values,
                onItemClick = onMovieClicked
            )
        }

        HomeState.Error -> {
            Center {
                Text(text = stringResource(id = R.string.message_loading_error))
            }
        }

        else -> {
            Center {
                CircularProgressIndicator()
            }
        }
    }
}