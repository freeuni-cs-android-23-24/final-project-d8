package com.example.fin.ui.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fin.model.ApplicationUser
import com.example.fin.model.UserPost
import com.example.fin.repository.UserDataRepository
import com.example.fin.repository.UserPostRepository
import com.example.fin.ui.posts.ConfirmPostDialog
import com.example.fin.ui.posts.UserPostUI

@Composable
fun SearchAndPostSection(
    navController: NavHostController,
    currentUser: ApplicationUser?,
    userPostRepository: UserPostRepository,
    userDataRepository: UserDataRepository
) {
    var userPosts by remember { mutableStateOf<List<UserPost>>(emptyList()) }
    var searched by remember { mutableStateOf(false) }

    val (postContent, setPostContent) = remember { mutableStateOf("") }
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (selectedImageUri, setSelectedImageUri) = remember { mutableStateOf<Uri?>(null) }
    val (fileName, setFileName) = remember { mutableStateOf("") }
    val (searchText, setSearchText) = remember { mutableStateOf("") }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            setSelectedImageUri(uri)
            uri?.let {
                setFileName("(Attached Image: ${it.lastPathSegment?.substringAfterLast("/")})")
                setPostContent("$postContent\n$fileName")
            }
        }

    Row(modifier = Modifier.padding(top = 8.dp)) {
        TextField(
            value = searchText,
            onValueChange = setSearchText,
            label = { Text("Search") },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                if (searchText.isEmpty()) {
                    userPostRepository.getAllPosts { result, _ ->
                        if (result != null) {
                            userPosts = result
                            searched = false
                        }
                    }
                } else {
                    userPostRepository.searchPosts(searchText) { result, _ ->
                        if (result != null) {
                            userPosts = result
                            searched = true
                        }
                    }
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }
    }

    Row(modifier = Modifier.padding(top = 8.dp)) {
        TextField(
            value = postContent,
            onValueChange = setPostContent,
            label = { Text("Create a post") },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                if (postContent.isNotBlank()) {
                    setShowDialog(true)
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Post")
        }
        if (currentUser != null) {
            IconButton(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                }
            )
            {
                Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Select Image")
            }
        }
    }

    if (showDialog) {
        if (currentUser != null) {
            ConfirmPostDialog(
                postContent = postContent,
                fileName = fileName,
                onConfirm = {
                    setPostContent(postContent.removeSuffix(fileName))
                    userPostRepository.savePost(postContent, selectedImageUri) { _, _ -> }
                    setPostContent("")
                    setShowDialog(false)
                },
                onDismiss = { setShowDialog(false) }
            )
        } else {
            AlertDialog(
                onDismissRequest = { setShowDialog(false) },
                title = { Text(text = "Sign In Required") },
                text = { Text(text = "You need to sign in to post") },
                confirmButton = {
                    Button(
                        onClick = {
                            setShowDialog(false)
                        }
                    ) {
                        Text("I Understand")
                    }
                }
            )
        }
    }

    if (!searched) {
        userPostRepository.getAllPosts { result, _ ->
            if (result != null) {
                userPosts = result
            }
        }
    }

    userPosts.forEach { post ->
        var profileUrl by remember { mutableStateOf("") }
        var deleteEnabled = false
        if (currentUser != null) {
            deleteEnabled = currentUser.id == post.authorId || currentUser.moderator
        }
        userDataRepository.getUserProfileUrl(searchUser = ApplicationUser(id = post.authorId)) { result, _ ->
            if (result != null) {
                profileUrl = result
            }
        }
        UserPostUI(
            userPost = post,
            profileUrl = profileUrl,
            deleteEnabled = deleteEnabled,
            onClick = {
                navController.navigate("UserPostPage/${post.userPostId}")
            },
            onDeleteClick = {
                userPostRepository.disablePost(post.userPostId) { _, _ ->
                    userPostRepository.getAllPosts { result, _ ->
                        if (result != null) {
                            userPosts = result
                        }
                    }
                }
            }
        )
    }
}
