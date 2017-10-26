package com.daniellq.rxbasics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife

class MainActivity : AppCompatActivity(), IMainView {
    @BindView(R.id.btnGetData)
    lateinit var btnGetData: Button
    @BindView(R.id.btnGetEmailFromUser)
    lateinit var btnGetEmailFromUse: Button
    @BindView(R.id.fetchStatus)
    lateinit var fetchStatus: TextView

    val mPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        btnGetData.setOnClickListener {
            mPresenter.fetchData()
        }
        btnGetEmailFromUse.setOnClickListener( {
            mPresenter.fetchUserFollowers()
        })
    }

    override fun setStatus(status: String) {
        fetchStatus.text = status
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

interface IMainView {
    fun setStatus(status: String)
    fun showToast(message: String)
}
