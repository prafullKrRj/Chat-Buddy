package com.prafull.chatbuddy.mainApp.home.ui.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.ChatViewModelAbstraction
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.utils.UriSaver
import com.prafull.chatbuddy.utils.toBitmaps
import kotlinx.coroutines.launch


@Composable
fun PromptField(modifier: Modifier, viewModel: ChatViewModelAbstraction) {

    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let { imageUris.add(it) }
    }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(imageUris.size) {
        if (imageUris.isNotEmpty()) listState.animateScrollToItem(imageUris.size)
    }
    ElevatedCard(
            modifier = Modifier,
    ) {
        ImagePickerRow(imageUris, pickMedia, listState)
        MessageInputRow(
                modifier = modifier,
                prompt = viewModel.currPrompt.text,
                onPromptChange = {
                    viewModel.currPrompt = ChatMessage(text = it)
                },
                onSend = {
                    scope.launch {
                        val bitmaps = imageUris.mapNotNull { it.toBitmaps(context) }
                        viewModel.currPrompt = viewModel.currPrompt.copy(
                                imageBitmaps = bitmaps.toMutableList()
                        )
                        viewModel.sendMessage()
                        imageUris.clear()
                        focusManager.clearFocus()
                    }
                },
                onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                viewModel.loading
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
    modifier: Modifier,
    prompt: String,
    onPromptChange: (String) -> Unit,
    onSend: () -> Unit,
    onPickImage: () -> Unit,
    isLoading: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        IconButton(onClick = onPickImage) {
            Icon(
                    painter = painterResource(id = R.drawable.baseline_image_24),
                    contentDescription = "Pick Image",
                    Modifier.size(24.dp)
            )
        }
        OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                value = prompt,
                label = {
                    if (!isFocused && prompt.isBlank()) {
                        Text("Message")
                    }
                },
                interactionSource = interactionSource,
                onValueChange = { onPromptChange(it) },
                keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default
                ),
                trailingIcon = {
                    if (prompt.isNotBlank()) {
                        IconButton(
                                onClick = {
                                    onSend()
                                }
                        ) {
                            Icon(
                                    Icons.AutoMirrored.Default.Send,
                                    contentDescription = "send",
                                    modifier = Modifier
                            )
                        }
                    }
                    if (prompt.isBlank() && isLoading) {
                        CircularProgressIndicator(
                                strokeWidth = 4.dp,
                                modifier = Modifier.padding(4.dp)
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(textAlign = TextAlign.Start)
        )
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