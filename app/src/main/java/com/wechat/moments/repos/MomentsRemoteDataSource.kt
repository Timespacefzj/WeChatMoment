package com.wechat.moments.repos

import com.timespace.base.network.Result
import com.wechat.moments.data.TweetsBean
import com.wechat.moments.data.UserInfoBean
import com.wechat.moments.network.MomentsService

class MomentsRemoteDataSource(
    private val service: MomentsService
): BaseRemoteDataSource(), IMomentsRemoteDataSource {
    override suspend fun requestTweetsList(): Result<List<TweetsBean>> {
        return safeApiCall(
            call = {
                executeResponse(service.fetchTweets())
            },
            message = ""
        )
    }

    override suspend fun requestUserInfo(): Result<UserInfoBean> {
        return safeApiCall(
            call = {
                executeResponse(
                    service.fetchUserInfo()
                )
            },
            message = ""
        )
    }
}