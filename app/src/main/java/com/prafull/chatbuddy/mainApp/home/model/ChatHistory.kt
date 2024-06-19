package com.prafull.chatbuddy.mainApp.home.model

import com.google.firebase.Timestamp
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelSafety
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.model.Model
import com.prafull.chatbuddy.utils.Const
import java.util.UUID

data class ChatHistory(
    var id: String = UUID.randomUUID().toString(),
    var messages: MutableList<ChatMessage> = mutableListOf(),
    var model: String = "gemini-1.5-flash-latest",
    var modelGeneralName: String = "Chat Buddy",
    var botImage: String = "",
    var lastModified: Timestamp = Timestamp.now(),
    var systemPrompt: String = Const.GENERAL_SYSTEM_PROMPT,
    var promptName: String = "",
    var promptDescription: String = "",
    var temperature: Double = 0.7,
    var safetySetting: String = ModelSafety.UNSPECIFIED.name,
) {
    fun toPromptLibraryItem() = PromptLibraryItem(promptName, promptDescription, systemPrompt, "")
    fun toModel() = Model(
            generalName = modelGeneralName,
            actualName = model,
            temperature = temperature,
            safetySetting = safetySetting,
            system = systemPrompt,
            image = botImage,

            )
}

fun String.isGeminiModel() = this.contains("gemini")
fun String.isClaudeModel() = this.contains("claude")
fun String.isGptModel() = this.contains("gpt")