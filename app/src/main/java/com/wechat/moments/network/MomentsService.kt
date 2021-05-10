package com.wechat.moments.network

import com.wechat.moments.data.TweetsBean
import com.wechat.moments.data.UserInfoBean
import retrofit2.Response
import retrofit2.http.GET

interface MomentsService {
    @GET("user/jsmith")
    suspend fun fetchUserInfo(): Response<UserInfoBean>

    @GET("user/jsmith/tweets")
    suspend fun fetchTweets(): Response<List<TweetsBean>>
}