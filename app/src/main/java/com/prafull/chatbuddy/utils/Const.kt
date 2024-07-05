package com.prafull.chatbuddy.utils

import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting

object Const {


    const val GENERAL_SYSTEM_PROMPT =
        "The content should be concise and crisp to get the best results. Until specified for detail explanations."

    const val GPT = "GPT"
    const val CLAUDE = "Claude"
    const val GEMINI = "Gemini"
    const val OPENAI = "Open AI"
    const val CHAT_BUDDY = "Chat Buddy"
    const val NLP = "nlp"

    const val NORMAL_HISTORY = "normal_history"
    const val MODELS_HISTORY = "models_history"
    const val LIBRARY_HISTORY = "library_history"
    const val CHARACTER_HISTORY = "character_history"

    val SAFETY_SETTINGS_NORMAL = listOf(
            SafetySetting(
                    harmCategory = HarmCategory.SEXUALLY_EXPLICIT,
                    threshold = BlockThreshold.MEDIUM_AND_ABOVE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.HARASSMENT,
                    threshold = BlockThreshold.MEDIUM_AND_ABOVE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.HATE_SPEECH,
                    threshold = BlockThreshold.MEDIUM_AND_ABOVE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.DANGEROUS_CONTENT,
                    threshold = BlockThreshold.MEDIUM_AND_ABOVE
            )
    )
    val SAFETY_SETTINGS_UNINTERRUPTED = listOf(
            SafetySetting(
                    harmCategory = HarmCategory.SEXUALLY_EXPLICIT,
                    threshold = BlockThreshold.MEDIUM_AND_ABOVE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.HARASSMENT,
                    threshold = BlockThreshold.MEDIUM_AND_ABOVE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.HATE_SPEECH,
                    threshold = BlockThreshold.MEDIUM_AND_ABOVE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.DANGEROUS_CONTENT,
                    threshold = BlockThreshold.MEDIUM_AND_ABOVE
            ),
    )
    val SAFETY_SETTINGS_ROMANTIC = listOf(
            SafetySetting(
                    harmCategory = HarmCategory.SEXUALLY_EXPLICIT,
                    threshold = BlockThreshold.NONE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.HARASSMENT,
                    threshold = BlockThreshold.NONE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.HATE_SPEECH,
                    threshold = BlockThreshold.NONE
            ),
            SafetySetting(
                    harmCategory = HarmCategory.DANGEROUS_CONTENT,
                    threshold = BlockThreshold.NONE
            ),
    )
}