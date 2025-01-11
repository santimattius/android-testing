package com.santimattius.template.ui.xml

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentContainerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.shared_test.rules.MainCoroutinesTestRule
import com.santimattius.template.R
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.xml.home.HomeFragment
import com.santimattius.template.ui.xml.home.HomeViewActivity
import com.santimattius.template.ui.xml.home.components.viewholders.MovieViewHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@UninstallModules(DataModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.R],
    instrumentedPackages = ["androidx.loader.content"],
    application = HiltTestApplication::class
)
class HomeViewActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    @Before
    fun setUp() {
        hiltRule.inject()
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