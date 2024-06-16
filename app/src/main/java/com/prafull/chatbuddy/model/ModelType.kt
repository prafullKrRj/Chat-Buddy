package com.prafull.chatbuddy.model

import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.utils.Const


data class Model(
    val generalName: String = "",
    val actualName: String = "",
    val currPricePerToken: Double = 0.0,
    val image: String = "",
    val hasVision: Boolean = false,
    val hasFiles: Boolean = false,
    val modelGroup: String = "",
    val taskType: String = "",
    val temperature: Float = 0.7f,
    val systemPrompt: String = Const.GENERAL_SYSTEM_PROMPT
) {
    fun toChatScreen() = Routes.ChatScreen(
            generalName,
            actualName,
            currPricePerToken.toString(),
            image,
            hasVision,
            hasFiles,
            modelGroup,
            taskType
    )
}