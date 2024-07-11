package com.example.fin.repository

import com.example.fin.model.UserPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class UserPostRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("userPosts")

    fun savePost(bodyText: String, onComplete: (Boolean, String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val post = UserPost(
                authorId = currentUser.uid,
                authorName = currentUser.displayName ?: "N/A",
                postBodyText = bodyText
            )
            postsCollection.add(post)
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

    fun getAllPosts(onComplete: (List<UserPost>?, String?) -> Unit) {
        postsCollection.orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                val posts = result.map { document ->
                    val post = document.toObject(UserPost::class.java)
                    post.copy(userPostId = document.id)
                }
                onComplete(posts, null)
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }

    fun getPostById(userPostId: String, onComplete: (UserPost?, String?) -> Unit) {
        postsCollection.whereEqualTo(FieldPath.documentId(), userPostId)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    val post = document.toObject(UserPost::class.java)?.copy(userPostId = document.id)
                    onComplete(post, null)
                } else {
                    onComplete(null, "Post not found")
                }
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }

    fun getPostsByUser(authorId: String, onComplete: (List<UserPost>?, String?) -> Unit) {
        postsCollection.whereEqualTo("authorId", authorId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                val posts = result.map { document ->
                    val post = document.toObject(UserPost::class.java)
                    post.copy(userPostId = document.id)
                }
                onComplete(posts, null)
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }
}