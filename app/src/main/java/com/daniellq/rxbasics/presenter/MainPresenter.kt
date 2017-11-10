package com.daniellq.rxbasics.presenter

import android.util.Log
import com.daniellq.rxbasics.api.ApiClient
import com.daniellq.rxbasics.api.ApiService
import com.daniellq.rxbasics.model.GithubUser
import com.daniellq.rxbasics.view.IMainView
import com.polidea.rxandroidble.RxBleClient
import com.polidea.rxandroidble.scan.ScanResult
import com.polidea.rxandroidble.scan.ScanSettings
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by dani on 26/10/17.
 */

data class MainPresenter(val mView: IMainView, val rxBleClient: RxBleClient?) : IMainPresenter {
    val restApi: ApiService = ApiClient().getApiService()
    val scanSubscription = RxJavaInterop.toV2Observable(rxBleClient?.scanBleDevices(ScanSettings.Builder().build()))

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

    override fun scanBluetooth() {
        mView.clearRecyclerView()
        mView.changeButtonState("Scanning devices...")
        mView.showLoader()
        scanSubscription
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t: ScanResult? ->
                    mView.onDeviceScanned(t?.bleDevice?.macAddress)
                }, { t: Throwable? ->
                    Log.d("onError", "Error: ${t?.localizedMessage}")
                })

        // Unsubcribe after 5 seconds
        scanSubscription.delay(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    scanSubscription.unsubscribeOn(Schedulers.io())
                    mView.hideLoader()
                    mView.changeButtonState("Scan bluetooth devices")
                }
    }
}