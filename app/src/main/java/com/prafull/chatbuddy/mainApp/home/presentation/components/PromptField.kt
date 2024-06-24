package com.prafull.chatbuddy.mainApp.home.presentation.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.mainApp.common.model.Participant
import com.prafull.chatbuddy.utils.UriSaver
import com.prafull.chatbuddy.utils.toBitmaps
import kotlinx.coroutines.launch

@Composable
fun PromptField(
    modifier: Modifier = Modifier,
    onSend: (String, List<Bitmap?>, String) -> Unit,
    loading: Boolean = false,
) {
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
    var prompt by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(imageUris.size) {
        if (imageUris.isNotEmpty()) listState.animateScrollToItem(imageUris.size)
    }
    ElevatedCard(
            modifier = modifier,
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
        MessageInputRow(
                modifier = Modifier,
                prompt = prompt,
                onPromptChange = {
                    prompt = it
                },
                onSend = {
                    scope.launch {
                        val images = imageUris.mapNotNull { it.toBitmaps(context) }
                        onSend(prompt, images, Participant.USER.name)
                        focusManager.clearFocus()
                        prompt = ""
                        imageUris.removeAll(elements = imageUris)
                    }
                },
                onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                loading
        )
    }
}