package com.example.fin.model

data class UserPost(
    val userPostId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val postBodyText: String = "",
    val enabled: Boolean = true,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)