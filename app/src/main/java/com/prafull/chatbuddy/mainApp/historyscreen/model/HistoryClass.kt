package com.prafull.chatbuddy.mainApp.historyscreen.model

import com.prafull.chatbuddy.mainApp.common.data.repos.UserHistory


data class HistoryClass(
    var history: List<UserHistory> = emptyList()
)