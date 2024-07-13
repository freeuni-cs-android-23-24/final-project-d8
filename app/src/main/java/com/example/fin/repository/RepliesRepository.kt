package com.example.fin.repository

import com.example.fin.model.ApplicationUser
import com.example.fin.model.Reply
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RepliesRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userDataRepository = UserDataRepository()
    private val repliesCollection = firestore.collection("replies")

    fun saveReply(text: String, postId: String, userId: String, onComplete: (Boolean, String?) -> Unit) {

        userDataRepository.getUserById(userId = userId) { currentUser, _ ->
            if (currentUser != null) {
                val reply = Reply(
                    authorId = currentUser.id,
                    authorName = currentUser.name,
                    enabled = true,
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

    }

    fun getRepliesByPostId(postId: String, onComplete: (List<Reply>?, String?) -> Unit) {
        repliesCollection.whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
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

    fun disableReply(replyId: String, onComplete: (Boolean, String?) -> Unit) {
        repliesCollection.document(replyId)
            .update("enabled", false)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }
}