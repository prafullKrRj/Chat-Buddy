package com.prafull.chatbuddy.homeScreen.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.homeScreen.data.ChatRepository
import com.prafull.chatbuddy.homeScreen.ui.viewmodels.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chatModule = module {
    single<GenerativeModel> {
        GenerativeModel(
                modelName = "gemini-1.5-flash-latest",
                apiKey = BuildConfig.GEMINI_API_KEY,
                generationConfig = generationConfig {
                    temperature = 0.7f
                }
        )
    }
    single<ChatRepository> {
        ChatRepository()
    }
    viewModel { ChatViewModel() }
}