package com.example.fin.repository

import android.net.Uri
import com.example.fin.model.UserPost
import com.google.firebase.auth.FirebaseAuth

class UserPostRepository(private val firestoreRepository: FirestoreRepository) {

    fun savePost(bodyText: String, imageUri : String? , onComplete: (Boolean, String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val post = UserPost(
                authorId = currentUser.uid,
                authorName = currentUser.displayName ?: "N/A",
                imageUrl = imageUri ?: "",
                postBodyText = bodyText
            )
            firestoreRepository.savePost(post, onComplete)
        } else {
            onComplete(false, "User not authenticated")
        }
    }

    fun getAllPosts(onComplete: (List<UserPost>?, String?) -> Unit) {
        firestoreRepository.getAllPosts(onComplete)
    }

    fun getPostById(userPostId: String, onComplete: (UserPost?, String?) -> Unit) {
        firestoreRepository.getPostById(userPostId, onComplete)
    }

    fun getPostsByUser(authorId: String, onComplete: (List<UserPost>?, String?) -> Unit) {
        firestoreRepository.getPostsByUser(authorId, onComplete)
    }
}