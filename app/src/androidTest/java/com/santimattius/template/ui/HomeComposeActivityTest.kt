package com.santimattius.template.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.IdlingPolicies
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.compose.HomeComposeActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit


@UninstallModules(DataModule::class)
@HiltAndroidTest
class HomeComposeActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    val coroutinesTestRule = MainCoroutinesTestRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<HomeComposeActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        composeTestRule.mainClock.autoAdvance = false
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS)
    }

    @Test
    fun showsTitleOfImageIfThereAreImages() {
        composeTestRule.mainClock.advanceTimeBy(5000)
        composeTestRule.onNodeWithTag("Spider-Man: No Way Home")
            .assertExists("The image title tag was not found.")
    }
}