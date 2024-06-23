package com.prafull.chatbuddy.mainApp.common

import android.graphics.Bitmap

interface BaseHistory {
    val id: String
    val system: String
    val messages: MutableList<BaseMessage>
    val temperature: Double
    val safetySettings: String
    val promptType: String
    val model: String
}

interface BaseMessage {
    val id: String
    val text: String
    val participant: String
    val model: String
    var botImage: String
    val imageBase64: List<String>
    val imageBitmaps: List<Bitmap?>
}