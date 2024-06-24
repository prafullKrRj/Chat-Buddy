package com.prafull.chatbuddy.mainApp.common.data.repos

import com.prafull.chatbuddy.mainApp.common.data.remote.ClaudeApiService
import com.prafull.chatbuddy.mainApp.common.model.ClaudeRequest
import com.prafull.chatbuddy.mainApp.common.model.HistoryItem
import com.prafull.chatbuddy.mainApp.common.model.HistoryMessage
import com.prafull.chatbuddy.mainApp.common.model.Participant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.inject

class ClaudeRepo : HomeChatAbstract() {

    private val claudeApiService by inject<ClaudeApiService>()
    override fun getResponse(
        history: HistoryItem,
        prompt: HistoryMessage
    ): Flow<HistoryMessage> {
        return callbackFlow {
            try {
                val request = ClaudeRequest(
                        model = history.model,
                        max_tokens = 350,
                        system = history.system,
                        temperature = history.temperature.toFloat(),
                        messages = history.messages.map {
                            it.toClaudeMessage()
                        } + prompt.toClaudeMessage()
                )
                val response = claudeApiService.postMessage(request = request)
                response.content.firstOrNull()?.text?.let {
                    val responseModel = HistoryMessage(
                            text = it,
                            participant = Participant.ASSISTANT.name,
                            model = prompt.model,
                            botImage = prompt.botImage
                    )
                    trySend(
                            responseModel
                    )
                }
            } catch (e: Exception) {
                val responseModel = HistoryMessage(
                        text = e.localizedMessage ?: "Error occurred. Please try again.",
                        participant = Participant.ERROR.name,
                        model = prompt.model,
                        botImage = prompt.botImage
                )
                trySend(
                        responseModel
                )
            }
            awaitClose { }
        }
    }
}