package com.prafull.chatbuddy.mainApp.historyscreen.model

import com.prafull.chatbuddy.mainApp.home.models.ChatHistoryNormal
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryHistory


data class HistoryClass(
    val modelsHistory: MutableList<ModelsHistory> = mutableListOf(),
    val normalHistory: MutableList<ChatHistoryNormal> = mutableListOf(),
    val promptLibHistory: MutableList<PromptLibraryHistory> = mutableListOf()
)