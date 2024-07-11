package com.example.fin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.fin.model.UserPost
import com.example.fin.repository.FirestoreRepository
import com.example.fin.repository.UserPostRepository
import com.example.fin.repository.UserRepository
import com.example.fin.ui.posts.UserPostUI
import com.example.fin.ui.theme.FinTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val userRepository = UserRepository.getInstance()
    private val fireStoreRepository = FirestoreRepository()
    private val userPostRepository = UserPostRepository(fireStoreRepository)

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
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "ApplicationScreen") {
                    composable("ApplicationScreen") {
                        ApplicationScreen(
                            navController = navController,
                            userRepository = userRepository,
                            userPostRepository = userPostRepository,
                            signInLauncher = signInLauncher,
                        )
                    }
                    composable(
                        route = "UserPostPage/{userPostId}",
                        arguments = listOf(navArgument("userPostId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        UserPostPage(
                            userPostId = backStackEntry.arguments?.getString("userPostId") ?: "",
                            userPostRepository = userPostRepository,
                            userRepository = userRepository
                        )
                    }
                }

            }
        }
    }

}

@Composable
fun ApplicationScreen(
    navController: NavHostController,
    userRepository: UserRepository,
    userPostRepository: UserPostRepository,
    signInLauncher: ActivityResultLauncher<Intent>
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        var userPosts by remember { mutableStateOf<List<UserPost>>(emptyList()) }
        val currentUser = userRepository.currentUser.collectAsState().value

        if (currentUser != null) {
            Text(
                text = "Hello, ${currentUser.name}",
            )
            Button(
                onClick = { FirebaseAuth.getInstance().signOut() },
            ) {
                Text(text = "Sign Out", color = Color.White)
            }
            Button(
                onClick = {
                    userPostRepository.savePost("Hello, World") { _, _ -> }
                },
            ) {
                Text(text = "Create Post", color = Color.White)
            }
        } else {
            Button(
                onClick = { signInLauncher.launch(createSignInIntent()) },
            ) {
                Text(text = "Sign in", color = Color.White)
            }
        }


        userPostRepository.getAllPosts { result, _ ->
            if (result != null) {
                userPosts = result
            }
        }

        print(userPosts.toString())

        for (post in userPosts) {
            UserPostUI(post, onClick = {
                navController.navigate("UserPostPage/${post.userPostId}")
            })
        }

    }
}

@Composable
fun UserPostPage(userPostId: String, userPostRepository: UserPostRepository, userRepository: UserRepository) {
    var userPost by remember { mutableStateOf(UserPost()) }

    userPostRepository.getPostById(userPostId) { result, _ ->
        if (result != null) {
            userPost = result
        }
    }

    UserPostUI(userPost, {})
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