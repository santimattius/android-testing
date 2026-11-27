package com.santimattius.template.ui.compose

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.core.net.toUri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.santimattius.test.compose.HomeRobot
import com.santimattius.test.compose.scenario
import com.santimattius.test.rules.FakeImageLoaderRule
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.di.FakeDataModule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.plugin.module.dsl.modules
import org.koin.test.KoinTestRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** `androidtesting://movies/{id}`, described in docs/features/movie_deep_link.feature. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R], application = Application::class)
class HomeMovieDeepLinkTest {

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule.create {
        modules(DataModule::class, FakeDataModule::class)
    }

    @get:Rule(order = 1)
    val fakeImageLoaderRule = FakeImageLoaderRule()

    @get:Rule(order = 2)
    val composeTestRule = createEmptyComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val robot by lazy { HomeRobot(composeTestRule) }
    private var activityScenario: ActivityScenario<HomeComposeActivity>? = null

    @After
    fun closeActivity() {
        activityScenario?.close()
    }

    @Test
    fun `a browsable movie link resolves to the Compose home screen`() {
        val link = viewIntent(MOVIE_LINK).addCategory(Intent.CATEGORY_BROWSABLE)

        val resolved = context.packageManager.queryIntentActivities(link, 0)

        assertEquals(
            listOf(HomeComposeActivity::class.java.name),
            resolved.map { it.activityInfo.name },
        )
    }

    @Test
    fun `opening a movie link shows the detail of that movie`() = scenario(robot) {
        whenever { open(MOVIE_LINK) }
        then { assertMovieDetailDisplayed(MOVIE_TITLE) }
    }

    @Test
    fun `an open detail survives a configuration change`() = scenario(robot) {
        given { open(MOVIE_LINK) }
        whenever { recreateActivity() }
        then { assertMovieDetailDisplayed(MOVIE_TITLE) }
    }

    @Test
    fun `a dismissed detail is not shown again after a configuration change`() = scenario(robot) {
        given {
            open(MOVIE_LINK)
            closeMovieDetail()
        }
        whenever { recreateActivity() }
        then {
            assertMovieListed(MOVIE_TITLE)
            assertNoMovieDetail()
        }
    }

    @Test
    fun `the activity is reused for links received while it is on top`() {
        val activity = ComponentName(context, HomeComposeActivity::class.java)

        val launchMode = context.packageManager.getActivityInfo(activity, 0).launchMode

        assertEquals(ActivityInfo.LAUNCH_SINGLE_TOP, launchMode)
    }

    @Test
    fun `a movie link received while the screen is open shows that movie`() = scenario(robot) {
        given {
            open(MOVIE_LINK)
            closeMovieDetail()
        }
        whenever { receive(viewIntent("androidtesting://movies/508947")) }
        then { assertMovieDetailDisplayed("Turning Red") }
    }

    @Test
    fun `an unrelated intent received while a detail is open keeps it`() = scenario(robot) {
        given { open(MOVIE_LINK) }
        whenever { receive(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)) }
        then { assertMovieDetailDisplayed(MOVIE_TITLE) }
    }

    @Test
    fun `a movie link relaunched from recents shows no detail`() = scenario(robot) {
        whenever { activityScenario = ActivityScenario.launch(fromRecents(MOVIE_LINK)) }
        then {
            assertMovieListed(MOVIE_TITLE)
            assertNoMovieDetail()
        }
    }

    @Test
    fun `a movie link redelivered from recents does not reopen a dismissed detail`() =
        scenario(robot) {
            given {
                open(MOVIE_LINK)
                closeMovieDetail()
            }
            whenever { receive(fromRecents(MOVIE_LINK)) }
            then { assertNoMovieDetail() }
        }

    @Test
    fun `a link with an unknown id shows no detail`() =
        assertIgnored("androidtesting://movies/1")

    @Test
    fun `a link with a non numeric id shows no detail`() =
        assertIgnored("androidtesting://movies/abc")

    @Test
    fun `a link with an oversized id shows no detail`() =
        assertIgnored("androidtesting://movies/99999999999999999999")

    @Test
    fun `a link with extra path segments shows no detail`() =
        assertIgnored("androidtesting://movies/other/634649")

    @Test
    fun `a link with a different host shows no detail`() =
        assertIgnored("androidtesting://series/634649")

    @Test
    fun `an opaque link shows no detail`() = assertIgnored("androidtesting:634649")

    @Test
    fun `a movie link sent with another action shows no detail`() =
        assertIgnored(MOVIE_LINK, action = Intent.ACTION_SEND)

    private fun assertIgnored(link: String, action: String = Intent.ACTION_VIEW) = scenario(robot) {
        whenever { open(link, action) }
        then {
            assertMovieListed(MOVIE_TITLE)
            assertNoMovieDetail()
        }
    }

    private fun viewIntent(link: String) =
        Intent(Intent.ACTION_VIEW, link.toUri()).setPackage(context.packageName)

    private fun open(link: String, action: String = Intent.ACTION_VIEW) {
        activityScenario = ActivityScenario.launch(explicitIntent(link).setAction(action))
    }

    private fun explicitIntent(link: String) =
        viewIntent(link).setClass(context, HomeComposeActivity::class.java)

    /** The intent as the platform redelivers it when a finished task is reopened from Recents. */
    private fun fromRecents(link: String) =
        explicitIntent(link).addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)

    /** Delivers an intent to the running activity the way the platform does for `singleTop`. */
    private fun receive(intent: Intent) {
        checkNotNull(activityScenario).onActivity {
            InstrumentationRegistry.getInstrumentation().callActivityOnNewIntent(it, intent)
        }
    }

    private fun recreateActivity() {
        checkNotNull(activityScenario).recreate()
    }

    private companion object {
        const val MOVIE_LINK = "androidtesting://movies/634649"
        const val MOVIE_TITLE = "Spider-Man: No Way Home"
    }
}
