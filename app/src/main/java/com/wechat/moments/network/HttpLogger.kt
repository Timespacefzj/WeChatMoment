package com.wechat.moments.network

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor

class HttpLogger : HttpLoggingInterceptor.Logger {
    companion object {
        private const val TAG = "HttpLogger"
    }
    override fun log(message: String) {
        Log.d(TAG, message)
    }
}