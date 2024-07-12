package com.example.fin.repository

import com.example.fin.model.ApplicationUser
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class UserDataRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userDataCollection = firestore.collection("userData")

    fun saveUserData(user: FirebaseUser, onComplete: (ApplicationUser, String?) -> Unit) {

        getUserById(user.uid) { result, _ ->
            if (result == null) {
                val applicationUser = ApplicationUser(
                    id = user.uid,
                    email = user.email.toString(),
                    name = user.displayName.toString(),
                    isModerator = false,
                )

                userDataCollection.add(applicationUser)
                    .addOnSuccessListener {
                        onComplete(applicationUser, null)
                    }
                    .addOnFailureListener { exception ->
                        onComplete(ApplicationUser(), exception.message)
                    }
            } else {
                onComplete(result, null)
            }
        }
    }

    fun getUserById(userId: String, onComplete: (ApplicationUser?, String?) -> Unit) {
        userDataCollection.whereEqualTo("id", userId)
            .get(Source.SERVER)
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