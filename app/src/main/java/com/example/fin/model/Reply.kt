package com.example.fin.model

data class Reply(
    val replyId : String = "",
    val authorId: String = "",
    val authorName: String = "",
    val postId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
