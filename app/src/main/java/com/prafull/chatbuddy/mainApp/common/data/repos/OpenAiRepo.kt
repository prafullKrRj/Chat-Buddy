package com.prafull.chatbuddy.mainApp.common.data.repos

import com.prafull.chatbuddy.mainApp.common.data.remote.OpenAIService
import com.prafull.chatbuddy.mainApp.common.model.HistoryItem
import com.prafull.chatbuddy.mainApp.common.model.HistoryMessage
import com.prafull.chatbuddy.mainApp.common.model.OpenAiRequest
import com.prafull.chatbuddy.mainApp.common.model.Participant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OpenAiRepo : HomeChatAbstract() {
    override fun getResponse(
        history: HistoryItem,
        prompt: HistoryMessage
    ): Flow<HistoryMessage> {
        return callbackFlow {
            try {
                val request = OpenAiRequest(
                        model = history.model,
                        maxTokens = 1024,
                        messages = history.messages.map {
                            it.toOpenAi()
                        } + prompt.toOpenAi()
                )
                val response = OpenAIService.getResponse(request)

                response.choices.first().openAiMessageResp.let {
                    val responseModel = HistoryMessage(
                            text = it.content,
                            participant = Participant.ASSISTANT.name,
                            model = history.model,
                            botImage = prompt.botImage
                    )
                    trySend(responseModel)
                }
            } catch (e: Exception) {
                val responseModel = HistoryMessage(
                        text = e.localizedMessage ?: "Error",
                        model = history.model,
                        participant = Participant.ASSISTANT.name,
                        botImage = prompt.botImage
                )
                trySend(responseModel)
            }
            awaitClose { }
        }
    }
}