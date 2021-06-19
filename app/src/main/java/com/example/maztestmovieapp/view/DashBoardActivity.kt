package com.example.maztestmovieapp.view
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonushub.pax.model.local.AppDatabase
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.maztestmovieapp.R
import com.example.maztestmovieapp.databinding.ActivityDashboardBinding
import com.example.maztestmovieapp.databinding.MovieListItemBinding
import com.example.maztestmovieapp.model.database.Results
import com.example.maztestmovieapp.utils.BaseActivity
import com.example.maztestmovieapp.viewmodal.DashboardViewModal
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashBoardActivity : BaseActivity() {
    private val dashboardViewModal : DashboardViewModal by lazy { ViewModelProvider(this).get(DashboardViewModal :: class.java) }
    private var binding : ActivityDashboardBinding? = null
    private var moviesLiveData : MutableList<Results?>? = null
    private val movieAdapter : MovieAdapter by lazy { MovieAdapter(moviesLiveData , ::onItemClick , ::updateFavouriteIcon) }
    private val orientation by lazy { this.resources.configuration.orientation }
    private var appDatabase : AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.toolBar?.toolbarTitle?.text = getString(R.string.popular_movies)
        binding?.toolBar?.headerImage?.visibility = View.GONE

        //Observing DashboardViewModal Movie Data:-
        appDatabase = AppDatabase.getDatabase(this)
        showProgress(getString(R.string.loading_msg))
        if(checkInternetConnection()) {
            getPopularMoviesData()
        }
        else
            lifecycleScope.launch(Dispatchers.IO) {
                showPopularMovieDataFromDB()
            }
    }

    //region===========Below method is used to get movies data and inflate on recycler view grid list:-
    private fun getPopularMoviesData(){
        dashboardViewModal.getPopularMovieData()?.observe(this, { movieData ->
            moviesLiveData = movieData
            Log.d("MovieData:- " , Gson().toJson(moviesLiveData))
            setUpRecyclerView()
            lifecycleScope.launch(Dispatchers.IO) {
                saveAllPopularMovieDataInDB()
                withContext(Dispatchers.Main){
                    hideProgress()
                }
            }
        })
    }
    //endregion

    //region===============Below method is used to setUp RecyclerView of Top Rated Movies List:-
    private fun setUpRecyclerView(){
        binding?.rvMovies?.apply {
            layoutManager = if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                GridLayoutManager(this@DashBoardActivity, 2)
            } else{
                GridLayoutManager(this@DashBoardActivity, 4)
            }

            itemAnimator = DefaultItemAnimator()
            adapter = movieAdapter
        }
    }
    //endregion

    //region===============Below method is used to get Popular Movies from DB and Inflate on UI if Available:-
    private suspend fun showPopularMovieDataFromDB(){
        val popularMovieDataFromDB = appDatabase?.dao()?.getAllPopularMovieDataFromDB()
        if(popularMovieDataFromDB?.isNotEmpty() == true){
            lifecycleScope.launch(Dispatchers.Main){
                moviesLiveData = popularMovieDataFromDB
                setUpRecyclerView()
                hideProgress()
            }
        }else {
            lifecycleScope.launch(Dispatchers.Main) {
                hideProgress()
                Toast.makeText(this@DashBoardActivity, "No Data Found , Please Check Your Internet", Toast.LENGTH_LONG).show()
            }
        }
    }
    //endregion

    //region=================Below method is used to navigate Movie Page To Detail Movie Page when user clicks on any movie Item of RecyclerView:-
    private fun onItemClick(position : Int){
        if(position > -1){
            lifecycleScope.launch(Dispatchers.IO){
                val updateMovieData = appDatabase?.dao()?.getAllPopularMovieDataFromDB()
                val clickedMovieData = updateMovieData?.filter { it?.isClicked == true }
                moviesLiveData = updateMovieData
                if(clickedMovieData?.size?:0 < 3){
                    val updateMovieClickedInDB = appDatabase?.dao()?.updateMovieClicked(clickedValue = true,
                        moviesLiveData?.get(position)?.original_title?:"")
                    Log.d("UpdateStatus:- " , updateMovieClickedInDB.toString())
                    withContext(Dispatchers.Main){
                        startActivity(Intent(this@DashBoardActivity , MovieDetailPageActivity ::class.java).apply {
                            putExtra("data" , moviesLiveData?.get(position))
                        })
                    }
                }else if(clickedMovieData?.size == 3 && moviesLiveData?.get(position)?.isClicked == true){
                    withContext(Dispatchers.Main){
                        startActivity(Intent(this@DashBoardActivity , MovieDetailPageActivity ::class.java).apply {
                            putExtra("data" , moviesLiveData?.get(position))
                        })
                    }
                }else{
                    withContext(Dispatchers.Main){
                        showInfoPopUp()
                    }
                }
            }
        }
    }
