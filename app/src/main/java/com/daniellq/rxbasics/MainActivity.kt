package com.daniellq.rxbasics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.daniellq.rxbasics.api.ApiClient
import com.daniellq.rxbasics.api.ApiService
import com.daniellq.rxbasics.model.GithubUser
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    @BindView(R.id.btnGetData)
    lateinit var btnGetData: Button
    @BindView(R.id.fetchStatus)
    lateinit var fetchStatus: TextView

    val restApi: ApiService = ApiClient().getApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        btnGetData.setOnClickListener {
            fetchStatus.text = "Loading data..."
            getUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { user ->
                                fetchStatus.text = "Loaded!"
                                Toast.makeText(this, "${user.name} - ${user.bio}", Toast.LENGTH_SHORT).show()
                            },
                            { e ->
                                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                            })
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
