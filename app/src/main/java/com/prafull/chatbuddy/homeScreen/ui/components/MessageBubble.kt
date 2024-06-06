package com.prafull.chatbuddy.homeScreen.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.homeScreen.models.ChatMessage
import com.prafull.chatbuddy.homeScreen.models.Participant

@Composable
fun MessageBubble(message: ChatMessage, mA: FirebaseAuth) {
    Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
    ) {
        if (message.participant == Participant.USER) {
            UserImage(Modifier.weight(.05f), firebaseAuth = mA)
        } else {
            BotImage(Modifier.weight(.05f))
        }
        Column(
                Modifier
                    .weight(.95f)
                    .padding(horizontal = 8.dp)
        ) {
            LazyRow(
                    modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items(message.imageUri) { imageUri ->
                    AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .requiredWidth(72.dp)
                                .clickable {

                                }
                    )
                }
            }
            Text(
                    text = message.text
            )
        }
    }
}