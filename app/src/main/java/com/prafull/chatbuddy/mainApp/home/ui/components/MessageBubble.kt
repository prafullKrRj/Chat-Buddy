package com.prafull.chatbuddy.mainApp.home.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.ChatViewModelAbstraction
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.Participant
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.model.isGptModel
import com.prafull.chatbuddy.mainApp.ui.BotImage
import com.prafull.chatbuddy.mainApp.ui.UserImage
import com.prafull.chatbuddy.utils.UriSaver
import com.prafull.chatbuddy.utils.toBitmaps
import kotlinx.coroutines.launch

@Composable
fun MessageBubble(
    message: ChatMessage,
    mA: FirebaseAuth,
    clipboardManager: ClipboardManager,
    context: Context,
    isSecondLast: Boolean,
    isLast: Boolean,
    viewModel: ChatViewModelAbstraction
) {
    var isEditingPrompt by rememberSaveable { mutableStateOf(false) }
    var editingPrompt by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
            imageUri?.let { imageUris.add(it) }
        }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(imageUris.size) {
        if (imageUris.isNotEmpty()) listState.animateScrollToItem(imageUris.size)
    }

    Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
    ) {
        if (message.participant == Participant.USER) {
            UserMessageBubble(
                    message = message,
                    mA = mA,
                    isEditingPrompt = isEditingPrompt,
                    editingPrompt = editingPrompt,
                    imageUris = imageUris,
                    listState = listState,
                    pickMedia = pickMedia,
                    onEditPromptChange = { editingPrompt = it },
                    onSendClick = {
                        scope.launch {
                            val bitmaps = imageUris.mapNotNull { it.toBitmaps(context) }
                            viewModel.updateLastPrompt(bitmaps, editingPrompt)
                            imageUris.clear()
                            editingPrompt = ""
                            isEditingPrompt = false
                            focusManager.clearFocus()
                        }
                    },
                    onPickImageClick = {
                        pickMedia.launch(
                                PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                        )
                    },
                    onEditPromptToggle = {
                        editingPrompt = message.text
                        isEditingPrompt = true
                    },
                    context = context,
                    clipboardManager = clipboardManager,
                    isSecondLast = isSecondLast,
                    isLast = isLast
            )
        } else {
            BotMessageBubble(
                    message = message,
                    viewModel = viewModel,
                    context = context,
                    clipboardManager = clipboardManager,
                    isSecondLast = isSecondLast,
                    isLast = isLast
            )
        }
    }
}

@Composable
fun UserMessageBubble(
    message: ChatMessage,
    mA: FirebaseAuth,
    isEditingPrompt: Boolean,
    editingPrompt: String,
    imageUris: MutableList<Uri>,
    listState: LazyListState,
    pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    onEditPromptChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onPickImageClick: () -> Unit,
    onEditPromptToggle: () -> Unit,
    context: Context,
    clipboardManager: ClipboardManager,
    isSecondLast: Boolean,
    isLast: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
                        items(message.imageBitmaps) { image ->
                            image?.let { PromptedImages(imageUri = it) }
                        }
                    }
                    FormattedText(
                            text = message.text,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                    )
                    MessageFunctions(
                            message = message,
                            context = context,
                            clipboardManager = clipboardManager,
                            isSecondLast = isSecondLast,
                            isLast = isLast,
                            editPrompt = onEditPromptToggle
                    )
                } else {
                    ImagePickerRow(imageUris, pickMedia, listState)
                    OutlinedTextField(
                            value = editingPrompt,
                            onValueChange = onEditPromptChange,
                            modifier = Modifier.padding(8.dp),
                            trailingIcon = {
                                IconButton(onClick = onSendClick) {
                                    Icon(
                                            imageVector = Icons.AutoMirrored.Default.Send,
                                            contentDescription = "Send"
                                    )
                                }
                            },
                            leadingIcon = {
                                IconButton(onClick = onPickImageClick) {
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
}

@Composable
fun BotMessageBubble(
    message: ChatMessage,
    viewModel: ChatViewModelAbstraction,
    context: Context,
    clipboardManager: ClipboardManager,
    isSecondLast: Boolean,
    isLast: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Log.d("BotMessageBubble", "BotMessageBubble: ${message.model}")
        if (viewModel.currModel.modelGroup == "Characters") {
            BotImage(modifier = Modifier.size(24.dp), data = viewModel.currModel.image)
        } else if (message.model.isGptModel()) {
            BotImage(modifier = Modifier.size(24.dp), image = R.drawable.gpt)
        } else if (message.model.isGeminiModel()) {
            BotImage(modifier = Modifier.size(24.dp), image = R.drawable.gemini)
        } else if (message.model.isClaudeModel()) {
            BotImage(modifier = Modifier.size(24.dp), image = R.drawable.claude)
        } else {
            BotImage(Modifier.size(24.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = message.model)
            FormattedText(text = message.text)
            MessageFunctions(
                    message = message,
                    context = context,
                    clipboardManager = clipboardManager,
                    isSecondLast = isSecondLast,
                    isLast = isLast,
                    regenerateOutput = {
                        viewModel.regenerateResponse()
                    }
            )
        }
    }
}

@Composable
fun MessageFunctions(
    message: ChatMessage,
    context: Context,
    clipboardManager: ClipboardManager,
    isSecondLast: Boolean,
    isLast: Boolean,
    editPrompt: () -> Unit = {},
    regenerateOutput: () -> Unit = {}
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
    ) {
        if (isSecondLast && message.participant == Participant.USER) {
            IconButton(onClick = editPrompt) {
                Icon(
                        painter = painterResource(id = R.drawable.outline_edit_24),
                        contentDescription = "Re Prompt"
                )
            }
        }
        if (isLast && message.participant == Participant.ASSISTANT) {
            IconButton(onClick = regenerateOutput) {
                Icon(
                        painter = painterResource(id = R.drawable.outline_autorenew_24),
                        contentDescription = "Regenerate"
                )
            }
        }
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
                .clickable { }
    )
}
