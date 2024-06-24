package com.prafull.chatbuddy.mainApp.common.model

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.prafull.chatbuddy.mainApp.home.models.ChatHistoryNormal
import com.prafull.chatbuddy.mainApp.home.models.NormalHistoryMsg
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsMessage
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryMessage
import com.prafull.chatbuddy.utils.toBase64
import java.util.UUID

data class HistoryItem(
    val id: String,
    val system: String,
    val messages: MutableList<HistoryMessage>,
    val temperature: Double,
    val safetySettings: String,
    val promptType: String,
    val model: String
) {
    fun toChatHistoryNormal(): ChatHistoryNormal {
        return ChatHistoryNormal(
                id = id,
                messages = messages.map { it.toNormalHistoryMsg() }.toMutableList(),
                system = system,
                safetySettings = safetySettings,
                temperature = temperature,
                promptType = promptType,
                model = model
        )
    }

    fun toModelsHistory() = ModelsHistory(
            id = id,
            system = system,
            messages = messages.map { it.toModelsHisMsg() }.toMutableList(),
            temperature = temperature,
            safetySettings = safetySettings,
            promptType = promptType,
            model = model
    )

}

data class HistoryMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val participant: String,
    val model: String,
    var botImage: String,
    val imageBase64: List<String> = emptyList(),
    val imageBitmaps: List<Bitmap?> = emptyList()
) {
    fun geminiContent(): Content {
        return content {
            for (image in imageBitmaps) {
                if (image != null) {
                    image(image)
                }
            }
            text(text)
        }
    }

    fun toClaudeMessage(): ClaudeMessage {
        return ClaudeMessage(
                role = participant.lowercase(),
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

    fun toOpenAi(): OpenAiMessageInp {
        return OpenAiMessageInp(
                role = participant.lowercase(),
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

    fun toNormalHistoryMsg(): NormalHistoryMsg {
        return NormalHistoryMsg(
                id = id,
                text = text,
                imageBase64 = imageBase64,
                imageBitmaps = imageBitmaps,
                participant = participant,
                model = model,
                botImage = botImage
        )
    }

    fun toPromptLibMsg(): PromptLibraryMessage {
        return PromptLibraryMessage(
                id = id,
                text = text,
                imageBase64 = imageBase64,
                imageBitmaps = imageBitmaps,
                participant = participant,
                model = model,
                botImage = botImage
        )
    }

    fun toModelsHisMsg() = ModelsMessage(
            id = id,
            text = text,
            participant = participant,
            model = model,
            botImage = botImage,
            imageBase64 = imageBase64,
            imageBitmaps = imageBitmaps
    )
}