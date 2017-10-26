package com.daniellq.rxbasics

import com.daniellq.rxbasics.api.ApiClient
import com.daniellq.rxbasics.api.ApiService
import com.daniellq.rxbasics.model.GithubUser
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by dani on 26/10/17.
 */

data class MainPresenter(val mView: IMainView) : IMainPresenter {
    val restApi: ApiService = ApiClient().getApiService()

    override fun fetchData() {
        getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { user ->
                            mView.setStatus("${user.name} - ${user.bio}")
                        },
                        { e ->
                            mView.setStatus(e.localizedMessage)
                        })
    }

    private fun getUser(): Observable<GithubUser> {
        return Observable.create { subscriber ->
            val response = restApi.getGithubUser().execute()
            if (response.isSuccessful) {
                subscriber.onNext(response.body())
                subscriber.onComplete()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

}

interface IMainPresenter {
    fun fetchData()
}