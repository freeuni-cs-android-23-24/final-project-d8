package com.example.fin.ui.replies

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fin.R
import com.example.fin.model.Reply
import com.example.fin.utils.DateUtils

@Composable
fun ReplyInput(onReply: (String) -> Unit) {
    var input by remember { mutableStateOf("") }
    Row(
        modifier = Modifier.padding(top = 16.dp, end = 4.dp, start = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            placeholder = { Text("Reply to the post...") },
            modifier = Modifier.weight(1f),
            value = input,
            onValueChange = { input = it }
        )

        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            content = {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            },
            onClick = {
                onReply(input)
                input = ""
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyItem(reply: Reply, deleteEnabled: Boolean, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(top = 10.dp),
        onClick = onClick,
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            var menuExtended by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = reply.authorName,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
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
                text = "Replied on " + DateUtils.getDateTime(reply.timestamp.toString()),
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = reply.text,
                style = TextStyle(fontSize = 14.sp),
                color = Color.DarkGray
            )

        }
    }
}