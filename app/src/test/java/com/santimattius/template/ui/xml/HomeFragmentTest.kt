package com.santimattius.template.ui.xml

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.test.extensions.launchFragmentInHiltContainer
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.xml.home.HomeFragment
import com.santimattius.template.ui.xml.home.components.viewholders.MovieViewHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Ignore
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
class HomeFragmentTest {

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
    @Ignore("Test not working, check documentation test fragment and hilt")
    fun `verify first movie is spider-man`() {

//        val scenario =
//            launchFragmentInContainer<HomeFragment>(themeResId = R.style.Theme_EntertainmentApp)
//
//        scenario.onFragment { fragment -> }

//https://developer.android.com/training/dependency-injection/hilt-testing?hl=es-419#launchfragment
        launchFragmentInHiltContainer<HomeFragment> {
            val recyclerView = (this as HomeFragment).viewBinding.gridOfMovies

            val viewHolder = recyclerView
                .findViewHolderForAdapterPosition(0)

            val imageView = (viewHolder as MovieViewHolder).viewBinding.imageMovie

            MatcherAssert.assertThat(
                imageView.contentDescription,
                equalTo("Spider-Man: No Way Home")
            )
        }
    }
}