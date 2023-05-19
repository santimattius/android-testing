package com.santimattius.template.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.compose.HomeActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
@UninstallModules(DataModule::class)
@HiltAndroidTest
class HomeActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    @get:Rule
    val composeTestRule = createAndroidComposeRule<HomeActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun showsTitleOfImageIfThereAreImages() {
        composeTestRule
            .onNodeWithTag("Spider-Man: No Way Home")
            .assertIsDisplayed()
    }
}