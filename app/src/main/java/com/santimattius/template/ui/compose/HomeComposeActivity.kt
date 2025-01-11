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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santimattius.template.R
import com.santimattius.template.ui.xml.home.HomeViewActivity
import com.santimattius.template.ui.xml.home.models.HomeState
import com.santimattius.template.ui.xml.home.models.MovieUiModel
import com.santimattius.template.ui.compose.ui.components.AppBar
import com.santimattius.template.ui.compose.ui.components.AppBarIcon
import com.santimattius.template.ui.compose.ui.components.AppBarIconModel
import com.santimattius.template.ui.compose.ui.components.Center
import com.santimattius.template.ui.compose.ui.components.MoviesGrid
import com.santimattius.template.ui.compose.ui.theme.AndroidTestingTheme
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
    Scaffold(
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

        HomeState.Empty -> {
            Center {
                Text(text = stringResource(id = R.string.message_text_empty_result))
            }
        }

        else -> {
            Center(modifier = Modifier.testTag("loading")) {
                CircularProgressIndicator()
            }
        }
    }
}