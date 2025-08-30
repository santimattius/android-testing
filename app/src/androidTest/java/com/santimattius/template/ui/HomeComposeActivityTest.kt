package com.santimattius.template.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.IdlingPolicies
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.santimattius.test.rules.KoinAndroidTestRule
import com.santimattius.template.ui.compose.HomeComposeActivity
import com.santimattius.test.rules.MainCoroutinesTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.com_santimattius_template_di_FakeDataModule
import org.koin.ksp.generated.defaultModule
import java.util.concurrent.TimeUnit

@MediumTest
@RunWith(AndroidJUnit4::class)
class HomeComposeActivityTest {

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

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<HomeComposeActivity>()

    @Before
    fun init() {
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