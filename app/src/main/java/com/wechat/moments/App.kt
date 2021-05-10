package com.wechat.moments

import android.app.Application
import com.bumptech.glide.Glide
import com.timespace.base.utils.BaseContext
import com.timespace.base.utils.LogUtils

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        BaseContext.init(this)
        LogUtils.apply {
            rootTag = "WeChatMoments"
            forceDebug = true
            initLogStatus()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }
}