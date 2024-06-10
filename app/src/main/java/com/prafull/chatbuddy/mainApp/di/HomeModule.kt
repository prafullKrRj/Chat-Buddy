package com.prafull.chatbuddy.mainApp.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.data.ChatRepository
import com.prafull.chatbuddy.mainApp.data.HomeRepository
import com.prafull.chatbuddy.mainApp.data.PromptLibraryRepo
import com.prafull.chatbuddy.mainApp.ui.homescreen.ChatViewModel
import com.prafull.chatbuddy.mainApp.ui.promplibraryscreen.PromptLibraryViewModel
import com.prafull.chatbuddy.mainApp.ui.viewmodels.HomeViewModel
import org.koin.android.ext.koin.androidContext
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
                    text(androidContext().getString(R.string.story_telling_sidekick))
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