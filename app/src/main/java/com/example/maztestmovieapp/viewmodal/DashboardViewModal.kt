package com.example.maztestmovieapp.viewmodal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.maztestmovieapp.model.database.Results
import com.example.maztestmovieapp.service.ServiceRepository

class DashboardViewModal : ViewModel() {

    //region========================GET Movie Data in ViewModal From Repository:-
    fun getPopularMovieData(): LiveData<ArrayList<Results?>>? {
        return ServiceRepository().getMutableLiveData()
    }
    //endregion
}