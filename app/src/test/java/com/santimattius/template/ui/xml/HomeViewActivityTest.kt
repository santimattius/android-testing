package com.santimattius.template.ui.xml

import org.koin.plugin.module.dsl.modules
import com.santimattius.template.ui.di.FakeDataModule
import com.santimattius.template.di.DataModule
import android.app.Application
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.menu.MenuBuilder
import android.view.View
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentContainerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.template.R
import com.santimattius.template.ui.xml.home.HomeFragment
import com.santimattius.template.ui.xml.home.HomeViewActivity
import com.santimattius.template.ui.xml.home.components.viewholders.MovieViewHolder
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTestRule
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.R],
    instrumentedPackages = ["androidx.loader.content"],
    application = Application::class
)
class HomeViewActivityTest {

    @get:Rule
    var koinTestRule = KoinTestRule.create {
        modules(DataModule::class, FakeDataModule::class)
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Test
    fun `up navigation dispatches to the registered back callback`() {
        ActivityScenario.launch(HomeViewActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                var backCount = 0
                activity.onBackPressedDispatcher.addCallback(
                    activity,
                    object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            backCount++
                        }
                    }
                )
                val upItem = MenuBuilder(activity).add(0, android.R.id.home, 0, "Up")

                assertThat(activity.onOptionsItemSelected(upItem), equalTo(true))
                assertThat(backCount, equalTo(1))
                assertThat(activity.isFinishing, equalTo(false))
            }
        }
    }

    @Test
    fun `up navigation finishes the activity without a back callback`() {
        ActivityScenario.launch(HomeViewActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val upItem = MenuBuilder(activity).add(0, android.R.id.home, 0, "Up")

                assertThat(activity.onOptionsItemSelected(upItem), equalTo(true))
                assertThat(activity.isFinishing, equalTo(true))
            }
        }
    }

    @Test
    fun `first movie cards start below the action bar`() {
        ActivityScenario.launch(HomeViewActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val actionBar = activity.findViewById<View>(androidx.appcompat.R.id.action_bar_container)
                assertNotNull("Action bar container must exist", actionBar)
                assertTrue("Action bar must be laid out", actionBar.height > 0)
                val barLocation = IntArray(2)
                actionBar.getLocationInWindow(barLocation)
                val actionBarBottom = barLocation[1] + actionBar.height
                assertTrue("Action bar bottom must be positive", actionBarBottom > 0)

                val fragment = activity.findViewById<FragmentContainerView>(R.id.fragment_container_view)
                    .getFragment<HomeFragment>()
                val grid = fragment.viewBinding.gridOfMovies
                assertTrue("Grid must be laid out", grid.width > 0 && grid.height > 0)
                for (position in 0..1) {
                    val holder = grid.findViewHolderForAdapterPosition(position)
                    assertNotNull("Card $position must be laid out", holder)
                    val card = holder!!.itemView
                    assertTrue("Card $position must have height", card.height > 0)
                    val location = IntArray(2)
                    card.getLocationInWindow(location)
                    assertTrue(
                        "Card $position top ${location[1]} overlaps action bar bottom $actionBarBottom",
                        location[1] >= actionBarBottom
                    )
                }
            }
        }
    }

    @Test
    fun `verify first movie is spider-man`() {
        val scenario = ActivityScenario.launch(HomeViewActivity::class.java)

        scenario.onActivity { activity ->
            val fragmentContainer =
                activity.findViewById<FragmentContainerView>(R.id.fragment_container_view)

            val fragment = fragmentContainer.getFragment<HomeFragment>()
            val recyclerView = fragment.viewBinding.gridOfMovies

            val viewHolder = recyclerView
                .findViewHolderForAdapterPosition(0)

            val imageView = (viewHolder as MovieViewHolder).viewBinding.imageMovie

            assertThat(
                imageView.contentDescription,
                equalTo("Spider-Man: No Way Home")
            )
        }
    }
}