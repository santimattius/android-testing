package com.santimattius.template.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.compose.HomeComposeActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test


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
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun showsTitleOfImageIfThereAreImages() {
        composeTestRule.waitUntilExactlyOneExists(matcher = hasTestTag("Spider-Man: No Way Home"))
    }
}