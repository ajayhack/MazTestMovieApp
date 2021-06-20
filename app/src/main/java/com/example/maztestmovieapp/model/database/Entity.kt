package com.example.maztestmovieapp.model.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

//region===============Results Data Class Used for mapping data from Https response and also used to store data in DB:-
@Parcelize
@Entity
data class Results(
    var vote_average: Double = 0.0,
    var original_language: String? = "",
    @PrimaryKey
    var original_title: String = "",
    var poster_path: String = "",
    var isFavourite: Boolean = false,
    var isClicked: Boolean = false) : Parcelable
//endregion