package com.example.fin.repository

import android.net.Uri
import com.example.fin.cloud.uploadImageToStorage
import com.example.fin.model.UserPost
import com.google.firebase.auth.FirebaseAuth

class UserPostRepository(private val firestoreRepository: FirestoreRepository) {
    private var userDataRepository = UserDataRepository()

    fun savePost(bodyText: String, imageUri: Uri?, onComplete: (Boolean, String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userDataRepository.getUserById(currentUser.uid) { currentUserData, _ ->
                if (imageUri != null) {
                    uploadImageToStorage(imageUri) { result, _ ->
                        val post = UserPost(
                            authorId = currentUserData!!.id,
                            authorName = currentUserData.name,
                            imageUrl = result,
                            postBodyText = bodyText
                        )
                        firestoreRepository.savePost(post, onComplete)
                    }

                } else {
                    val post = UserPost(
                        authorId = currentUserData!!.id,
                        authorName = currentUserData.name,
                        imageUrl = "",
                        postBodyText = bodyText
                    )
                    firestoreRepository.savePost(post, onComplete)

                }
            }
        } else {
            onComplete(false, "User not authenticated")
        }

    }

    fun searchPosts(searchTerm: String, onComplete: (List<UserPost>?, String?) -> Unit) {
        firestoreRepository.searchPosts(searchTerm, onComplete)
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