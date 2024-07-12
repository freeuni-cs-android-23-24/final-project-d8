package com.example.fin.repository

import com.example.fin.model.ApplicationUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserRepository private constructor() {

    private val _currentUser = MutableStateFlow<ApplicationUser?>(null)

    val currentUser: StateFlow<ApplicationUser?> = _currentUser

    init {
        initialize()
    }

    fun initialize() {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                val applicationUser = ApplicationUser(user.uid, user.email ?: "N/A", user.displayName ?: "N/A")
                _currentUser.value = applicationUser
            } else {
                _currentUser.value = null
            }
        }
    }

    fun update(user: ApplicationUser) {
        val applicationUser = ApplicationUser(user.id, user.email, user.name, user.moderator)
        _currentUser.value = applicationUser
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository().also { instance = it }
            }
        }
    }
}