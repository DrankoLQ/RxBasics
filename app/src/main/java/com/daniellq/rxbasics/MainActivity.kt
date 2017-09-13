package com.daniellq.rxbasics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    var btnGetData: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetData = findViewById(R.id.btnGetData) as Button

        btnGetData?.setOnClickListener { }
    }
}
