package com.wechat.moments.data

import com.google.gson.annotations.SerializedName

data class TweetsBean (
    val content: String? = null,
    val sender: Sender? = null,
    val images: MutableList<ImagesBean>? = null,
    val comments: MutableList<Comment>? = null,
    @SerializedName(value = "error", alternate = ["unknown error"])
    val error: String? = null
)

data class ImagesBean(
    val url: String
)