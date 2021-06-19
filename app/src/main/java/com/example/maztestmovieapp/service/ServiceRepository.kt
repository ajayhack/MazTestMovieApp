package com.example.maztestmovieapp.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.maztestmovieapp.model.MovieDBResponse
import com.example.maztestmovieapp.model.database.Results
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceRepository {

   private var movieMutableLiveDataList : MutableLiveData<ArrayList<Results?>>? = MutableLiveData()

    //region==========================Getting Data and Stubbing Data in Modal:-
    fun getMutableLiveData(): MutableLiveData<ArrayList<Results?>>? {
        val service : ApiClient = getClient().create(ApiClient :: class.java)
        val call: Call<MovieDBResponse> =service.getPopularMovies("f8c2fb3301267b649b40cb8d22023776")
        call.enqueue(object : Callback<MovieDBResponse?> {
            override fun onResponse(call: Call<MovieDBResponse?>, response: Response<MovieDBResponse?>) {
                val movieResponse: MovieDBResponse? = response.body()
                when(response.code()){
                    SUCCESS -> {
                        Log.d("SUCCESS:- " , response.message())
                        if (movieResponse?.results != null) {
                            movieMutableLiveDataList?.value = movieResponse.results as ArrayList<Results?>
                        }
                    }
                    UNAUTH -> Log.d("UNAUTHENTICATED:- " , response.message())
                    INTERNAL_SERVER_ERROR -> Log.d("SERVER ERROR:- " , response.message())
                    BAD_REQUEST -> Log.d("BAD REQUEST:- " , response.message())
                    NOT_FOUND -> Log.d("NOT FOUND:- " , response.message())
                }
            }
            override fun onFailure(call: Call<MovieDBResponse?>, t: Throwable) {}
        })
        return movieMutableLiveDataList
    }
    //endregion
}