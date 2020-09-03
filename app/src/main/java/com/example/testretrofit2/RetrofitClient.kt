package com.example.testretrofit2

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://private-amnesiac-8522d-autopilot.apiary-proxy.com/v1/"
    private  val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val headerInterceptor = Interceptor { chain ->
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("Accept","application/json")
            .addHeader("Content-type","application/json")
            .addHeader("autopilotapikey","e2cff516881142c8b20fe3febf24d515")
            .build()
        chain.proceed(request)
    }

    private val builder = OkHttpClient.Builder()
        .readTimeout(5000, TimeUnit.MILLISECONDS)
        .writeTimeout(5000, TimeUnit.MILLISECONDS)
        .connectTimeout(5000, TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor(headerInterceptor)
        .addInterceptor(logger)
        .build()
    val instance : Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(builder)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(Api::class.java)
    }
}