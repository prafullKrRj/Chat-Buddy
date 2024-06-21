package com.prafull.chatbuddy.mainApp.promptlibrary.model

import android.graphics.Bitmap
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.mainApp.common.model.HistoryItem
import com.prafull.chatbuddy.mainApp.common.model.HistoryMessage
import com.prafull.chatbuddy.utils.Const
import java.util.UUID


data class PromptLibraryHistory(
    val id: String = UUID.randomUUID().toString(),
    val system: String = "",
    val messages: MutableList<PromptLibraryMessage> = mutableListOf(),
    val temperature: Double = 1.0,
    val safetySettings: String = "",
    val promptType: String = Const.LIBRARY_HISTORY,
    val model: String = "",

    val name: String = "",
    val description: String = "",
    val user: String = "",
) {
    fun toHistoryItem(): HistoryItem {
        return HistoryItem(
                id = id,
                messages = messages.map { it.toHistoryMessage() }.toMutableList(),
                system = system,
                safetySettings = safetySettings,
                temperature = temperature,
                promptType = promptType,
                model = model
        )
    }
}

data class PromptLibraryMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val participant: String = "",
    val model: String = "",
    var botImage: String = "",
    val imageBase64: List<String> = emptyList(),
    val imageBitmaps: List<Bitmap?> = emptyList()
) {
    fun toHistoryMessage(): HistoryMessage {
        return HistoryMessage(
                id = id,
                text = text,
                participant = participant,
                model = model,
                botImage = botImage,
                imageBase64 = imageBase64,
                imageBitmaps = imageBitmaps
        )
    }
}

data class PromptLibraryItem(
    val name: String = "",
    val description: String = "",
    val system: String = "",
    val user: String = ""
) {
    fun isEmpty(): Boolean {
        return name.isEmpty() && description.isEmpty() && user.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return name.isNotEmpty() || description.isNotEmpty() || user.isNotEmpty()
    }

    fun toHomeArgs() = Routes.HomeWithArgs(name, description, system, user)
    fun toPromptChatScreen() = Routes.PromptChatScreen(name, description, system, user)
}