package com.example.fin.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fin.R
import com.example.fin.model.ApplicationUser
import com.example.fin.repository.UserDataRepository


@Composable
fun UserProfileUI(
    user: ApplicationUser,
    currentUser: ApplicationUser?,
    userDataRepository: UserDataRepository
) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        if (user.profileUrl.isNotBlank()) {
            AsyncImage(
                model = user.profileUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
                placeholder = painterResource(id = R.drawable.profile),
                error = painterResource(id = R.drawable.profile),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                var editedUsername by remember { mutableStateOf(user.name) }
                var isEditingUsername by remember { mutableStateOf(false) }
                var successfullyEditedUsername by remember { mutableStateOf(false)
                }
                if (isEditingUsername) {
                    TextField(
                        value = editedUsername,
                        onValueChange = { editedUsername = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if (currentUser != null) {
                                userDataRepository.updateUsername(
                                    currentUser.id,
                                    editedUsername
                                ) { success, _ ->
                                    if (success) {
                                        isEditingUsername = false
                                        successfullyEditedUsername = true
                                    }
                                }

                            }
                            isEditingUsername = false

                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                } else {
                    Text(
                        text = user.name,
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f)
                    )
                    if (currentUser?.id == user.id) {
                        IconButton(
                            onClick = {
                                isEditingUsername = true
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            }

            Text(
                text = user.email,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = Color.Blue,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

