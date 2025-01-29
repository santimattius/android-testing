package com.santimattius.template.ui.xml

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.template.ui.xml.home.HomeFragment
import com.santimattius.template.ui.xml.home.components.viewholders.MovieViewHolder
import com.santimattius.test.rules.MainCoroutinesTestRule
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.com_santimattius_template_ui_di_FakeDataModule
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTestRule
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.R],
    instrumentedPackages = ["androidx.loader.content"],
    application = Application::class
)
class HomeFragmentTest {

    @get:Rule
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


    @Test
    fun `verify first movie is spider-man`() {

        val scenario = launchFragmentInContainer<HomeFragment>(
            initialState = Lifecycle.State.INITIALIZED
        )

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
            val recyclerView = fragment.viewBinding.gridOfMovies

            val viewHolder = recyclerView
                .findViewHolderForAdapterPosition(0)

            val imageView = (viewHolder as MovieViewHolder).viewBinding.imageMovie
            val title = imageView.contentDescription.toString()
            assertThat(title, equalTo("Spider-Man: No Way Home"))
        }

    }
}