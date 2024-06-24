package com.prafull.chatbuddy.mainApp.home.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SelectedImage(imageUri: Uri, removeImage: (Uri) -> Unit) {
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