package com.example.fin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.fin.model.ApplicationUser
import com.example.fin.model.Reply
import com.example.fin.model.UserPost
import com.example.fin.repository.*
import com.example.fin.ui.posts.UserPostUI
import com.example.fin.ui.replies.ReplyInput
import com.example.fin.ui.replies.ReplyItem
import com.example.fin.ui.theme.FinTheme
import com.example.fin.ui.user.UserProfileUI
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val userRepository = UserRepository.getInstance()
    private val fireStoreRepository = FirestoreRepository()
    private val userPostRepository = UserPostRepository(fireStoreRepository)
    private val userDataRepository = UserDataRepository()
    private val repliesRepository = RepliesRepository()

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return@registerForActivityResult
        userRepository.update(currentUser)
        userDataRepository.saveUserData(currentUser, { _, _ -> })

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
                            )
                        }
                        composable(
                            route = "UserPostPage/{userPostId}",
                            arguments = listOf(navArgument("userPostId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            UserPostPage(
                                userPostId = backStackEntry.arguments?.getString("userPostId") ?: "",
                                navController = navController,
                                userPostRepository = userPostRepository,
                                userRepository = userRepository,
                                repliesRepository = repliesRepository
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

@Composable
fun ApplicationScreen(
    navController: NavHostController,
    userRepository: UserRepository,
    userPostRepository: UserPostRepository,
    signInLauncher: ActivityResultLauncher<Intent>
) {
    //val postViewModel: PostViewModel = viewModel()
    Column(
        modifier = Modifier.padding(10.dp).fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        var userPosts by remember { mutableStateOf<List<UserPost>>(emptyList()) }
        val currentUser = userRepository.currentUser.collectAsState().value
        var postContent by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }
        if (currentUser != null) {
            Text(
                text = "Hello, ${currentUser.name}",
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        navController.navigate("UserProfilePage/${currentUser.id}")
                    },
                ) {
                    Text(text = "My Profile", color = Color.White)
                }
                Button(
                    onClick = { FirebaseAuth.getInstance().signOut() },
                ) {
                    Text(text = "Sign Out", color = Color.White)
                }
            }
//            CreatePostUI(postViewModel = postViewModel)
            Row(modifier = Modifier.padding(top = 8.dp)) {
                TextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    label = { Text("Create a post") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        if (postContent.isNotBlank()) {
                            showDialog = true
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Post")
                }
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Confirm Post") },
                    text = { Text(text = "Are you sure you want to post this?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                userPostRepository.savePost(postContent) { _, _ -> }
                                postContent = ""  // Clear the TextField after saving
                                showDialog = false
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text("No")
                        }
                    }
                )
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

        for (post in userPosts) {
            UserPostUI(post, onClick = {
                navController.navigate("UserPostPage/${post.userPostId}")
            })
        }

    }
}


@Composable
fun UserPostPage(
    userPostId: String,
    navController: NavHostController,
    userPostRepository: UserPostRepository,
    userRepository: UserRepository,
    repliesRepository: RepliesRepository
) {
    var userPost by remember { mutableStateOf(UserPost()) }
    var replies by remember { mutableStateOf<List<Reply>>(emptyList()) }
    val currentUser = userRepository.currentUser.collectAsState().value


    userPostRepository.getPostById(userPostId) { result, _ ->
        if (result != null) {
            userPost = result
        }
    }

    repliesRepository.getRepliesByPostId(userPost.userPostId) { result, _ ->
        if (result != null) {
            replies = result
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        UserPostUI(userPost, onClick = {
            navController.navigate("UserProfilePage/${userPost.authorId}")
        })
        if (currentUser != null) {
            ReplyInput { reply ->
                repliesRepository.saveReply(reply, userPostId) { _, _ -> }
                repliesRepository.getRepliesByPostId(userPost.userPostId) { result, _ ->
                    if (result != null) {
                        replies = result
                    }
                }
            }
        }

        for (reply in replies) {
            ReplyItem(reply, onClick = {
                navController.navigate("UserProfilePage/${reply.authorId}")
            })
        }
    }
}

@Composable
fun UserProfilePage(
    userId: String,
    navController: NavHostController,
    userPostRepository: UserPostRepository,
    userDataRepository: UserDataRepository,
    userRepository: UserRepository
) {
//    val currentUser = userRepository.currentUser.collectAsState().value

    var user by remember { mutableStateOf(ApplicationUser()) }
    var posts by remember { mutableStateOf<List<UserPost>>(emptyList()) }

    userDataRepository.getUserById(userId) { result, _ ->
        if (result != null) {
            user = result
        }
    }

    userPostRepository.getPostsByUser(userId) { result, _ ->
        if (result != null) {
            posts = result
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        UserProfileUI(user)

        if (posts.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(10.dp),
                text = "${user.name}'s posts:",
            )
        }

        for (post in posts) {
            UserPostUI(post, onClick = {
                navController.navigate("UserPostPage/${post.userPostId}")
            })
        }
    }
}

private fun createSignInIntent(): Intent {
    val providers = listOf(
        EmailBuilder().build(), GoogleBuilder().build()
    )

    return AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
        .setAlwaysShowSignInMethodScreen(false).setIsSmartLockEnabled(false).build()
}