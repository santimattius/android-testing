package com.santimattius.template.ui

import org.koin.plugin.module.dsl.modules
import com.santimattius.template.di.FakeDataModule
import com.santimattius.template.di.DataModule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.santimattius.template.R
import com.santimattius.template.espresso.RecyclerViewInteraction
import com.santimattius.test.rules.KoinAndroidTestRule
import com.santimattius.template.ui.models.MovieUiModel
import com.santimattius.template.ui.models.mapping.asUiModels
import com.santimattius.template.ui.xml.home.HomeViewActivity
import com.santimattius.test.data.MovieMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class HomeViewActivityTest {

    @get:Rule(order = 0)
    var koinTestRule = KoinAndroidTestRule(
        configuration = { modules(DataModule::class, FakeDataModule::class) }
    )

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    val coroutinesTestRule = MainCoroutinesTestRule()

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