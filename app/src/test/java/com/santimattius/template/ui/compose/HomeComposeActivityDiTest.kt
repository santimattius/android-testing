package com.santimattius.template.ui.compose

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.template.ui.rules.OkHttpComposeIdle
import com.santimattius.template.ui.rules.RoomTestRule
import com.santimattius.test.data.TheMovieDBServiceMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.test.rules.MockWebServerRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.com_santimattius_template_di_DataModule
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.R],
    instrumentedPackages = ["androidx.loader.content"],
    application = Application::class
)
class HomeComposeActivityDiTest : KoinTest {

    private lateinit var idle: OkHttpComposeIdle
    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    @get:Rule(order = 0)
    val mockWebServerRule = MockWebServerRule {
        TheMovieDBServiceMother.createPopularMovieResponse()
    }

    @get:Rule(order = 1)
    val roomTestRule = RoomTestRule(appContext, TheMovieDataBase::class.java)


    @get:Rule(order = 2)
    var koinTestRule = KoinTestRule.create {
        modules(
            com_santimattius_template_di_DataModule,
            com_santimattius_template_di_AppModule,
            defaultModule,
            module {
                //override base url and database
                factory(named("base_url")) { mockWebServerRule.baseUrl }
                factory<TheMovieDataBase> { roomTestRule.db }
            }
        )
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    @OptIn(ExperimentalTestApi::class)
    @get:Rule(order = 3)
    val composeTestRule = createAndroidComposeRule(
        HomeComposeActivity::class.java,
        coroutinesTestRule.testDispatcher
    )

    @Before
    fun setUp() {
        val client = get<RetrofitServiceCreator>().client
        val idlingResource = OkHttp3IdlingResource.create("OkHttp", client)
        idle = OkHttpComposeIdle(idlingResource)

        composeTestRule.registerIdlingResource(idle)
    }

    @After
    fun tearDown() {
        composeTestRule.unregisterIdlingResource(idle)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Verify first movie is Fantastic Beasts The Secrets of Dumbledore`() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntilDoesNotExist(hasTestTag("loading"), timeoutMillis = 10_000)
        val title = "Fantastic Beasts: The Secrets of Dumbledore"
        composeTestRule.waitUntilExactlyOneExists(hasTestTag("card_$title"))
        composeTestRule.onNodeWithTag("card_$title").assertExists()
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `onFavorite should update state with success message when adding to favorite succeeds`(){
        with(composeTestRule){

            composeTestRule.waitForIdle()
            composeTestRule.waitUntilDoesNotExist(hasTestTag("loading"), timeoutMillis = 10_000)

            val title = "Fantastic Beasts: The Secrets of Dumbledore"
            composeTestRule.waitUntilExactlyOneExists(hasTestTag("card_$title"))

            onNodeWithTag("favorite_${title}")
                .assertExists()
                .performScrollTo()
                .assertIsDisplayed()
                .performClick()

            onNodeWithText("Add $title  to favorite")
                .assertIsDisplayed()

            onNodeWithText("OK").assertIsDisplayed().performClick()

            onNodeWithText("Add $title  to favorite")
                .assertDoesNotExist()
        }
    }

}

