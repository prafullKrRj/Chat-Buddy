package com.prafull.chatbuddy.homeScreen.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
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
fun BotImage(modifier: Modifier) {
    Image(
            modifier = modifier,
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Bot Image",
            contentScale = ContentScale.FillWidth
    )
}