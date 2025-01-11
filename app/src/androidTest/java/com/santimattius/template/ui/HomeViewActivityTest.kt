package com.santimattius.template.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.santimattius.test.data.MovieMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.template.R
import com.santimattius.template.di.DataModule
import com.santimattius.template.espresso.RecyclerViewInteraction
import com.santimattius.template.ui.xml.home.HomeViewActivity
import com.santimattius.template.ui.xml.home.models.MovieUiModel
import com.santimattius.template.ui.xml.home.models.mapping.asUiModels
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@UninstallModules(DataModule::class)
@HiltAndroidTest
class HomeViewActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    val coroutinesTestRule = MainCoroutinesTestRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun showsTitleOfImageIfThereAreImages() {
        val pictures = MovieMother.createDomainMovies()

        ActivityScenario.launch(HomeViewActivity::class.java)

        RecyclerViewInteraction.onRecyclerView<MovieUiModel>(withId(R.id.grid_of_movies))
            .withItems(pictures.asUiModels())
            .check { picture, view, exception ->
                matches(hasDescendant(withContentDescription(picture.title))).check(
                    view,
                    exception
                )
            }
    }
}