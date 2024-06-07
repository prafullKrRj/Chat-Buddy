package com.prafull.chatbuddy.utils

sealed interface Response<out T> {
    data object Initial : Response<Nothing>

    data class Success<out T>(val data: T) : Response<T>
    data class Error(val exception: Exception) : Response<Nothing>
}