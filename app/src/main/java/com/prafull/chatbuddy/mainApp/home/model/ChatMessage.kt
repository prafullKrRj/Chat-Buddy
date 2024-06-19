package com.prafull.chatbuddy.mainApp.home.model

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.prafull.chatbuddy.utils.toBase64
import com.robbiebowman.claude.MessageContent
import com.robbiebowman.claude.ResolvedImageContent
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var imageBitmaps: List<Bitmap?> = mutableListOf(),
    var imageUrls: List<String> = mutableListOf(),
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false
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

    fun toClaudeContent(): List<MessageContent> {
        return listOf(
                MessageContent.TextContent(text)
        ) + imageBitmaps.map {
            MessageContent.ImageContent(
                    source = ResolvedImageContent(
                            data = it?.toBase64() ?: "",
                            mediaType = "image/jpeg"
                    )
            )
        }
    }
}

enum class Participant {
    USER, ASSISTANT, ERROR, SYSTEM
}