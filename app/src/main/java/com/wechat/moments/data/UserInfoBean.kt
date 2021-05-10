package com.wechat.moments.data

import com.google.gson.annotations.SerializedName

data class UserInfoBean(
    @SerializedName("profile-image")
    val profileImage: String,
    val avatar: String,
    val nick: String,
    val username: String
)