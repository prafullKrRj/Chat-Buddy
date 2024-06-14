package com.prafull.chatbuddy.mainApp.home.ui.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.utils.UriSaver
import com.prafull.chatbuddy.utils.toBitmaps
import kotlinx.coroutines.launch

@Composable
fun PromptField(chatViewModel: ChatViewModel) {
    var prompt by rememberSaveable { mutableStateOf("") }

    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let { imageUris.add(it) }
    }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLoading by chatViewModel.loading.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(imageUris.size) {
        if (imageUris.isNotEmpty()) listState.animateScrollToItem(imageUris.size)
    }
    Column {
        ImagePickerRow(imageUris, pickMedia, listState)
        MessageInputRow(
                prompt,
                onPromptChange = { prompt = it },
                onSend = {
                    scope.launch {
                        val bitmaps = imageUris.mapNotNull { it.toBitmaps(context) }
                        chatViewModel.sendMessage(
                                ChatMessage(
                                        text = prompt,
                                        imageBitmaps = bitmaps.toMutableList()
                                )
                        )
                        imageUris.clear()
                        focusManager.clearFocus()
                        prompt = ""
                    }
                },
                onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                isLoading
        )
    }
}

@Composable
fun ImagePickerRow(
    imageUris: MutableList<Uri>,
    pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    listState: LazyListState
) {
    LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            state = listState
    ) {
        items(imageUris) { imageUri ->
            SelectedImage(imageUri = imageUri) { imageUris.remove(it) }
        }
        if (imageUris.isNotEmpty()) {
            item {
                FilledTonalIconButton(onClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Image")
                }
            }
        }
    }
}

@Composable
fun MessageInputRow(
    prompt: String,
    onPromptChange: (String) -> Unit,
    onSend: () -> Unit,
    onPickImage: () -> Unit,
    isLoading: Boolean
) {
    Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChange,
                modifier = Modifier
                    .weight(0.9f)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .padding(top = 2.dp),
                leadingIcon = {
                    IconButton(onClick = onPickImage) {
                        Icon(
                                painter = painterResource(id = R.drawable.baseline_image_24),
                                contentDescription = "Add Image"
                        )
                    }
                },
                trailingIcon = {
                    if (prompt.isNotEmpty()) {
                        IconButton(onClick = onSend) {
                            Icon(
                                    imageVector = Icons.AutoMirrored.Default.Send,
                                    contentDescription = "Send"
                            )
                        }
                    } else {
                        IconButton(onClick = { /* Handle voice input */ }) {
                            Icon(
                                    painter = painterResource(id = R.drawable.baseline_keyboard_voice_24),
                                    contentDescription = "Record Voice"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(35),
                colors = OutlinedTextFieldDefaults.promptFieldColors(),
                label = { Text("Type a message") }
        )
        if (isLoading) {
            CircularProgressIndicator(Modifier.weight(0.1f))
        }
    }
}

@Composable
private fun SelectedImage(imageUri: Uri, removeImage: (Uri) -> Unit) {
    Box(
            modifier = Modifier
                .padding(4.dp)
                .requiredSize(100.dp)
    ) {
        AsyncImage(
                model = imageUri,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.padding(4.dp)
        )
        IconButton(
                onClick = { removeImage(imageUri) },
                modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Remove image",
                    tint = Color.Red
            )
        }
    }
}

@Composable
fun OutlinedTextFieldDefaults.promptFieldColors(): TextFieldColors {
    return colors(
            focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
    )
}