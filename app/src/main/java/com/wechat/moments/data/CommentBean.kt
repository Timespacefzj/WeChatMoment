package com.wechat.moments.data

data class Comment(
    val content: String,
    val sender: Sender
)

data class Sender(
    val username: String,
    val nick: String,
    val avatar: String
)