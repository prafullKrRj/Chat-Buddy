package com.prafull.chatbuddy.mainApp.home.presentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.R

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
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(40),
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
                                strokeWidth = 2.dp,
                                modifier = Modifier.padding(4.dp)
                        )
                    }
                }
        )
    }
}