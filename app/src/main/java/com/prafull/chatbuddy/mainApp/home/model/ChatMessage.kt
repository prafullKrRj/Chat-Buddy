package com.prafull.chatbuddy.mainApp.home.model

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
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
}

enum class Participant {
    USER, MODEL, ERROR
}