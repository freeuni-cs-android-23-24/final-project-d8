package com.example.fin.model

data class UserPost(
    val authorId: String = "",
    val authorName: String = "",
    val postBodyText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)