//endregion

    //region==================Update Favourite Icon in DB:-
    private fun updateFavouriteIcon(position: Int , value: Boolean){
        lifecycleScope.launch(Dispatchers.IO){
            val updateFavouriteMovie = appDatabase?.dao()?.updateFavMovie(value ,
                moviesLiveData?.get(position)?.original_title?:"")
            Log.d("updateFavouriteMovie:- " , updateFavouriteMovie.toString())
        }
    }
    //endregion

    //region====================Below method is used to save all popular movie data in DB , So that we can display data also when app is in offline State from Internet:-
    private suspend fun saveAllPopularMovieDataInDB(){
        val popularMovieDBData = appDatabase?.dao()?.getAllPopularMovieDataFromDB()
        if(moviesLiveData?.isNotEmpty() == true ) {
            if(popularMovieDBData?.isEmpty() == true){
            for (value in moviesLiveData!!) {
                val modal = Results()
                modal.original_language = value?.original_language ?: ""
                modal.original_title = value?.original_title ?: ""
                modal.poster_path = value?.poster_path ?: ""
                modal.vote_average = value?.vote_average ?: 0.0
                val insertStatus = appDatabase?.dao()?.insertPopularMovieData(modal)
                Log.d("InsertData:- ", insertStatus.toString())

            }
            }else{
                for(value in moviesLiveData!!){
                    val updateFav = appDatabase?.dao()?.updateFavMovie(favValue = false , value?.original_title?:"")
                    Log.d("updateFav" , updateFav.toString())
                }
            }
        }else{
            lifecycleScope.launch(Dispatchers.Main){
                Toast.makeText(this@DashBoardActivity , "No Data Found" , Toast.LENGTH_LONG).show()
            }
        }
    }
    //endregion

    //region====================Show Reach Out To Us Pop-Up:-
    private fun showInfoPopUp(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Alert")
            .setMessage("Please Reach Out To Us")
            .setCancelable(false)
            .setNeutralButton(""){_ , _ ->}
            .setNegativeButton(""){_ , _ ->}
            .setPositiveButton("Ok"){dialog , _ -> dialog.dismiss()}.show()

    }
    //endregion

    //region====================INTERNET CONNECTION CHECK:-
    private fun checkInternetConnection(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
//endregion
}

internal class MovieAdapter(private var movieArrayList : MutableList<Results?>? , var cb: (Int) -> Unit,
                            var favouriteCB: (Int , Boolean) -> Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding  = MovieListItemBinding.inflate( LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieData = movieArrayList?.get(position)
        if (movieData?.isFavourite == true) {
            holder.binding.favouriteIcon.setImageResource(R.drawable.ic_favourite_24red)
        } else {
            holder.binding.favouriteIcon.setImageResource(R.drawable.ic_favorite_24white)
        }
        val moviePosterPath = "https://image.tmdb.org/t/p/w500" + movieData?.poster_path
        Glide.with(holder.binding.posterImage)
            .load(moviePosterPath).apply(RequestOptions().error(R.drawable.loading))
            .into(holder.binding.posterImage)
        val rating = "${movieData?.vote_average?:0.0} / 10"
        holder.binding.tvTitle.text = movieData?.original_title?:""
        holder.binding.tvRating.text = rating

        //region=============Click event of Favourite Icon to Show user Favourite Movie Poster:-
        holder.binding.favouriteIcon.setOnClickListener {
                movieData?.isFavourite = !movieData?.isFavourite!!
                favouriteCB(position , movieData.isFavourite)
                notifyDataSetChanged()
        }
        //endregion
    }

    override fun getItemCount(): Int {
        return movieArrayList?.size?:0
    }

    inner class MovieViewHolder(var binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.cvMovie.setOnClickListener { cb(absoluteAdapterPosition) }
        }
    }
}