package com.prafull.chatbuddy.mainApp.modelsScreen.model

import android.graphics.Bitmap
import com.prafull.chatbuddy.mainApp.common.model.HistoryItem
import com.prafull.chatbuddy.mainApp.common.model.HistoryMessage
import com.prafull.chatbuddy.utils.Const
import java.util.UUID

data class ModelsHistory(
    val id: String = UUID.randomUUID().toString(),
    val system: String = Const.GENERAL_SYSTEM_PROMPT,
    val messages: MutableList<ModelsMessage> = mutableListOf(),
    val temperature: Double = 1.0,
    val safetySettings: String = "",
    val promptType: String = Const.MODELS_HISTORY,
    val model: String = "",
) {
    fun toHistoryItem() = HistoryItem(
            id = id,
            messages = messages.map { it.toHistoryMessage() }.toMutableList(),
            system = system,
            safetySettings = safetySettings,
            temperature = temperature,
            promptType = promptType,
            model = model
    )
}

data class ModelsMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    val participant: String = "",
    val model: String = "",
    var botImage: String = "",
    val imageBase64: List<String> = emptyList(),
    val imageBitmaps: List<Bitmap?> = emptyList()
) {
    fun toHistoryMessage() = HistoryMessage(
            id = id,
            text = text,
            participant = participant,
            model = model,
            botImage = botImage,
            imageBase64 = imageBase64,
            imageBitmaps = imageBitmaps
    )
}