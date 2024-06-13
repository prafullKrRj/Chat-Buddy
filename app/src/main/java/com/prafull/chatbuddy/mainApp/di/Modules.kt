package com.prafull.chatbuddy.mainApp.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.home.data.ClaudeRepository
import com.prafull.chatbuddy.mainApp.home.data.GeminiRepository
import com.prafull.chatbuddy.mainApp.home.data.HomeRepository
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelViewModel
import com.prafull.chatbuddy.mainApp.promptlibrary.data.PromptLibraryRepo
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptLibraryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val repositories = module {
    single<HomeRepository> {
        HomeRepository()
    }
    single<PromptLibraryRepo> { PromptLibraryRepo() }
    single<GeminiRepository> {
        GeminiRepository()
    }
    single<ClaudeRepository> {
        ClaudeRepository()
    }
    single<FirebaseFirestore> {
        FirebaseFirestore.getInstance()
    }
    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }
}
val viewModels = module {
    viewModel<HomeViewModel> {
        HomeViewModel()
    }
    viewModel<ChatViewModel> { ChatViewModel() }
    viewModel<PromptLibraryViewModel> { PromptLibraryViewModel() }
    viewModel<ModelViewModel> { ModelViewModel() }

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
}