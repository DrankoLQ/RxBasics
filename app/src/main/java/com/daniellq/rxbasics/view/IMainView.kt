package com.daniellq.rxbasics.view

/**
 * Created by dani on 3/11/17.
 */
interface IMainView {
    fun setStatus(status: String)
    fun showToast(message: String)
    fun clearRecyclerView()
    fun onDeviceScanned(deviceAddres: String?)
    fun changeButtonState(state: String)
    fun showLoader()
    fun hideLoader()
}