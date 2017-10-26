package com.daniellq.rxbasics

import com.daniellq.rxbasics.api.ApiClient
import com.daniellq.rxbasics.api.ApiService
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
}

interface IMainPresenter {
    fun fetchData()
}