package com.example.fin.ui.posts

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fin.R
import com.example.fin.model.UserPost
import com.example.fin.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPostUI(userPost: UserPost, onClick: () -> Unit) {
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
        elevation =  CardDefaults.cardElevation(
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
                    text = userPost.authorName,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
        }
    }
}