package com.santimattius.core.data.client.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.santimattius.core.data.models.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class TheMovieDataBase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "tmdb_database"

        /*@Volatile
        private var INSTANCE: TheMovieDataBase? = null

        fun get(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: create(context).also { INSTANCE = it }
        }
        */

        fun get(context: Context) = create(context)

        private fun create(context: Context) =
            Room.databaseBuilder(context, TheMovieDataBase::class.java, DATABASE_NAME)
                .build()
    }

    abstract fun dao(): MovieDao
}