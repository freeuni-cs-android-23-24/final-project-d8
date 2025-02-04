package com.example.fin.model

data class ApplicationUser(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val moderator: Boolean = false,
    var profileUrl: String = ""
)