package com.prafull.chatbuddy.mainApp.common.model

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.prafull.chatbuddy.utils.toBase64
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var imageBitmaps: List<Bitmap?> = mutableListOf(),
    var imageUrls: List<String> = mutableListOf(),
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false,
    var model: String = "Chat Buddy"
) {

    fun toGeminiContent(): Content {
        return content {
            for (image in imageBitmaps) {
                if (image != null) {
                    image(image)
                }
            }
            text(text)
        }
    }

    fun toOpenAi(): OpenAiMessageInp {
        return OpenAiMessageInp(
                role = when (participant) {
                    Participant.USER -> "user"
                    Participant.ASSISTANT -> "assistant"
                    Participant.ERROR -> "error"
                    Participant.SYSTEM -> "system"
                },
                openAiMessageContent = listOf(
                        OpenAiMessageContent(
                                type = "text",
                                text = text
                        )
                ) + imageBitmaps.map {
                    OpenAiMessageContent(
                            type = "image_url",
                            imageUrl = it?.toBase64()
                    )
                }
        )
    }

    fun toClaudeMessages(): ClaudeMessage {
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
                                    data = it?.toBase64() ?: ""
                            )
                    )
                }
        )
    }

}

enum class Participant {
    USER, ASSISTANT, ERROR, SYSTEM
}