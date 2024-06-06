package com.prafull.chatbuddy.homeScreen.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.ads.BannerAd
import com.prafull.chatbuddy.homeScreen.ui.viewmodels.ChatViewModel
import com.prafull.chatbuddy.utils.UriSaver
import com.prafull.chatbuddy.utils.toBitmaps
import kotlinx.coroutines.launch


@Composable
fun PromptField(chatViewModel: ChatViewModel) {
    var prompt by rememberSaveable {
        mutableStateOf("")
    }
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia = rememberLauncherForActivityResult(      // Create a launcher for picking media
            ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let {
            imageUris.add(it)
        }
    }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Column {
        LazyRow(
                modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            items(imageUris) { imageUri ->
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
                            onClick = { imageUris.remove(imageUri) },
                            modifier = Modifier.align(TopEnd)
                    ) {
                        Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Remove image",
                                tint = Color.Red
                        )
                    }
                }
            }
        }
        OutlinedTextField(
                value = prompt,
                onValueChange = {
                    prompt = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                leadingIcon = {
                    IconButton(onClick = {
                        pickMedia.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)   // Launch the media picker
                        )
                    }) {
                        Icon(
                                painter = painterResource(id = R.drawable.baseline_image_24),
                                contentDescription = "Add Image"
                        )
                    }
                },
                trailingIcon = {
                    if (prompt.isNotEmpty()) {
                        IconButton(onClick = {
                            scope.launch {
                                val bitmaps = imageUris.mapNotNull {
                                    it.toBitmaps(context)
                                }
                                chatViewModel.sendMessage(prompt, bitmaps)
                                imageUris.clear()
                                focusManager.clearFocus()
                                prompt = ""
                            }

                        }) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                        }
                    }
                },
                shape = RoundedCornerShape(35),
                colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
        )
        BannerAd()
    }

}