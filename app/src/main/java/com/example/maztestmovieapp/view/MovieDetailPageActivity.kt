package com.example.maztestmovieapp.view

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.maztestmovieapp.R
import com.example.maztestmovieapp.databinding.ActivityMovieDetailPageBinding
import com.example.maztestmovieapp.model.database.Results
import com.example.maztestmovieapp.utils.BaseActivity

class MovieDetailPageActivity : BaseActivity() {
    private var binding : ActivityMovieDetailPageBinding? = null
    private val movieData  by lazy { intent.getParcelableExtra("data") as? Results }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailPageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.toolBar?.headerImage?.visibility = View.VISIBLE
        binding?.toolBar?.headerImage?.setOnClickListener { onBackPressed() }
        binding?.toolBar?.toolbarTitle?.text = getString(R.string.movie_detail_page)

        //region===============Binding Movie Data with UI Elements:-
        binding?.tvTitleLanguage?.text = movieData?.original_language?:""
        binding?.tvTitle?.text = movieData?.original_title?:""
        val moviePosterPath = "https://image.tmdb.org/t/p/w500" + movieData?.poster_path
        binding?.posterImage?.let {
            Glide.with(this)
                .load(moviePosterPath).apply(RequestOptions().error(R.drawable.loading))
                .into(it)
        }
        val rating = "${movieData?.vote_average?:0.0} / 10"
        binding?.tvRating?.text = rating?:""

        if(movieData?.isFavourite == true)
            binding?.favouriteIcon?.setImageResource(R.drawable.ic_favourite_24red)
        else
            binding?.favouriteIcon?.setImageResource(R.drawable.ic_favorite_24white)
        //endregion
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}