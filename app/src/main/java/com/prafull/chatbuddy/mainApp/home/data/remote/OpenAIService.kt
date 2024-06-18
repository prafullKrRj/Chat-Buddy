package com.prafull.chatbuddy.mainApp.home.data.remote

import com.prafull.chatbuddy.mainApp.home.model.OpenAiRequest
import com.prafull.chatbuddy.mainApp.home.model.OpenAiResponse
import com.prafull.chatbuddy.mainApp.home.model.OpenAiUsage
import com.prafull.chatbuddy.mainApp.home.model.ResponseChoice

object OpenAIService {


    suspend fun getResponse(openAiRequest: OpenAiRequest): OpenAiResponse {
        return OpenAiResponse(
                choices = listOf(
                        ResponseChoice(
                                finishReason = "",
                                index = 0,
                                logprobs = Any(),
                                openAiMessageResp = com.prafull.chatbuddy.mainApp.home.model.OpenAiMessageResp(
                                        content = "Hello how can I help you?",
                                        role = "assistant"
                                )
                        )
                ),
                created = 0,
                id = "",
                model = "",
                responseObject = "",
                usage = OpenAiUsage(
                        completionTokens = 0,
                        promptTokens = 0,
                        totalTokens = 0
                )
        )
    }
}