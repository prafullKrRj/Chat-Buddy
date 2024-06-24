package com.prafull.chatbuddy.mainApp.home.presentation.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


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