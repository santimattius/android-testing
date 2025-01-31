package com.santimattius.template.ui

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
import com.santimattius.template.rules.KoinAndroidTestRule
import com.santimattius.template.ui.models.MovieUiModel
import com.santimattius.template.ui.models.mapping.asUiModels
import com.santimattius.template.ui.xml.home.HomeViewActivity
import com.santimattius.test.data.MovieMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.com_santimattius_template_di_FakeDataModule
import org.koin.ksp.generated.defaultModule

@MediumTest
@RunWith(AndroidJUnit4::class)
class HomeViewActivityTest {

    @get:Rule(order = 0)
    var koinTestRule = KoinAndroidTestRule(
        listOf(
            com_santimattius_template_di_FakeDataModule,
            com_santimattius_template_di_AppModule,
            defaultModule
        )
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