package com.prafull.chatbuddy.mainApp.promptlibrary.model

import com.prafull.chatbuddy.Routes

data class PromptLibraryItem(
    val name: String = "",
    val description: String = "",
    val system: String = "",
    val user: String = ""
) {
    fun isEmpty(): Boolean {
        return name.isEmpty() && description.isEmpty() && user.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return name.isNotEmpty() || description.isNotEmpty() || user.isNotEmpty()
    }

    fun toHomeArgs() = Routes.HomeWithArgs(name, description, system, user)
}