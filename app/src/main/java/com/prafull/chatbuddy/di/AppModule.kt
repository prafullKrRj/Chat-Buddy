package com.prafull.chatbuddy.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.homeScreen.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<GenerativeModel> {
        GenerativeModel(
                modelName = "gemini-1.5-flash-latest",
                apiKey = BuildConfig.GEMINI_API_KEY,
                generationConfig = generationConfig {
                    temperature = 0.7f
                }
        )
    }
    viewModel { ChatViewModel() }
}