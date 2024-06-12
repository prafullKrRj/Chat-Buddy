package com.prafull.chatbuddy.mainApp.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.home.data.ChatRepository
import com.prafull.chatbuddy.mainApp.home.data.HomeRepository
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.HomeViewModel
import com.prafull.chatbuddy.mainApp.promptlibrary.data.PromptLibraryRepo
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptLibraryViewModel
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
                },
                systemInstruction = content {

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
    single<PromptLibraryRepo> { PromptLibraryRepo() }
    viewModel<ChatViewModel> { ChatViewModel() }
    viewModel<PromptLibraryViewModel> { PromptLibraryViewModel() }
}