package com.daniellq.rxbasics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.daniellq.rxbasics.api.ApiClient
import com.daniellq.rxbasics.api.ApiService
import com.daniellq.rxbasics.model.GithubUser
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    @BindView(R.id.btnGetData)
    lateinit var btnGetData: Button

    val restApi: ApiService = ApiClient().getApiService()
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        btnGetData.setOnClickListener {
            val subscription = getUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { user ->
                                Toast.makeText(this, "${user.name} - ${user.bio}", Toast.LENGTH_SHORT).show()
                            },
                            { e ->
                                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                            })
            compositeDisposable.addAll(subscription)
        }
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
