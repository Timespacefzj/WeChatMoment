package com.wechat.moments.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.timespace.base.android.BaseViewModel
import com.timespace.base.network.Result
import com.timespace.base.utils.LogUtils
import com.wechat.moments.data.LoadMoreBean
import com.wechat.moments.data.TweetsBean
import com.wechat.moments.data.UserInfoBean
import com.wechat.moments.repos.MomentsRemoteDataSource
import com.wechat.moments.utils.TweetsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MomentsViewModel(
    private val momentsRepository: MomentsRemoteDataSource
): BaseViewModel() {
    companion object {
        private const val TAG = "MomentsViewModel"
    }

    private var page = 1

    val mTweetList: LiveData<List<TweetsBean>>
        get() = _tweetList

    val mUserInfo: LiveData<UserInfoBean>
        get() = _userInfo

    val mLoadMore: LiveData<LoadMoreBean>
        get() = _loadMore

    private val _tweetList = MutableLiveData<List<TweetsBean>>()
    private val _userInfo = MutableLiveData<UserInfoBean>()
    private val _loadMore = MutableLiveData<LoadMoreBean>()

    fun refreshData() {
        page = 1
        loadUserInfo()
        loadTweetsList()
    }

    fun loadUserInfo() {
        launchOnIO {
            val result = momentsRepository.requestUserInfo()
            withContext(Dispatchers.Main) {
                if (result is Result.Success) {
                    _userInfo.value = result.data
                }
            }
        }
    }

    fun loadTweetsList() {
        launchOnIO {
            val result = momentsRepository.requestTweetsList()
            withContext(Dispatchers.Main) {
                if (result is Result.Success) {
                    val list = mutableListOf<TweetsBean>()
                    result.data?.let {
                        //LogUtils.d(TAG, "loadTweetsList size=$it.size")
                        it.map { tweet ->
                            //LogUtils.d(TAG, "loadTweetsList tweet=${tweet}")
                        }
                        it.filter { tweet ->
                            tweet.error == null
                        }.forEach { newTweet ->
                            list.add(newTweet)
                        }
                        val length: Int = list.size / 5
                        val other: Int = list.size % 5
                        TweetsStore.totalPage = if (other == 0) length else length + 1
                        LogUtils.d(TAG, "loadTweetsList new size=${list.size} totalPage=${TweetsStore.totalPage}")
                        TweetsStore.saveMomentList(list)
                        _tweetList.value = getLocalMaxSize(list)
                    }
                }
            }
        }
    }

    fun loadMoreData() {
        if (page < TweetsStore.totalPage) {
            page += 1
            _tweetList.value = TweetsStore.getSomeOfMomentList(page)
            setLoadMoreState(true)
        } else {
            setLoadMoreState(false)
        }
        LogUtils.d(TAG, "loadMoreData page=$page")
    }

    fun isStartPage(): Boolean {
        return page == 1
    }

    private fun setLoadMoreState(hasMore: Boolean) {
        val loadMoreBean = LoadMoreBean(true, hasMore)
        _loadMore.value = loadMoreBean
    }

    private fun getLocalMaxSize(list: List<TweetsBean>): List<TweetsBean> {
        return if (list.size <= 5) list else list.subList(0, 5)
    }
}