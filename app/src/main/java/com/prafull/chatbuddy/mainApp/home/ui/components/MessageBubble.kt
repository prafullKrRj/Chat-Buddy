package com.prafull.chatbuddy.mainApp.home.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.Participant
import com.prafull.chatbuddy.mainApp.ui.BotImage
import com.prafull.chatbuddy.mainApp.ui.UserImage

@Composable
fun MessageBubble(
    message: ChatMessage,
    mA: FirebaseAuth,
    clipboardManager: ClipboardManager,
    context: Context = LocalContext.current
) {
    Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
    ) {
        if (message.participant == Participant.USER) {
            Row {
                UserImage(Modifier.size(24.dp), firebaseAuth = mA)
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Text(text = mA.currentUser?.displayName ?: "User")
                    Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp)
                    ) {
                        LazyRow(
                                modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            items(message.imageBitmaps) { image ->
                                PromptedImages(imageUri = image)
                            }
                        }
                        FormattedText(
                                text = message.text,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(top = 8.dp)
                        )
                        CopyAndShare(
                                message = message,
                                context = context,
                                clipboardManager = clipboardManager
                        )
                    }
                }
            }
        } else {
            Row {
                BotImage(Modifier.size(24.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Text(text = stringResource(id = R.string.app_name))
                    FormattedText(text = message.text)
                    CopyAndShare(
                            message = message,
                            context = context,
                            clipboardManager = clipboardManager
                    )
                }
            }
        }
    }
}

@Composable
fun CopyAndShare(
    message: ChatMessage,
    context: Context,
    clipboardManager: ClipboardManager
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = {
            clipboardManager.setText(AnnotatedString(message.text))
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }) {
            Icon(
                    painter = painterResource(id = R.drawable.baseline_content_copy_24),
                    contentDescription = "Copy",
                    Modifier.size(18.dp)
            )
        }
        IconButton(onClick = {
            shareText(context = context, text = message.text)
        }) {
            Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    Modifier.size(18.dp)
            )
        }
    }
}

fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

@Composable
private fun PromptedImages(imageUri: Bitmap) {
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