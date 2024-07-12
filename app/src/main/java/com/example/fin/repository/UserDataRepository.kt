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
                    moderator = false,
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

    fun getUserProfileUrl(searchUser: ApplicationUser?, onComplete: (String?, String?) -> Unit) {
        var searchUserId = "";
        if (searchUser != null) {
            searchUserId = searchUser.id;
        }
        userDataCollection.whereEqualTo("id", searchUserId)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val user = result.documents[0].toObject(ApplicationUser::class.java)
                    if (user != null) {
                        onComplete(user.profileUrl, null)
                    } else {
                        onComplete(null, "User data parsing error")
                    }
                } else {
                    onComplete(null, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }


    fun updateUsername(userId : String, editedUserName : String, onComplete: (Boolean, String?) -> Unit){
        userDataCollection.whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0].reference
                    document.update("name", editedUserName)
                        .addOnSuccessListener {
                            onComplete(true, null)
                        }
                        .addOnFailureListener { exception ->
                            onComplete(false, exception.message)
                        }
                } else {
                    onComplete(false, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }

    fun updateProfileUrl(userId: String, newUrl: String, onComplete: (Boolean, String?) -> Unit) {
        userDataCollection.whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0].reference
                    document.update("profileUrl", newUrl)
                        .addOnSuccessListener {
                            onComplete(true, null)
                        }
                        .addOnFailureListener { exception ->
                            onComplete(false, exception.message)
                        }
                } else {
                    onComplete(false, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }
}