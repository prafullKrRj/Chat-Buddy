package com.prafull.chatbuddy.ui.theme.themechanging

import android.content.Context
import android.content.SharedPreferences

enum class ThemeOption {
    SYSTEM_DEFAULT, LIGHT, DARK
}

class ThemePreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val THEME_KEY = "theme_option"
    }

    fun getThemeOption(): ThemeOption {
        val themeValue = preferences.getString(THEME_KEY, ThemeOption.SYSTEM_DEFAULT.name)
        return ThemeOption.valueOf(themeValue ?: ThemeOption.SYSTEM_DEFAULT.name)
    }

    fun setThemeOption(option: ThemeOption) {
        preferences.edit().putString(THEME_KEY, option.name).apply()
    }
}
