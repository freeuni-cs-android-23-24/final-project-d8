package com.example.fin.repository

import android.net.Uri
import com.example.fin.cloud.uploadImageToStorage
import com.example.fin.model.UserPost
import com.google.firebase.auth.FirebaseAuth

class UserPostRepository(private val firestoreRepository: FirestoreRepository) {

    fun savePost(bodyText: String, imageUri: Uri?, onComplete: (Boolean, String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (imageUri != null) {
            uploadImageToStorage(imageUri) { result, _ ->
                if (currentUser != null) {
                    val post = UserPost(
                        authorId = currentUser.uid,
                        authorName = currentUser.displayName ?: "N/A",
                        imageUrl = result,
                        postBodyText = bodyText
                    )
                    firestoreRepository.savePost(post, onComplete)
                } else {
                    onComplete(false, "User not authenticated")
                }
            }

        }
        else {
            if (currentUser != null) {
                val post = UserPost(
                    authorId = currentUser.uid,
                    authorName = currentUser.displayName ?: "N/A",
                    imageUrl = "",
                    postBodyText = bodyText
                )
                firestoreRepository.savePost(post, onComplete)
            }
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

    fun disablePost(postId: String, onComplete: (Boolean, String?) -> Unit) {
        firestoreRepository.disablePostByPostId(postId, onComplete)
    }
}