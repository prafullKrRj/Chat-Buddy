package com.prafull.chatbuddy.utils

sealed interface Resource<out T> {
    data object Initial : Resource<Nothing>

    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val exception: Exception) : Resource<Nothing>
}