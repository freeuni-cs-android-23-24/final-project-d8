package com.example.fin.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fin.R
import com.example.fin.model.ApplicationUser


@Composable
fun UserProfileUI(user: ApplicationUser) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
        )

        Column(
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = user.name,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 1.dp)
            )
            Text(
                text = user.email,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = Color.Blue,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

