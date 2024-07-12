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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val currentUser =
                FirebaseAuth.getInstance().currentUser ?: return@registerForActivityResult
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
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        var userPosts by remember { mutableStateOf<List<UserPost>>(emptyList()) }
        val currentUser = userRepository.currentUser.collectAsState().value
        var postContent by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var fileName by remember { mutableStateOf("") }
        val imagePickerLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                selectedImageUri = uri
                selectedImageUri?.let {
                    fileName = "(Attached Image :" + it.lastPathSegment?.substringAfterLast("/") + ")"
                    postContent += "\n"
                    postContent += fileName
                }
            }
        if (currentUser != null) {
            Text(
                text = "Hello, ${currentUser.name}",
            )
            Button(
                onClick = { FirebaseAuth.getInstance().signOut() },
            ) {
                Text(text = "Sign Out", color = Color.White)
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
                IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Select Image"
                    )
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
                                postContent = postContent.removeSuffix(fileName)
                                userPostRepository.savePost(postContent, selectedImageUri.toString()) { _, _ -> }
                                postContent = ""
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
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
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
    val currentUser = userRepository.currentUser.collectAsState().value

    var user by remember { mutableStateOf(ApplicationUser()) }
    var posts by remember { mutableStateOf<List<UserPost>>(emptyList()) }

    print(userId)

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
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        UserProfileUI(user)

        if (posts.isNotEmpty()) {
            Text(
                text = "${user.name}'s posts",
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