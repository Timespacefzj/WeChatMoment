package com.wechat.moments.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.widget.ImageView
import com.wechat.moments.utils.SimpleWeakObjectPool
import java.util.*

class NineGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs), OnHierarchyChangeListener {
    private var mAdapter: NineGridAdapter<*>? = null
    private var mListener: OnImageClickListener? = null
    private val imagePool: SimpleWeakObjectPool<View> by lazy {
        SimpleWeakObjectPool(9)
    }
    private var mRows = 0
    private var mColumns = 0
    private var mSingleWidth = 0
    private var mSingleHeight = 0
    private var mImageViews: MutableList<ImageView>? = null
    private var space = 0
    private var childWidth = 0
    private var childHeight = 0

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        setOnHierarchyChangeListener(this)
        space = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            4f, context.resources.displayMetrics
        ).toInt()
    }

    fun setAdapter(adapter: NineGridAdapter<*>?) {
        if (adapter == null || adapter.count <= 0) {
            removeAllViews()
            return
        }
        if (mImageViews == null) {
            mImageViews = ArrayList()
        } else {
            mImageViews?.clear()
        }
        mAdapter = adapter
        val oldCount = childCount
        val newCount = adapter.count
        initMatrix(newCount)
        removeScrapViews(oldCount, newCount)
        addChildrenData(adapter)
        requestLayout()
    }

    private fun removeScrapViews(oldCount: Int, newCount: Int) {
        if (newCount < oldCount) {
            removeViewsInLayout(newCount, oldCount - newCount)
        }
    }

    private fun initMatrix(length: Int) {
        if (length <= 3) {
            mRows = 1
            mColumns = length
        } else if (length <= 6) {
            mRows = 2
            mColumns = 3
        } else {
            mRows = 3
            mColumns = 3
        }
    }

    private fun addChildrenData(adapter: NineGridAdapter<*>) {
        val childCount = childCount
        val count = adapter.count
        for (i in 0 until count) {
            val hasChild = i < childCount
            var recycleView = if (hasChild) getChildAt(i) else null
            if (recycleView == null) {
                recycleView = imagePool.get()
                val child = adapter.getView(i, recycleView)
                addViewInLayout(child, i, child.layoutParams, true)
                mImageViews!!.add(child as ImageView)
            } else {
                adapter.getView(i, recycleView)
                mImageViews!!.add(recycleView as ImageView)
            }
        }
    }


    override fun addViewInLayout(
        child: View,
        index: Int,
        params: LayoutParams,
        preventRequestLayout: Boolean
    ): Boolean {
        if (child !is ImageView) {
            throw ClassCastException("addView(View child) NineGridView只能放ImageView")
        }
        return super.addViewInLayout(child, index, params, preventRequestLayout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childCount = childCount
        if (childCount <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if ((mRows == 0 || mColumns == 0) && mAdapter == null) {
            initMatrix(childCount)
        }
        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        val width = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val availableWidth = width - paddingLeft - paddingRight
        if (childCount <= 1) {
            childWidth = if (mSingleWidth == 0) {
                availableWidth * 2 / 5
            } else {
                availableWidth / 2
            }
            childHeight = if (mSingleHeight == 0) {
                childWidth
            } else {
                (mSingleHeight / mSingleWidth.toFloat() * childWidth).toInt()
            }
        } else {
            childWidth = (availableWidth - space * (mColumns - 1)) / 3
            childHeight = childWidth
        }
        val height = childHeight * mRows + space * (mRows - 1)
        setMeasuredDimension(width, height + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutChildren()
    }

    private fun layoutChildren() {
        if (mRows <= 0 || mColumns <= 0) {
            return
        }
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i) as ImageView
            val row = i / mColumns
            val col = i % mColumns
            val left = (childWidth + space) * col + paddingLeft
            val top = (childHeight + space) * row + paddingTop
            val right = left + childWidth
            val bottom = top + childHeight
            view.layout(left, top, right, bottom)
            view.setOnClickListener { v: View? ->
                if (mListener != null) {
                    mListener!!.onImageClick(i, view)
                }
            }
        }
    }

    fun setOnImageClickListener(listener: OnImageClickListener?) {
        mListener = listener
    }

    override fun onChildViewAdded(parent: View, child: View) {}

    override fun onChildViewRemoved(parent: View, child: View) {
        imagePool.put(child)
    }

    interface NineGridAdapter<T> {
        val count: Int
        fun getItem(position: Int): T?
        fun getView(position: Int, itemView: View?): View
    }

    interface OnImageClickListener {
        fun onImageClick(position: Int, view: View?)
    }
}