package com.wechat.moments.utils

import androidx.annotation.IntDef

@IntDef(
    LoadingState.LOADING,
    LoadingState.LOADING_ERROR,
    LoadingState.LOADING_COMPLETE,
    LoadingState.LOADING_NO_MORE
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class LoadingState {
    companion object {
        const val LOADING = 0
        const val LOADING_ERROR = 2
        const val LOADING_COMPLETE = 3
        const val LOADING_NO_MORE = 4
    }
}