package com.prafull.chatbuddy.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

    fun setDefaultModel(model: String) {
        val editor = sharedPreferences.edit()
        editor.putString("defaultModel", "${Const.CHAT_BUDDY}/${Const.CHAT_BUDDY}")
        editor.apply()
    }

    fun getDefaultModel(): String {
        if (!sharedPreferences.contains("defaultModel")) {
            setDefaultModel(Const.CHAT_BUDDY)
        }
        return sharedPreferences.getString("defaultModel", Const.CHAT_BUDDY)!!
    }
}