package com.prafull.chatbuddy.mainApp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.prafull.chatbuddy.mainApp.home.data.repos.HomeRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.ClaudeRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.GeminiRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.OpenAiRepository
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.chat.ModelsChatVM
import com.prafull.chatbuddy.mainApp.promptlibrary.data.PromptLibraryRepo
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptLibraryViewModel
import com.prafull.chatbuddy.settings.SettingsViewModel
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
    single<OpenAiRepository> {
        OpenAiRepository()
    }
    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }
    /* single<OpenAIService> {
         Retrofit.Builder()
             .baseUrl("https://api.openai.com/")
             .addConverterFactory(GsonConverterFactory.create())
             .build().create(OpenAIService::class.java)
     }*/
    single<FirebaseStorage> {
        FirebaseStorage.getInstance()
    }
    single<Gson> {
        Gson()
    }
}
val viewModels = module {
    viewModel<HomeViewModel> {
        HomeViewModel()
    }
    viewModel<ChatViewModel> { ChatViewModel() }
    viewModel<ModelsChatVM> { ModelsChatVM(get()) }
    viewModel<PromptLibraryViewModel> { PromptLibraryViewModel() }
    viewModel<ModelViewModel> { ModelViewModel() }
    viewModel<SettingsViewModel> { SettingsViewModel() }
}