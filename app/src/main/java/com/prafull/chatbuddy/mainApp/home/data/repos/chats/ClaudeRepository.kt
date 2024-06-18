package com.prafull.chatbuddy.mainApp.home.data.repos.chats

import android.util.Log
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.Participant
import com.robbiebowman.claude.ClaudeClientBuilder
import com.robbiebowman.claude.MessageContent
import com.robbiebowman.claude.Role
import com.robbiebowman.claude.SerializableMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class ClaudeRepository : ChatRepository() {

    override suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage> {
        return callbackFlow {
            withContext(Dispatchers.IO) {
                try {
                    val client = ClaudeClientBuilder().withApiKey(BuildConfig.CLAUDE_API_KEY)
                        .withSystemPrompt(history.systemPrompt)
                        .withModel(history.model).build()

                    val messages = history.toSerializedMessages() + SerializableMessage(
                            role = Role.User,
                            content = prompt.toClaudeContent()
                    )
                    Log.d("ClaudeRepository", "Messages: $messages")
                    val response = client.getChatCompletion(messages)

                    when (val x = response.content.first()) {
                        is MessageContent.TextContent -> {
                            trySend(
                                    ChatMessage(
                                            text = x.text,
                                            participant = Participant.ASSISTANT
                                    )
                            )
                        }

                        is MessageContent.ImageContent -> {
                            trySend(
                                    ChatMessage(
                                            text = "error",
                                            participant = Participant.ASSISTANT
                                    )
                            )
                        }

                        is MessageContent.ToolResult -> trySend(
                                ChatMessage(
                                        text = "error",
                                        participant = Participant.ASSISTANT
                                )
                        )

                        is MessageContent.ToolUse -> trySend(
                                ChatMessage(
                                        text = "error",
                                        participant = Participant.ASSISTANT
                                )
                        )
                    }
                } catch (httpException: retrofit2.HttpException) {
                    trySend(
                            ChatMessage(
                                    text = httpException.localizedMessage ?: "Error",
                                    participant = Participant.ERROR
                            )
                    )
                } catch (e: Exception) {
                    trySend(
                            ChatMessage(
                                    text = e.localizedMessage ?: "Error",
                                    participant = Participant.ERROR
                            )
                    )
                }
            }
            awaitClose { }
        }
    }
}

fun ChatHistory.toSerializedMessages(): List<SerializableMessage> {
    return messages.map {
        SerializableMessage(
                role = if (it.participant == Participant.USER) Role.User else Role.Assistant,
                content = it.toClaudeContent()
        )
    }
}