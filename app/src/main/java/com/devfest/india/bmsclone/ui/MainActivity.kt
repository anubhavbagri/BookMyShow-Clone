package com.devfest.india.bmsclone.ui

import android.os.Bundle
import android.telecom.Call
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.devfest.india.bmsclone.R
import com.devfest.india.bmsclone.data.MovieRepositoryImpl
import com.devfest.india.bmsclone.data.local.database.MovieDatabase
import com.devfest.india.bmsclone.data.local.database.entity.Movie
import com.devfest.india.bmsclone.data.local.database.entity.MovieResponse
import com.devfest.india.bmsclone.data.remote.retrofit.MovieService
import com.devfest.india.bmsclone.data.remote.retrofit.RetrofitBuilder
import com.devfest.india.bmsclone.ui.adapter.MoviesAdapter
import com.devfest.india.bmsclone.ui.util.MainViewModelFactory
import com.devfest.india.bmsclone.util.NetworkHelper
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    companion object {
        private const val API_KEY = "8ba73cb2e4eb1663e251203a5900a0f6"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        observeViewModel()
    }
    private fun fetchMovies(){

        val networkHelper = NetworkHelper(this)

        if(networkHelper.isNetworkConnected()){
            val request = RetrofitBuilder.buildService()
            val call  = request.getMovies(API_KEY)

            showProgress()

            call.enqueue(object : Callback<MovieResponse>{
                override fun onResponse(call: retrofit2.Call<MovieResponse>, response: Response<MovieResponse>) {
                    hideProgress()
                    if(response.isSuccessful && response.body() != null){ //200

                        val movieResponse = response.body()!!
                        val movies = movieResponse.results
                        showMovies(movies)
                    }else{     //300,400,500
                        showErrorMessage(resources.getString(R.string.error_msg))
                    }
                }

                override fun onFailure(call: retrofit2.Call<MovieResponse>, t: Throwable) {
                    hideProgress()
                    showErrorMessage(t.message)
                }
            })
        } else{
            showErrorMessage(resources.getString(R.string.no_internet))
        }
    }
    private fun setupViewModel() {
        showProgress()

        viewModel = ViewModelProvider(
            this, MainViewModelFactory(
                NetworkHelper(this),
                MovieRepositoryImpl(
                    MovieDatabase.getInstance(this).movieDao(),
                    RetrofitBuilder.buildService()
                )
            )
        )[MainViewModel::class.java]
        viewModel.onCreate()
    }

    private fun observeViewModel() {
        viewModel.movieResponse.observe(this, Observer {
            showMovies(it.results)
            hideProgress()
        })

        viewModel.errorResponse.observe(this, Observer {
            showErrorMessage(it)
            hideProgress()
        })
    }

    private fun showMovies(movies: List<Movie>) {
        recyclerView.visibility = View.VISIBLE
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = MoviesAdapter(movies)
    }

    private fun showErrorMessage(errorMessage: String?) {
        errorView.visibility = View.VISIBLE
        errorView.text = errorMessage
    }

    private fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }
}