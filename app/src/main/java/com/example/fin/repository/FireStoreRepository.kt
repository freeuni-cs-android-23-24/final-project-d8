package com.example.fin.repository

import com.example.fin.model.UserPost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("userPosts")

    fun savePost(post: UserPost, onComplete: (Boolean, String?) -> Unit) {
        postsCollection.add(post)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }

    fun getAllPosts(onComplete: (List<UserPost>?, String?) -> Unit) {
        postsCollection
            .whereEqualTo("enabled", true)
            .orderBy("timestamp", Query.Direction.DESCENDING)
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
        postsCollection.document(userPostId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
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
        postsCollection
            .whereEqualTo("enabled", true)
            .whereEqualTo("authorId", authorId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
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

    fun disablePostByPostId(postId: String, onComplete: (Boolean, String?) -> Unit) {
        postsCollection.document(postId)
            .update("enabled", false)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }
}