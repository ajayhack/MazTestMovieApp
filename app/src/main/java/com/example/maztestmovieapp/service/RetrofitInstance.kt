package com.example.maztestmovieapp.service

import com.example.maztestmovieapp.model.MovieDBResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

lateinit var retroFit : Retrofit
const val BASE_URL = "https://api.themoviedb.org/3/"
const val SUCCESS = 200
const val UNAUTH = 401
const val BAD_REQUEST = 400
const val INTERNAL_SERVER_ERROR = 500
const val NOT_FOUND = 404

//region====================Creating GET Client for Retrofit:-
fun getClient(): Retrofit {
    val client : OkHttpClient = OkHttpClient.Builder().apply {
        addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        readTimeout(1, TimeUnit.MINUTES)
        connectTimeout(2, TimeUnit.MINUTES)
        writeTimeout(1, TimeUnit.MINUTES)
        followRedirects(false)
        followSslRedirects(false)
        retryOnConnectionFailure(false)
        cache(null)
    }.build()

    //Creating Retrofit Instance:-
    retroFit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    return retroFit
}
//endregion

//region=================API Client to used with Retrofit Request:-
internal interface ApiClient{
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String?): Call<MovieDBResponse>
}
//endregion