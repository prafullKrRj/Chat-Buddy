package com.prafull.chatbuddy.mainApp.home.model

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.prafull.chatbuddy.utils.toBase64
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var imageBitmaps: MutableList<Bitmap> = mutableListOf(),
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false
) {
    fun toGeminiContent(): Content {
        return content {
            for (image in imageBitmaps) {
                image(image)
            }
            text(text)
        }
    }

    fun toClaudeContent(): ClaudeMessage {
        return ClaudeMessage(
                role = if (participant == Participant.USER) "user" else "assistant",
                claudeMessageContent = listOf(
                        ClaudeMessageContent(
                                type = "text",
                                text = text
                        )
                ) + imageBitmaps.map {
                    ClaudeMessageContent(
                            type = "image",
                            claudeMessageContentSource = ClaudeMessageContentSource(
                                    type = "base64",
                                    media_type = "image/jpeg",
                                    data = it.toBase64() ?: ""
                            )
                    )
                }
        )
    }
}

enum class Participant {
    USER, ASSISTANT, ERROR
}