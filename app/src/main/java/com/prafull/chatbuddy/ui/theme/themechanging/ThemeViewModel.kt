package com.prafull.chatbuddy.ui.theme.themechanging

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferences = ThemePreferences(application)

    private val _themeOption = MutableStateFlow(themePreferences.getThemeOption())
    val themeOption: StateFlow<ThemeOption> = _themeOption.asStateFlow()

    fun setThemeOption(option: ThemeOption) {
        themePreferences.setThemeOption(option)
        _themeOption.update {
            option
        }
    }
}
