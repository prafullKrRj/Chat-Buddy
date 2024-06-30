package com.prafull.chatbuddy.authScreen.models

import com.prafull.chatbuddy.mainApp.common.data.repos.UserHistory

data class User(
    val name: String = "",
    val email: String = "",
    val subscribed: Boolean = false,
    val currCoins: Long = 0,
    val history: List<UserHistory> = emptyList()
)
