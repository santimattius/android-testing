package com.santimattius.template.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToUiModel
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.R
import com.santimattius.template.di.DataModule
import com.santimattius.template.espresso.RecyclerViewInteraction
import com.santimattius.template.ui.androidview.home.MainActivity
import com.santimattius.template.ui.androidview.home.models.MovieUiModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@UninstallModules(DataModule::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun showsTitleOfImageIfThereAreImages() {
        val pictures = MovieMother.createMovies().dtoToUiModel()

        ActivityScenario.launch(MainActivity::class.java)

        RecyclerViewInteraction.onRecyclerView<MovieUiModel>(withId(R.id.grid_of_movies))
            .withItems(pictures)
            .check { picture, view, exception ->
                matches(hasDescendant(withContentDescription(picture.title))).check(
                    view,
                    exception
                )
            }
    }
}