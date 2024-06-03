package com.prafull.chatbuddy.homeScreen.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.R


@Composable
fun PromptField(imageUris: (Uri) -> Unit, send: (String) -> Unit) {
    var prompt by rememberSaveable {
        mutableStateOf("")
    }
    val pickMedia = rememberLauncherForActivityResult(      // Create a launcher for picking media
            ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let {
            imageUris(it)
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
                IconButton(onClick = {
                    send(prompt)
                    prompt = ""
                }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            },
            shape = RoundedCornerShape(35),
            colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray
            )
    )

}