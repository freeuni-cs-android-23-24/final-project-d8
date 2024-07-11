package com.example.fin.repository

import com.example.fin.model.UserPost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
class FireStoreRepository {
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
        postsCollection.orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                val posts = result.map { document -> document.toObject(UserPost::class.java) }
                onComplete(posts, null)
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
                val posts = result.map { document -> document.toObject(UserPost::class.java) }
                onComplete(posts, null)
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }
}
