package com.prafull.chatbuddy.mainApp.models

data class PromptLibraryItem(
    val name: String = "",
    val description: String = "",
    val system: String = "",
    val user: String = ""
) {
    fun isEmpty(): Boolean {
        return name.isEmpty() && description.isEmpty() && system.isEmpty() && user.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return name.isNotEmpty() || description.isNotEmpty() || system.isNotEmpty() || user.isNotEmpty()
    }
}