package com.example.fin.ui.posts


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fin.R
import com.example.fin.model.UserPost
import com.example.fin.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPostUI(
    userPost: UserPost,
    profileUrl: String?,
    deleteEnabled: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(10.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var menuExtended by remember { mutableStateOf(false) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!profileUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = profileUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(id = R.drawable.profile),
                            error = painterResource(id = R.drawable.profile),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    }

                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = userPost.authorName,
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (deleteEnabled) {
                    Row {
                        IconButton(onClick = { menuExtended = !menuExtended }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Details")
                        }
                        DropdownMenu(expanded = menuExtended, onDismissRequest = { menuExtended = false }) {
                            DropdownMenuItem({ Text(text = "Delete") }, onClick = onDeleteClick)
                        }
                    }
                }
            }

            Text(
                text = DateUtils.getDateTime(userPost.timestamp.toString()),
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = userPost.postBodyText,
                style = TextStyle(fontSize = 16.sp),
                color = Color.DarkGray
            )
            if (userPost.imageUrl != "") {
                AsyncImage(
                    model = userPost.imageUrl,
                    contentDescription = "User Post Image",
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

        }
    }
}

