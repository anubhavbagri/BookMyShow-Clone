package com.devfest.india.bmsclone.data.remote.retrofit

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private val client = OkHttpClient.Builder().build()

    private val gson: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson)) //attach or integrate the gson
        .client(client) //attaching okhttp client
        .build()

    fun buildService(): MovieService {                  //return movie service interface
        return retrofit.create(MovieService::class.java)
    }
}