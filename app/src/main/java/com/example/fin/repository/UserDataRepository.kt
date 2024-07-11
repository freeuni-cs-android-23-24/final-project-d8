package com.example.fin.repository

import com.example.fin.model.ApplicationUser
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserDataRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userDataCollection = firestore.collection("userData")

    fun saveUserData(user: FirebaseUser, onComplete: (Boolean, String?) -> Unit) {
        getUserById(user.uid) { result, _ ->
            if (result == null) {
                val applicationUser = ApplicationUser(
                    id = user.uid,
                    email = user.email.toString(),
                    name = user.displayName.toString(),
                )

                userDataCollection.add(applicationUser)
                    .addOnSuccessListener {
                        onComplete(true, null)
                    }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.message)
                    }
            }
        }
    }

    fun getUserById(userId: String, onComplete: (ApplicationUser?, String?) -> Unit) {
        userDataCollection.whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val user = result.documents[0].toObject(ApplicationUser::class.java)
                    onComplete(user, null)
                } else {
                    onComplete(null, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }
}