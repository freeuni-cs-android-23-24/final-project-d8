package com.example.fin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fin.ui.theme.FinTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    private val userRepository = UserRepository.getInstance()

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return@registerForActivityResult
        userRepository.update(currentUser)
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinTheme {

                val currentUser = userRepository.currentUser.collectAsState().value

                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                ) {
                    if (currentUser != null) {
                        Text(
                            text = "Hello, ${currentUser.name}",
                        )
                        Button(
                            onClick = { FirebaseAuth.getInstance().signOut() },
                        ) {
                            Text(text = "Sign Out", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { signInLauncher.launch(createSignInIntent()) },
                        ) {
                            Text(text = "Sign in", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    private fun createSignInIntent(): Intent {
        val providers = listOf(
            EmailBuilder().build(),
            GoogleBuilder().build()
        )

        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setAlwaysShowSignInMethodScreen(false)
            .setIsSmartLockEnabled(false)
            .build()
    }
}