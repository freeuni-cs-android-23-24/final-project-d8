package com.example.fin.repository

import com.example.fin.model.ApplicationUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserRepository private constructor() {

    private val _currentUser = MutableStateFlow<ApplicationUser?>(null)

    private val userDataRepository = UserDataRepository()

    val currentUser: StateFlow<ApplicationUser?> = _currentUser

    init {
        initialize()
    }

    fun initialize() {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                userDataRepository.getUserById(user.uid) { result, _ ->
                    _currentUser.value = result
                }
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