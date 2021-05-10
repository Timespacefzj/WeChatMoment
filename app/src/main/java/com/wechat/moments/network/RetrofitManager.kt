package com.wechat.moments.network

import com.timespace.base.BuildConfig
import com.timespace.base.retrofit.BaseRetrofitManager
import com.timespace.base.retrofit.TimeOutInterceptor
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitManager private constructor(): BaseRetrofitManager() {
    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManager()
        }
    }

    override val CONNECT_TIME_OUT: Long
        get() = 30
    override val READ_TIME_OUT: Long
        get() = 30
    override val WRITE_TIME_OUT: Long
        get() = 30

    override fun handleBuilder() {
        builder.addInterceptor(HttpLoggingInterceptor(HttpLogger()).apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        builder.addNetworkInterceptor(HttpLoggingInterceptor(HttpLogger()).apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        builder.addInterceptor(TimeOutInterceptor())
    }

    override fun addFactory(builder: retrofit2.Retrofit.Builder) {
        builder.addConverterFactory(GsonConverterFactory.create())
    }
}