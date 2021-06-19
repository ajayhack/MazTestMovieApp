package com.example.maztestmovieapp.model.database

import androidx.room.*

@Dao
interface AppDao {

    //region================================Popular Movie Data Table INSERT , READ , UPDATE and DELETE Manipulation:-
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPopularMovieData(movieData: Results): Long?

    @Query("SELECT * FROM Results")
    suspend fun getAllPopularMovieDataFromDB(): MutableList<Results?>?

    @Query("UPDATE Results SET isClicked = :clickedValue WHERE original_title = :title")
    suspend fun updateMovieClicked(clickedValue: Boolean, title: String)

    @Query("UPDATE Results SET isFavourite = :favValue WHERE original_title = :title")
    suspend fun updateFavMovie(favValue : Boolean, title: String)
    //endregion
}