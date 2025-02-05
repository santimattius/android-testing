package com.santimattius.core.data.client.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie")
    fun searchAll(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie")
    suspend fun getAll(): List<MovieEntity>

    @Query("SELECT * FROM movie WHERE id=:id")
    suspend fun findById(id: Long): MovieEntity?

    @Query("SELECT COUNT(id) FROM movie")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg movies: MovieEntity): List<Long>

    @Delete
    suspend fun delete(movies: MovieEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(movies: MovieEntity)

    @Query("UPDATE movie SET favorite = 1 WHERE id = :movieId")
    suspend fun addToFavorite(movieId: Long): Int

    @Query("UPDATE movie SET favorite = 0 WHERE id = :movieId")
    suspend fun removeFromFavorite(movieId: Long): Int

    @Query(
        """
        UPDATE movie 
        SET title = :title, 
            overview = :overview, 
            poster = :poster 
        WHERE id = :id
    """
    )
    suspend fun updateMovieDetails(id: Long, title: String, overview: String, poster: String)

    @Transaction
    suspend fun upsert(movies: List<MovieEntity>) {
        val result = insertAll(*movies.toTypedArray())
        for ((index, movieId) in result.withIndex()) {
            if (movieId == -1L) {
                val movie = movies[index]
                updateMovieDetails(movie.id, movie.title, movie.overview, movie.poster)
            }
        }
    }
}