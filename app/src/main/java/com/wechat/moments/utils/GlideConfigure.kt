package com.wechat.moments.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class GlideConfigure : AppGlideModule() {
    private lateinit var appRootPath: String

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        val diskCacheSizeBytes = 1024 * 1024 * 100L
        appRootPath = context.externalCacheDir?.path ?: context.cacheDir.path
        val calculator = MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2f)
                .setBitmapPoolScreens(3f)
                .build()
        builder.setBitmapPool(LruBitmapPool(calculator.bitmapPoolSize.toLong()))
        builder.setDiskCache(DiskLruCacheFactory("$appRootPath/GlideDisk", diskCacheSizeBytes))
    }

    override fun isManifestParsingEnabled() = false
}