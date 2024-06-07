package com.prafull.chatbuddy.homeScreen.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.homeScreen.data.HomeRepository
import com.prafull.chatbuddy.homeScreen.models.ChatHistory
import com.prafull.chatbuddy.utils.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val homeRepository by inject<HomeRepository>()
    private val _adButtonEnabled = MutableStateFlow(true)
    val adButtonEnabled = _adButtonEnabled.asStateFlow()

    private val _coins = MutableStateFlow(2000L)
    val coins = _coins.asStateFlow()

    private val _watchedAd = MutableStateFlow(false)
    val watchedAd = _watchedAd.asStateFlow()
    fun adWatched() {
        _watchedAd.update {
            true
        }
        if (watchedAd.value) addCoins()
    }

    private fun addCoins() {
        _coins.update {
            it + 2000
        }
        _watchedAd.update { false }
    }

    fun updateAdButtonState(enabled: Boolean) {
        _adButtonEnabled.update {
            enabled
        }
    }

    init {
        getPreviousChats()
    }

    private val _previousChats = MutableStateFlow<Response<List<ChatHistory>>>(Response.Initial)
    val previousChats = _previousChats.asStateFlow()
    fun getPreviousChats() {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.getPreviousChats().collect { resp ->
                when (resp) {
                    is Response.Success -> {
                        _previousChats.update { Response.Success(resp.data) }
                    }

                    is Response.Initial -> {
                        _previousChats.update { Response.Initial }
                    }

                    is Response.Error -> {
                        _previousChats.update { Response.Error(resp.exception) }
                    }
                }
            }
        }
    }
}