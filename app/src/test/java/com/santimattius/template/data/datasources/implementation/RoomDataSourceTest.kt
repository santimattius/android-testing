package com.santimattius.template.data.datasources.implementation

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.template.data.client.database.AppDataBase
import com.santimattius.template.data.dtoToEntity
import com.santimattius.shared_test.data.MovieMother
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
    val coroutinesTestRule = com.santimattius.shared_test.rules.MainCoroutinesTestRule()
    private lateinit var db: AppDataBase
    private lateinit var dataSource: RoomDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        dataSource = RoomDataSource(db.dao())
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