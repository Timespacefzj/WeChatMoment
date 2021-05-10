package com.wechat.moments.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timespace.base.system.DensityUtils
import com.wechat.moments.R
import com.wechat.moments.data.ImagesBean
import com.wechat.moments.widget.NineGridView.NineGridAdapter

class NineImageAdapter(
    private val mContext: Context,
    private val mImageBeans: MutableList<ImagesBean>
) : NineGridAdapter<String> {

    private val mRequestOptions: RequestOptions by lazy {
        RequestOptions().centerCrop().override(itemSize, itemSize)
    }

    private val itemSize = (DensityUtils.getScreenWidth() - 2 * DensityUtils.dp2px(4f) - DensityUtils.dp2px(54f)) / 3

    override val count: Int
        get() = mImageBeans.size

    override fun getItem(position: Int): String? {
        return if (position < mImageBeans.size) {
            mImageBeans[position].url
        } else null
    }

    override fun getView(position: Int, itemView: View?): View {
        val imageView: ImageView
        if (itemView == null) {
            imageView = ImageView(mContext)
            imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.base_F2F2F2))
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } else {
            imageView = itemView as ImageView
        }
        val url = mImageBeans[position].url
        Glide.with(mContext)
            .load(url)
            .apply(mRequestOptions)
            .into(imageView)
        return imageView
    }
}