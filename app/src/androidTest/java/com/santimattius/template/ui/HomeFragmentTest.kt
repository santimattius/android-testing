package com.santimattius.template.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.santimattius.shared_test.data.MovieMother
import com.santimattius.shared_test.data.dtoToUiModel
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.R
import com.santimattius.template.espresso.RecyclerViewInteraction
import com.santimattius.template.ui.androidview.home.MainActivity
import com.santimattius.template.ui.androidview.home.models.MovieUiModel
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

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
    fun showsAuthorNameOfImageIfThereAreImages() {
        val pictures = MovieMother.createMovies().dtoToUiModel()

        ActivityScenario.launch(MainActivity::class.java)

        RecyclerViewInteraction.onRecyclerView<MovieUiModel>(withId(R.id.image_movie))
            .withItems(pictures)
            .check { picture, view, exception ->
                matches(hasDescendant(withContentDescription(picture.title))).check(
                    view,
                    exception
                )
            }
    }
}