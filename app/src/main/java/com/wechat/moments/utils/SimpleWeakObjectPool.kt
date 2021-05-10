package com.wechat.moments.utils

import java.lang.ref.WeakReference

class SimpleWeakObjectPool<T> @JvmOverloads constructor(private val size: Int = 5) {
    private val objsPool: Array<WeakReference<T>> = java.lang.reflect.Array.newInstance(
        WeakReference::class.java,
        size
    ) as Array<WeakReference<T>>

    private var curPointer = -1

    @Synchronized
    fun get(): T? {
        if (curPointer == -1 || curPointer > objsPool.size) return null
        val obj = objsPool[curPointer].get()
        objsPool[curPointer].clear()
        curPointer--
        return obj
    }

    @Synchronized
    fun put(t: T): Boolean {
        if (curPointer == -1 || curPointer < objsPool.size - 1) {
            curPointer++
            objsPool[curPointer] = WeakReference(t)
            return true
        }
        return false
    }

    fun clearPool() {
        for (i in objsPool.indices) {
            objsPool[i].clear()
        }
        curPointer = -1
    }

    fun size(): Int {
        return objsPool.size ?: 0
    }
}