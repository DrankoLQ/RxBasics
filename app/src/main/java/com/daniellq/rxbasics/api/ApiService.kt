package com.daniellq.rxbasics.api

import com.daniellq.rxbasics.model.GithubUser
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by dani on 13/9/17.
 */

interface ApiService {
    @GET("/users/drankolq")
    fun getGithubUser(): Call<GithubUser>
}