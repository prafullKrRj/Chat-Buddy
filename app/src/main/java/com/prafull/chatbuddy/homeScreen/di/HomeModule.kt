package com.prafull.chatbuddy.homeScreen.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.homeScreen.data.ChatRepository
import com.prafull.chatbuddy.homeScreen.data.HomeRepository
import com.prafull.chatbuddy.homeScreen.ui.homescreen.ChatViewModel
import com.prafull.chatbuddy.homeScreen.ui.viewmodels.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val homeModule = module {
    single<HomeRepository> {
        HomeRepository()
    }
    viewModel<HomeViewModel> {
        HomeViewModel()
    }
}
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
    single<FirebaseFirestore> {
        FirebaseFirestore.getInstance()
    }
    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }
    viewModel { ChatViewModel() }
}