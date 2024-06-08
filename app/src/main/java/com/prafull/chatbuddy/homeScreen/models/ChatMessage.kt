package com.prafull.chatbuddy.homeScreen.models

import android.graphics.Bitmap
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var imageUri: MutableList<Bitmap> = mutableListOf(),
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false
)

enum class Participant {
    USER, MODEL, ERROR
}