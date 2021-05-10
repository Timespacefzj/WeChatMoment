package com.wechat.moments.widget

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.timespace.base.utils.LogUtils
import com.wechat.moments.data.Comment

class CommentsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "CommentsView"
    }

    private var mDatas: List<Comment>? = null

    init {
        orientation = VERTICAL
    }

    fun setList(list: List<Comment>?) {
        mDatas = list
    }

    fun notifyDataSetChanged() {
        LogUtils.d(TAG, "notifyDataSetChanged")
        removeAllViews()
        if (mDatas.isNullOrEmpty()) {
            return
        }
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 10, 0, 10)
        for (i in mDatas!!.indices) {
            val view = getView(i)
            addView(view, i, layoutParams)
        }
    }

    private fun getView(position: Int): View {
        val (content, sender) = mDatas!![position]
        LogUtils.d(TAG, "getView position=$position content=$content")
        val textView = TextView(context)
        textView.textSize = 15f
        textView.setTextColor(-0x979798)
        val builder = SpannableStringBuilder()
        builder.append(sender.nick)
        builder.append(" : ")
        builder.append(content)
        textView.text = builder
        return textView
    }
}