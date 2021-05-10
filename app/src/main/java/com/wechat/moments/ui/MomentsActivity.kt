package com.wechat.moments.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.timespace.base.android.BaseVMActivity
import com.timespace.base.utils.LogUtils
import com.wechat.moments.BaseConstants
import com.wechat.moments.R
import com.wechat.moments.data.TweetsBean
import com.wechat.moments.databinding.ActivityMomentsBinding
import com.wechat.moments.network.MomentsService
import com.wechat.moments.network.RetrofitManager
import com.wechat.moments.repos.MomentsRemoteDataSource
import com.wechat.moments.utils.LoadingState
import com.wechat.moments.viewmodel.MomentsViewModel
import kotlin.math.abs

class MomentsActivity : BaseVMActivity<MomentsViewModel, ActivityMomentsBinding>() {
    private var mTitleViewHeight = 0
    private var mAppBarLayoutHeight = 0
    private lateinit var mRlTitleView: View
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mTweetList: RecyclerView
    private lateinit var mAppBarLayout: AppBarLayout
    private val mMomentsAdapter: MomentsAdapter by lazy { MomentsAdapter(this) }
    private var mList: MutableList<TweetsBean> = mutableListOf()

    override val mViewModel: MomentsViewModel by lazy {
        MomentsViewModel(
                MomentsRemoteDataSource(
                        RetrofitManager.INSTANCE.getService(
                                MomentsService::class.java,
                                BaseConstants.BASE_URL
                        )!!
                )
        )
    }

    override fun getLayoutResId() = R.layout.activity_moments

    @SuppressLint("CutPasteId")
    override fun initView() {
        mRlTitleView = findViewById(R.id.rl_bar_title)
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        mTweetList = findViewById(R.id.tweet_list)
        mAppBarLayout = findViewById(R.id.app_bar)
        mSwipeRefreshLayout.apply {
            isRefreshing = true
            setProgressViewEndTarget(
                    false,
                    (100 * resources.displayMetrics.density).toInt()
            )
            setOnRefreshListener{
                mViewModel.refreshData()
            }
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mTweetList.apply {
            layoutManager = linearLayoutManager
            adapter = mMomentsAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        mAppBarLayout.post {
            mTitleViewHeight = mRlTitleView.height
            mAppBarLayoutHeight = mAppBarLayout.height
        }
        findViewById<TextView>(R.id.tv_bar_title).setOnClickListener {
            mTweetList.scrollToPosition(0)
        }
        appBarEvent()
        recyclerViewEvent()
    }

    override fun initData() {
        mViewModel.refreshData()
    }

    override fun startObserve() {
        mViewModel.apply {
            mTweetList.observe(this@MomentsActivity, { tweetList ->
                if (tweetList.isNotEmpty()) {
                    mSwipeRefreshLayout.isRefreshing = false
                    LogUtils.d(TAG, "startObserve mTweetList size = ${tweetList.size}")
                    if (mViewModel.isStartPage()) {
                        LogUtils.d(TAG, "startObserve isStartPage")
                        mList.clear()
                    }
                    mList.addAll(tweetList)
                    mMomentsAdapter.setData(mList) {
                        mMomentsAdapter.notifyDataSetChanged()
                    }
                    LogUtils.d(TAG, "startObserve mList = ${mList.size}")
                }
            })
            mUserInfo.observe(this@MomentsActivity, { userInfo ->
                mSwipeRefreshLayout.isRefreshing = false
                Glide.with(this@MomentsActivity)
                        .load(userInfo.profileImage)
                        .apply(
                                RequestOptions()
                                        .placeholder(R.mipmap.ic_launcher)
                                        .error(R.mipmap.ic_launcher)
                                        .centerCrop()
                        )
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(findViewById(R.id.user_bg))
                Glide.with(this@MomentsActivity)
                        .load(userInfo.avatar)
                        .apply(
                                RequestOptions()
                                        .placeholder(R.mipmap.ic_launcher)
                                        .error(R.mipmap.ic_launcher)
                                        .centerCrop()
                        )
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(findViewById(R.id.self_head))
                findViewById<TextView>(R.id.self_name).text = userInfo.nick
            })
            mViewModel.mLoadMore.observe(this@MomentsActivity, { loadMoreBean ->
                if (loadMoreBean.hasMoreData) {
                    mMomentsAdapter.apply {
                        getFooterItem()?.let {
                            mMomentsAdapter.setFootView(it, LoadingState.LOADING_COMPLETE)
                        }
                    }
                } else {
                    mMomentsAdapter.apply {
                        getFooterItem()?.let {
                            mMomentsAdapter.setFootView(it, LoadingState.LOADING_NO_MORE)
                        }
                    }
                }
            })
        }
    }

    private fun recyclerViewEvent() {
        mTweetList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                try {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mMomentsAdapter.apply {
                            getFooterItem()?.let {
                                mMomentsAdapter.setFootView(it, LoadingState.LOADING)
                            }
                        }
                        Glide.with(this@MomentsActivity).resumeRequests()
                        mViewModel.loadMoreData()
                    } else {
                        Glide.with(this@MomentsActivity).pauseRequests()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun appBarEvent() {
        mAppBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { _, verticalOffset: Int ->
            if (verticalOffset >= 0) {
                mSwipeRefreshLayout.isEnabled = true
                mRlTitleView.alpha = 0f
            } else {
                if (!mSwipeRefreshLayout.isRefreshing) {
                    mSwipeRefreshLayout.isEnabled = false
                }
                val abs = abs(verticalOffset)
                if (abs <= mAppBarLayoutHeight - (mTitleViewHeight + getStatusBarHeight(this@MomentsActivity))) {
                    val alpha = abs.toFloat() / mAppBarLayoutHeight
                    mRlTitleView.alpha = alpha
                } else {
                    mRlTitleView.alpha = 1.0f
                }
            }
        })
    }

    private fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        return resources.getDimensionPixelSize(
                resources.getIdentifier(
                        "status_bar_height",
                        "dimen",
                        "android"
                )
        )
    }
}