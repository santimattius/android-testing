package com.santimattius.template.ui.compose

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runAndroidComposeUiTest
import com.santimattius.test.rules.MainCoroutinesTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.com_santimattius_template_ui_di_FakeDataModule
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTestRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.R],
    instrumentedPackages = ["androidx.loader.content"],
    application = Application::class
)
class HomeComposeActivityTest {

    @get:Rule(order = 0)
    var koinTestRule = KoinTestRule.create {
        modules(
            com_santimattius_template_ui_di_FakeDataModule,
            com_santimattius_template_di_AppModule,
            defaultModule
        )
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `verify first movie is spider-man`() =
        runAndroidComposeUiTest(HomeComposeActivity::class.java) {
            onNodeWithTag("Spider-Man: No Way Home")
                .assertIsDisplayed()
        }

}