package com.santimattius.template.ui

import org.koin.plugin.module.dsl.modules
import com.santimattius.template.di.FakeDataModule
import com.santimattius.template.di.DataModule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.IdlingPolicies
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.santimattius.template.rules.FakeImageLoaderRule
import com.santimattius.test.rules.KoinAndroidTestRule
import com.santimattius.template.ui.compose.HomeComposeActivity
import com.santimattius.test.rules.MainCoroutinesTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@MediumTest
@RunWith(AndroidJUnit4::class)
class HomeComposeActivityTest {

    @get:Rule(order = 0)
    var koinTestRule = KoinAndroidTestRule(
        configuration = { modules(DataModule::class, FakeDataModule::class) }
    )

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    val coroutinesTestRule = MainCoroutinesTestRule()

    @get:Rule(order = 2)
    val fakeImageLoaderRule = FakeImageLoaderRule()

    @get:Rule(order = 3)
    val composeTestRule = createAndroidComposeRule<HomeComposeActivity>()

    @Before
    fun init() {
        // The loading state shows an infinite progress animation, so the clock is driven manually.
        composeTestRule.mainClock.autoAdvance = false
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS)
    }

    @Test
    fun showsMovieCardWhenThereAreMovies() {
        val cardTag = "card_Spider-Man: No Way Home"

        composeTestRule.mainClock.advanceTimeBy(5_000)

        composeTestRule.onNodeWithTag(cardTag)
            .assertExists("The movie card was not found.")
    }
}