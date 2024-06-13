package com.prafull.chatbuddy.model

data class Model(
    val generalName: String = "",
    val actualName: String = "",
    val currPricePerToken: Long = 0,
    val image: String = "",
    val hasVision: Boolean = false,
    val hasFiles: Boolean = false,
    val modelGroup: String = "",
    val taskType: String = ""
)