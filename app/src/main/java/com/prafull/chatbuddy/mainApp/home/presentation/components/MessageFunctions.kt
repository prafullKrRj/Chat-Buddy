package com.prafull.chatbuddy.mainApp.home.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.common.model.Participant
import com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen.shareText

@Composable
fun HomeChatMessageFunctions(
    message: Pair<String, List<Bitmap?>>,
    participant: String,
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
        if (isSecondLast && participant == Participant.USER.name) {
            IconButton(onClick = editPrompt) {
                Icon(
                        painter = painterResource(id = R.drawable.outline_edit_24),
                        contentDescription = "Re Prompt"
                )
            }
        }
        if (isLast && participant == Participant.ASSISTANT.name) {
            IconButton(onClick = regenerateOutput) {
                Icon(
                        painter = painterResource(id = R.drawable.outline_autorenew_24),
                        contentDescription = "Regenerate"
                )
            }
        }
        IconButton(onClick = {
            clipboardManager.setText(AnnotatedString(message.first))
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }) {
            Icon(
                    painter = painterResource(id = R.drawable.baseline_content_copy_24),
                    contentDescription = "Copy",
                    Modifier.size(18.dp)
            )
        }
        IconButton(onClick = {
            shareText(context = context, text = message.first)
        }) {
            Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    Modifier.size(18.dp)
            )
        }
    }
}