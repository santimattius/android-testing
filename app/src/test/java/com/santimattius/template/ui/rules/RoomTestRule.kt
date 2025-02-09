package com.santimattius.template.ui.rules

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.Executors

/**
 * A JUnit TestRule for testing Room database interactions.
 *
 * This rule provides an in-memory instance of a Room database for testing purposes.
 * It handles the setup and teardown of the database, ensuring a clean state for each test.
 *
 * @param appContext The application context used to build the in-memory database.
 * @param klass The class of the Room database to be tested (e.g., `MyDatabase::class.java`).
 * @param T The type of the Room database, which must extend [RoomDatabase].
 */
class RoomTestRule<T : RoomDatabase>(
    private val appContext: Context,
    private val klass: Class<T>
) : TestWatcher() {

    lateinit var db: T

    override fun starting(description: Description?) {
        db = Room.inMemoryDatabaseBuilder(appContext, klass)
            .allowMainThreadQueries()
//            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    override fun finished(description: Description?) {
        db.close()
    }
}