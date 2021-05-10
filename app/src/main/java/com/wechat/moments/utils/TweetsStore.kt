package com.wechat.moments.utils

import com.wechat.moments.data.TweetsBean
import kotlin.jvm.Synchronized
import java.util.ArrayList

object TweetsStore {

    var totalPage = 1
    private val mList: MutableList<TweetsBean> = ArrayList()

    @Synchronized
    fun saveMomentList(list: List<TweetsBean>) {
        mList.clear()
        mList.addAll(list)
    }

    @Synchronized
    fun getSomeOfMomentList(page: Int): List<TweetsBean> {
        val start = (page - 1) * 5
        val end = page * 5
        val size = mList.size
        return if (size >= end - 1) {
            mList.subList(start, end)
        } else {
            mList.subList(start, size)
        }
    }
}