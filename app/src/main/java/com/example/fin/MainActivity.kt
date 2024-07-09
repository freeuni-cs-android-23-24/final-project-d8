package com.example.fin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fin.ui.theme.FinTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { _ ->
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return@registerForActivityResult

        Toast.makeText(
            this,
            "Hello ${currentUser.displayName}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val providers = listOf(
            EmailBuilder().build(),
            GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setAlwaysShowSignInMethodScreen(false)
            .setIsSmartLockEnabled(false)
            .build()


        setContent {
            FinTheme {
                  signInLauncher.launch(signInIntent)
            }
        }
    }

}
