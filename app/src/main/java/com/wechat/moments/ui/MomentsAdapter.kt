package com.wechat.moments.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.timespace.base.android.BaseBindAdapter
import com.timespace.base.utils.LogUtils
import com.wechat.moments.R
import com.wechat.moments.data.TweetsBean
import com.wechat.moments.databinding.ItemFooterBinding
import com.wechat.moments.databinding.ItemMomentBinding
import com.wechat.moments.utils.LoadingState

class MomentsAdapter(
        private val mContext: Context
): BaseBindAdapter<TweetsBean>() {
    companion object {
        private const val TAG = "MomentsAdapter"
        private const val TYPE_ITEM = 1
        private const val TYPE_FOOT = 2
    }

    private var mItemFooterBinding: ItemFooterBinding? = null

    override val subClass: BaseBindAdapter<TweetsBean>
        get() = this

    override val diffCallback: DiffUtil.ItemCallback<TweetsBean>
        get() = AsyncDiffCallBack()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //LogUtils.d(TAG, "onCreateViewHolder viewType=$viewType")
        if(viewType == TYPE_ITEM) {
            val binding: ViewDataBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_moment,
                    parent,
                    false
            )
            return MomentsHolder(binding)
        } else {
            val binding: ViewDataBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_footer,
                    parent,
                    false
            )
            return FootViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //LogUtils.d(TAG, "onBindViewHolder position=$position holder=${holder}")
        when(holder) {
            is MomentsHolder -> {
                val itemTweetBinding = holder.mBinding as ItemMomentBinding
                val tweet = mData.currentList[holder.adapterPosition]
                //LogUtils.d(TAG, "onBindViewHolder tweet=${tweet}")
                tweet.content?.let {
                    itemTweetBinding.content.visibility = View.VISIBLE
                    itemTweetBinding.content.text = it
                } ?: run {
                    itemTweetBinding.content.visibility = View.GONE
                }
                tweet.sender?.nick.let {
                    itemTweetBinding.nick.text = it
                }
                Glide.with(mContext)
                        .load(tweet.sender?.avatar)
                        .apply(RequestOptions
                                .bitmapTransform(RoundedCorners(35))
                                .placeholder(R.mipmap.default_avater)
                                .error(R.mipmap.default_avater)
                        )
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemTweetBinding.avatar)
                if (tweet.images.isNullOrEmpty()) {
                    itemTweetBinding.nineGridView.visibility = View.GONE
                } else {
                    itemTweetBinding.nineGridView.visibility = View.VISIBLE
                    itemTweetBinding.nineGridView.setAdapter(NineImageAdapter(mContext, tweet.images))
                }
                itemTweetBinding.viewLine.visibility = if (position == 0) View.GONE else View.VISIBLE
                LogUtils.d(TAG, "onBindViewHolder comments=${tweet.comments}")
                if (tweet.comments.isNullOrEmpty()) {
                    itemTweetBinding.rlComment.visibility = View.GONE
                } else {
                    itemTweetBinding.rlComment.visibility = View.VISIBLE
                    itemTweetBinding.commentList.apply {
                        setList(tweet.comments)
                        notifyDataSetChanged()
                    }
                }
            }
            is FootViewHolder -> {
                val itemFooterBinding = holder.mBinding as ItemFooterBinding
                itemFooterBinding.root.visibility = View.VISIBLE
                mItemFooterBinding = itemFooterBinding
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mData.currentList.isNotEmpty()) {
            mData.currentList.size + 1
        } else 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount == position + 1) {
            TYPE_FOOT
        } else {
            TYPE_ITEM
        }
    }

    fun getFooterItem(): ItemFooterBinding? {
        return mItemFooterBinding
    }

    fun setFootView(itemFooterBinding: ItemFooterBinding, loadingState: Int) {
        when (loadingState) {
            LoadingState.LOADING -> {
                itemFooterBinding.footerProgress.visibility = View.VISIBLE
                itemFooterBinding.footerLoading.visibility = View.VISIBLE
                itemFooterBinding.footerLoading.text = "正在加载..."
            }
            LoadingState.LOADING_COMPLETE -> {
                itemFooterBinding.footerProgress.visibility = View.GONE
                itemFooterBinding.footerLoading.visibility = View.GONE
            }
            LoadingState.LOADING_NO_MORE -> {
                itemFooterBinding.footerProgress.visibility = View.GONE
                itemFooterBinding.footerLoading.visibility = View.VISIBLE
                itemFooterBinding.footerLoading.text = "到底了"
            }
            LoadingState.LOADING_ERROR -> {
                itemFooterBinding.footerProgress.visibility = View.GONE
                itemFooterBinding.footerLoading.visibility = View.VISIBLE
                itemFooterBinding.footerLoading.text = "加载出错~"
            }
        }
    }

    inner class FootViewHolder(val mBinding: ViewDataBinding) : RecyclerView.ViewHolder(mBinding.root)

    inner class MomentsHolder(val mBinding: ViewDataBinding) : RecyclerView.ViewHolder(mBinding.root)

    class AsyncDiffCallBack: DiffUtil.ItemCallback<TweetsBean>() {
        override fun areItemsTheSame(oldItem: TweetsBean, newItem: TweetsBean): Boolean {
            return oldItem.sender?.username == oldItem.sender?.username
        }

        override fun areContentsTheSame(oldItem: TweetsBean, newItem: TweetsBean): Boolean {
            return oldItem == newItem
        }
    }
}