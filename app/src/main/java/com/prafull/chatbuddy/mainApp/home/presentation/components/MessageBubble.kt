package com.prafull.chatbuddy.mainApp.home.presentation.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.common.components.BotImage
import com.prafull.chatbuddy.mainApp.common.components.FormattedText
import com.prafull.chatbuddy.mainApp.common.components.UserImage
import com.prafull.chatbuddy.mainApp.common.model.Participant
import com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen.PromptedImages
import com.prafull.chatbuddy.utils.UriSaver

@Composable
fun MessageBubble(
    modifier: Modifier,
    participant: String,
    message: Pair<String, List<Bitmap?>>,
    botImage1: String?,
    @DrawableRes botImage2: Int?,
    mA: FirebaseAuth,
    isLast: Boolean,
    isSecondLast: Boolean,
    clipboardManager: ClipboardManager,
    model: String,
    regenerateOutput: () -> Unit
) {
    var isEditingPrompt by rememberSaveable { mutableStateOf(false) }
    var editingPrompt by rememberSaveable { mutableStateOf("") }
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
            imageUri?.let { imageUris.add(it) }
        }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    LaunchedEffect(imageUris.size) {
        if (imageUris.isNotEmpty()) listState.animateScrollToItem(imageUris.size)
    }
    if (participant == Participant.USER.name) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            UserImage(Modifier.size(24.dp), firebaseAuth = mA)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = mA.currentUser?.displayName ?: "User")
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                ) {
                    if (!isEditingPrompt) {
                        LazyRow(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .background(Color.Transparent)
                        ) {
                            items(message.second) { image ->
                                if (image != null) {
                                    PromptedImages(imageUri = image)
                                }
                            }
                        }
                        FormattedText(
                                text = message.first,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(top = 8.dp)
                        )
                        HomeChatMessageFunctions(
                                message = message,
                                participant = participant,
                                context = context,
                                clipboardManager = clipboardManager,
                                isSecondLast = isSecondLast,
                                isLast = isLast,
                                editPrompt = {
                                    isEditingPrompt = true
                                }
                        )
                    } else {
                        ImagePickerRow(imageUris, pickMedia, listState)
                        OutlinedTextField(
                                value = editingPrompt,
                                onValueChange = {
                                    editingPrompt = it
                                },
                                modifier = Modifier.padding(8.dp),
                                trailingIcon = {
                                    IconButton(onClick = {

                                    }) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Default.Send,
                                                contentDescription = "Send"
                                        )
                                    }
                                },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        pickMedia.launch(
                                                PickVisualMediaRequest(
                                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                        )
                                    }) {
                                        Icon(
                                                painter = painterResource(id = R.drawable.baseline_image_24),
                                                contentDescription = "select image"
                                        )
                                    }
                                }
                        )
                    }
                }
            }
        }
    } else {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (botImage2 == null) {
                BotImage(modifier = Modifier.size(24.dp), data = botImage1)
            } else {
                BotImage(modifier = Modifier.size(24.dp), image = botImage2)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = model)
                FormattedText(text = message.first)
                HomeChatMessageFunctions(
                        message = message,
                        participant = participant,
                        context = context,
                        clipboardManager = clipboardManager,
                        isSecondLast = isSecondLast,
                        isLast = isLast,
                        regenerateOutput = regenerateOutput
                )
            }
        }
    }
}