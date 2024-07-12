package com.example.fin.ui.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fin.cloud.uploadImageToStorage
import com.example.fin.model.ApplicationUser
import com.example.fin.model.UserPost
import com.example.fin.repository.UserDataRepository
import com.example.fin.repository.UserPostRepository
import com.example.fin.repository.UserRepository
import com.example.fin.ui.posts.UserPostUI

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

        if (currentUser != null && currentUser.id == user.id) {
            val imagePickerLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    uri?.let {
                        uploadImageToStorage(uri) { result, _ ->
                            if (result != null) {
                                userDataRepository.updateProfileUrl(currentUser.id, result) { _, _ -> }
                            }
                        }

                    }
                }
            Button(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
            ) {
                Text(text = "Change Profile Photo", color = Color.White)
            }

        }

        if (posts.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(10.dp),
                text = "${user.name}'s posts",
            )
        }

        for (post in posts) {
            var deleteEnabled = false;
            if (currentUser != null) {
                deleteEnabled = currentUser.id == post.authorId || currentUser.moderator
            }
            var profileUrl by remember { mutableStateOf("") }
            userDataRepository.getUserProfileUrl(searchUser = ApplicationUser(id = post.authorId)) { result, _ ->
                if (result != null) {
                    profileUrl = result
                }
            }
            UserPostUI(post,
                profileUrl = profileUrl,
                deleteEnabled = deleteEnabled,
                onClick = {
                    navController.navigate("UserPostPage/${post.userPostId}")
                },
                onDeleteClick = {
                    userPostRepository.disablePost(post.userPostId) { _, _ -> }
                    userPostRepository.getPostsByUser(userId) { result, _ ->
                        if (result != null) {
                            posts = result
                        }
                    }
                }
            )
        }

    }
}