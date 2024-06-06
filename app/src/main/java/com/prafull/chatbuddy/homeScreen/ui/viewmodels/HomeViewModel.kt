package com.prafull.chatbuddy.homeScreen.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

class HomeViewModel: ViewModel(), KoinComponent {

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
}