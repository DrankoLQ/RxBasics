package com.daniellq.rxbasics

import com.daniellq.rxbasics.api.ApiClient
import com.daniellq.rxbasics.api.ApiService
import com.daniellq.rxbasics.model.GithubUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by dani on 26/10/17.
 */

data class MainPresenter(val mView: IMainView) : IMainPresenter {
    val restApi: ApiService = ApiClient().getApiService()

    override fun fetchData() {
        mView.setStatus("Fetching data...")
        restApi.getGithubUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { user ->
                            mView.setStatus("Data loaded!")
                            mView.showToast("${user.name} - ${user.bio}")
                        },
                        { e ->
                            mView.setStatus("Ups! there was some problem...")
                            mView.showToast(e.localizedMessage)
                        })
    }

    override fun fetchUserFollowers() {
        mView.setStatus("Loading user public email...")
        restApi.getGithubUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { user: GithubUser? ->
                            restApi.getUserFollowers(user?.login)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            { followersList: List<GithubUser>? ->
                                                mView.setStatus("User's followed loaded!")
                                                mView.showToast("Total number: ${followersList?.size}")
                                            },
                                            { t: Throwable? ->
                                                mView.setStatus("Ups! there was some problem...")
                                                mView.showToast("Error ${t?.localizedMessage}")
                                            }
                                    )
                        },
                        { t: Throwable? ->
                            mView.setStatus("Ups! there was some problem...")
                            mView.showToast("Error ${t?.localizedMessage}")
                        }
                )
    }
}

interface IMainPresenter {
    fun fetchData()
    fun fetchUserFollowers()
}