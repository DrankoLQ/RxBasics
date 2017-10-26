package com.daniellq.rxbasics.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by dani on 13/9/17.
 */

class ApiClient {
    private val retrofit: Retrofit

    init {
        val httpClient = OkHttpClient.Builder()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(logging)

        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                    .method(original.method(), original.body())
                    .build()
            chain.proceed(request)
        }

        val client = httpClient.build()
        retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    fun getApiService(): ApiService = retrofit.create<ApiService>(ApiService::class.java)
}