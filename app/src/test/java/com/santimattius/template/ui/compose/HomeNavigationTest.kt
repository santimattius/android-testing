package com.santimattius.template.ui.compose

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.di.FakeDataModule
import com.santimattius.template.ui.xml.home.HomeViewActivity
import com.santimattius.test.rules.FakeImageLoaderRule
import com.santimattius.test.rules.MainCoroutinesTestRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.plugin.module.dsl.modules
import org.koin.test.KoinTestRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Navigation between the Compose and Views home screens: the app bar action and back handling.
 */
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.R],
    instrumentedPackages = ["androidx.loader.content"],
    application = Application::class
)
class HomeNavigationTest {

    @get:Rule(order = 0)
    var koinTestRule = KoinTestRule.create {
        modules(DataModule::class, FakeDataModule::class)
    }

    @get:Rule
    val fakeImageLoaderRule = FakeImageLoaderRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `tapping the Views action opens the Views home screen`() =
        runAndroidComposeUiTest(HomeComposeActivity::class.java) {
            onNodeWithContentDescription("Open the Views version").performClick()

            val startedIntent = shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity
            assertEquals(HomeViewActivity::class.java.name, startedIntent.component?.className)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `back from the Compose home screen finishes the activity`() =
        runAndroidComposeUiTest(HomeComposeActivity::class.java) {
            runOnUiThread { activity!!.onBackPressedDispatcher.onBackPressed() }

            assertTrue(activity!!.isFinishing)
        }
}
