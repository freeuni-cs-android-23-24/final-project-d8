package com.example.fin.repository

import com.example.fin.model.Reply
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RepliesRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val repliesCollection = firestore.collection("replies")

    fun saveReply(text: String, postId: String, onComplete: (Boolean, String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val reply = Reply(
                authorId = currentUser.uid,
                authorName = currentUser.displayName ?: "N/A",
                postId = postId,
                text = text
            )
            repliesCollection.add(reply)
                .addOnSuccessListener {
                    onComplete(true, null)
                }
                .addOnFailureListener { exception ->
                    onComplete(false, exception.message)
                }
        } else {
            onComplete(false, "User not authenticated")
        }
    }

    fun getRepliesByPostId(postId: String, onComplete: (List<Reply>?, String?) -> Unit) {
        repliesCollection.whereEqualTo("postId", postId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                val replies = result.map { document ->
                    val post = document.toObject(Reply::class.java)
                    post.copy(replyId = document.id)
                }
                onComplete(replies, null)
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }
}