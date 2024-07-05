package com.prafull.chatbuddy.mainApp.common.model

import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.ModelSafety
import com.prafull.chatbuddy.utils.Const


data class Model(
    val generalName: String = Const.CHAT_BUDDY,
    val actualName: String = "gemini-1.5-flash",
    val currPricePerToken: Double = 0.0,
    val image: String = "",
    val hasVision: Boolean = false,
    val hasFiles: Boolean = false,
    val modelGroup: String = "",
    val taskType: String = "",
    val temperature: Double = 0.8,
    val system: String = Const.GENERAL_SYSTEM_PROMPT,
    val safetySetting: String = ModelSafety.UNSPECIFIED.name
) {
    fun toChatScreen(id: String = "") = Routes.ModelChatScreen(
            id = id,
            generalName,
            actualName,
            currPricePerToken.toString(),
            image,
            hasVision,
            hasFiles,
            modelGroup,
            taskType,
            temperature.toFloat(),
            system,
            safetySetting
    )
}