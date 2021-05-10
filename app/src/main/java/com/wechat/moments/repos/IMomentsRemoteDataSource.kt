package com.wechat.moments.repos

import com.timespace.base.repos.IRemoteDataSource
import com.wechat.moments.data.TweetsBean
import com.timespace.base.network.Result
import com.wechat.moments.data.UserInfoBean
import kotlinx.coroutines.CoroutineScope

interface IMomentsRemoteDataSource: IRemoteDataSource {
    suspend fun requestTweetsList(): Result<List<TweetsBean>>

    suspend fun requestUserInfo(): Result<UserInfoBean>
}