package com.daniellq.rxbasics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife

class MainActivity : AppCompatActivity() {
    @BindView(R.id.btnGetData)
    lateinit var btnGetData: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        btnGetData.setOnClickListener { }
    }
}
