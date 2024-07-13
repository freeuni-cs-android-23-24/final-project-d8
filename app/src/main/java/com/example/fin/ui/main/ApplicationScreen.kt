package com.example.fin.ui.main

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fin.model.ApplicationUser
import com.example.fin.repository.UserDataRepository
import com.example.fin.repository.UserPostRepository
import com.example.fin.repository.UserRepository
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
@Composable
fun ApplicationScreen(
    navController: NavHostController,
    userRepository: UserRepository,
    userPostRepository: UserPostRepository,
    userDataRepository: UserDataRepository,
    signInLauncher: ActivityResultLauncher<Intent>
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val currentUser = userRepository.currentUser.collectAsState().value

        GreetingAndProfileSection(
            currentUser = currentUser,
            navController = navController,
            signInLauncher = signInLauncher
        )

        SearchAndPostSection(
            navController = navController,
            currentUser = currentUser,
            userPostRepository = userPostRepository,
            userDataRepository = userDataRepository
        )
    }
}

@Composable
private fun GreetingAndProfileSection(
    currentUser: ApplicationUser?,
    navController: NavHostController,
    signInLauncher: ActivityResultLauncher<Intent>
) {
    currentUser?.let { user ->
        Text(text = "Hello, ${user.name}")

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("UserProfilePage/${user.id}") }
            ) {
                Text(text = "My Profile", color = Color.White)
            }

            Button(
                onClick = { FirebaseAuth.getInstance().signOut() }
            ) {
                Text(text = "Sign Out", color = Color.White)
            }
        }
    } ?: SignInButton(signInLauncher = signInLauncher)
}


@Composable
private fun SignInButton(signInLauncher: ActivityResultLauncher<Intent>) {
    Button(onClick = { signInLauncher.launch(createSignInIntent()) }) {
        Text(text = "Sign in", color = Color.White)
    }
}

private fun createSignInIntent(): Intent {
    val providers = listOf(
        AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
    )
    return AuthUI.getInstance().createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setAlwaysShowSignInMethodScreen(false)
        .setIsSmartLockEnabled(false)
        .build()
}



