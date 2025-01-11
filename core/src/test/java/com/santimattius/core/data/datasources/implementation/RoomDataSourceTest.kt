package com.santimattius.core.data.datasources.implementation

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.test.data.MovieMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.dtoToEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.R])
class RoomDataSourceTest {

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()
    private lateinit var db: TheMovieDataBase
    private lateinit var dataSource: RoomMovieLocalDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TheMovieDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        dataSource = RoomMovieLocalDataSource(db)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `validate that the movie was saved correctly`() {
        runTest(coroutinesTestRule.testDispatcher) {
            val movies = MovieMother.createMovies().dtoToEntity()
            val randomMovie = movies.random()

            val result = dataSource.save(movies)
            val findResult = dataSource.find(randomMovie.id)

            assertThat("Saved", result.isSuccess, IsEqual(true))
            assertThat("Find movie with id ${randomMovie.id}", findResult.isSuccess, IsEqual(true))
        }
    }

}