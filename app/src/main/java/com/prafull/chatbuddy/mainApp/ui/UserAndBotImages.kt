package com.prafull.chatbuddy.mainApp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.R

@Composable
fun UserImage(modifier: Modifier, firebaseAuth: FirebaseAuth) {
    firebaseAuth.currentUser?.photoUrl.let {
        AsyncImage(
                modifier = modifier.clip(CircleShape),
                model = it,
                contentDescription = "User Image",
        )
    }
}

@Composable
fun BotImage(modifier: Modifier, data: String? = null, image: Int = R.drawable.logo) {
    if (data != null) {
        AsyncImage(
                modifier = modifier.clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data)
                    .build(),
                contentDescription = "Bot Image",
        )
    } else {
        Image(
                modifier = modifier.clip(CircleShape),
                painter = painterResource(id = image),
                contentDescription = "Bot Image",
                contentScale = ContentScale.FillWidth,
        )
    }
}