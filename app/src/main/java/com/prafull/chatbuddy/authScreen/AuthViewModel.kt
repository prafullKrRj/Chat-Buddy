package com.prafull.chatbuddy.authScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.authScreen.repo.AuthRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthViewModel(): ViewModel(), KoinComponent {
    private val repository: AuthRepository by inject()

    var loading by mutableStateOf(false)

    fun loginUser(name: String, email: String) {
        loading = true
        viewModelScope.launch {
            val resp = repository.createUser(name, email)
            loading = false
        }
    }
}