package com.prafull.chatbuddy.utils

import com.prafull.chatbuddy.BuildConfig
import com.scottyab.aescrypt.AESCrypt

object CryptoEncryption {
    private const val KEY = BuildConfig.CRYPTO_KEY // Replace with your own password

    fun encrypt(message: String): String {
        return AESCrypt.encrypt(KEY, message)
    }

    fun decrypt(message: String): String {
        return AESCrypt.decrypt(KEY, message)
    }
}