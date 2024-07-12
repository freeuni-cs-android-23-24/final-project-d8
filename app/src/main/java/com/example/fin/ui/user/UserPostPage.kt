package com.example.fin.ui.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.fin.model.ApplicationUser
import com.example.fin.model.Reply
import com.example.fin.model.UserPost
import com.example.fin.repository.RepliesRepository
import com.example.fin.repository.UserDataRepository
import com.example.fin.repository.UserPostRepository
import com.example.fin.repository.UserRepository
import com.example.fin.ui.posts.UserPostUI
import com.example.fin.ui.replies.ReplyInput
import com.example.fin.ui.replies.ReplyItem

@Composable
fun UserPostPage(
    userPostId: String,
    navController: NavHostController,
    userPostRepository: UserPostRepository,
    userRepository: UserRepository,
    userDataRepository: UserDataRepository,
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
        var postDeleteEnabled = false;
        var profileUrl by remember { mutableStateOf<String>("") }
        if (currentUser != null) {
            postDeleteEnabled = currentUser.id == userPost.authorId || currentUser.moderator
        }
        userDataRepository.getUserProfileUrl(searchUser = ApplicationUser(id = userPost.authorId)) { result, _ ->
            if (result != null) {
                profileUrl = result
            }
        }
        UserPostUI(userPost,
            profileUrl = profileUrl,
            postDeleteEnabled,
            onClick = {
                navController.navigate("UserProfilePage/${userPost.authorId}")
            },
            onDeleteClick = {
                userPostRepository.disablePost(userPost.userPostId) { _, _ -> }
                navController.navigate("ApplicationScreen")
            }
        )

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
            var replyDeleteEnabled = false;
            if (currentUser != null) {
                replyDeleteEnabled = currentUser.id == reply.authorId || currentUser.moderator
            }
            if (reply.enabled) {

                var replyProfileUrl by remember { mutableStateOf("") }

                userDataRepository.getUserProfileUrl(searchUser = ApplicationUser(id = reply.authorId)) { result, _ ->
                    if (result != null) {
                        replyProfileUrl = result
                    }
                }
                ReplyItem(reply,
                    profileUrl = replyProfileUrl,
                    deleteEnabled = replyDeleteEnabled,
                    onClick = {
                        navController.navigate("UserProfilePage/${reply.authorId}")
                    },
                    onDeleteClick = {
                        repliesRepository.disableReply(replyId = reply.replyId) { _, _ -> }
                        repliesRepository.getRepliesByPostId(userPost.userPostId) { result, _ ->
                            if (result != null) {
                                replies = result
                            }
                        }
                    }
                )

            }
        }
    }
}
