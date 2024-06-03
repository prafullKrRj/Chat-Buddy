package com.prafull.chatbuddy.authScreen.models

data class User(
    val name: String = "",
    val email: String = "",
    val subscribed: Boolean = false,
    val currCoins: Long = 0
)
