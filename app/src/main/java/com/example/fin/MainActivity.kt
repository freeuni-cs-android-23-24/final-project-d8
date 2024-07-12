package com.example.fin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fin.cloud.uploadImageToStorage
import com.example.fin.model.ApplicationUser
import com.example.fin.model.Reply
import com.example.fin.model.UserPost
import com.example.fin.repository.*
import com.example.fin.ui.main.ApplicationScreen
import com.example.fin.ui.posts.UserPostUI
import com.example.fin.ui.replies.ReplyInput
import com.example.fin.ui.replies.ReplyItem
import com.example.fin.ui.theme.FinTheme
import com.example.fin.ui.user.UserPostPage
import com.example.fin.ui.user.UserProfilePage
import com.example.fin.ui.user.UserProfileUI
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val userRepository = UserRepository.getInstance()
    private val fireStoreRepository = FirestoreRepository()
    private val userPostRepository = UserPostRepository(fireStoreRepository)
    private val repliesRepository = RepliesRepository()
    private val userDataRepository = UserDataRepository()

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val currentUser =
                FirebaseAuth.getInstance().currentUser ?: return@registerForActivityResult
            userDataRepository.saveUserData(currentUser) { result, _ ->
                userRepository.update(result)
            }

        }
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinTheme {
                Surface(
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "ApplicationScreen") {
                        composable("ApplicationScreen") {
                            ApplicationScreen(
                                navController = navController,
                                userRepository = userRepository,
                                userPostRepository = userPostRepository,
                                signInLauncher = signInLauncher,
                                userDataRepository = userDataRepository
                            )
                        }
                        composable(
                            route = "UserPostPage/{userPostId}",
                            arguments = listOf(navArgument("userPostId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            UserPostPage(
                                userPostId = backStackEntry.arguments?.getString("userPostId")
                                    ?: "",
                                navController = navController,
                                userPostRepository = userPostRepository,
                                userRepository = userRepository,
                                repliesRepository = repliesRepository,
                                userDataRepository = userDataRepository
                            )
                        }
                        composable(
                            route = "UserProfilePage/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            UserProfilePage(
                                userId = backStackEntry.arguments?.getString("userId") ?: "",
                                navController = navController,
                                userPostRepository = userPostRepository,
                                userDataRepository = userDataRepository,
                                userRepository = userRepository
                            )
                        }
                    }

                }
            }
        }
    }
}
