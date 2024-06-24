package com.prafull.chatbuddy.mainApp.home.models

import android.graphics.Bitmap
import com.prafull.chatbuddy.mainApp.common.model.HistoryItem
import com.prafull.chatbuddy.mainApp.common.model.HistoryMessage
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.ModelSafety
import com.prafull.chatbuddy.utils.Const
import java.util.UUID

data class ChatHistoryNormal(
    val id: String = UUID.randomUUID().toString(),
    var messages: MutableList<NormalHistoryMsg> = mutableListOf(),
    val system: String = Const.GENERAL_SYSTEM_PROMPT,
    val safetySettings: String = ModelSafety.UNINTERRUPTED.name,
    val temperature: Double = 0.7,
    val promptType: String = Const.NORMAL_HISTORY,
    val model: String
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

data class NormalHistoryMsg(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val imageBase64: List<String> = emptyList(),
    val imageBitmaps: List<Bitmap?> = emptyList(),
    val participant: String = "",
    val model: String = "",
    var botImage: String = ""
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