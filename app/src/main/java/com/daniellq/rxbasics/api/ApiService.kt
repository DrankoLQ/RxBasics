package com.daniellq.rxbasics.api

import com.daniellq.rxbasics.model.GithubUser
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by dani on 13/9/17.
 */

interface ApiService {
    @GET("/users/drankolq")
    fun getGithubUser(): Observable<GithubUser>

    @GET("/users/{username}/followers")
    fun getUserFollowers(@Path("username") userName: String?): Observable<List<GithubUser>>
}