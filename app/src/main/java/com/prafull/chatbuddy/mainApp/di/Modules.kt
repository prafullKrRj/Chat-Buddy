package com.prafull.chatbuddy.mainApp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.mainApp.home.data.GeminiRepository
import com.prafull.chatbuddy.mainApp.home.data.HomeRepository
import com.prafull.chatbuddy.mainApp.home.data.claude.ClaudeRepository
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelViewModel
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
    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }
    single { }
}
val viewModels = module {
    viewModel<HomeViewModel> {
        HomeViewModel()
    }
    viewModel<ChatViewModel> { ChatViewModel() }
    viewModel<PromptLibraryViewModel> { PromptLibraryViewModel() }
    viewModel<ModelViewModel> { ModelViewModel() }
    viewModel<SettingsViewModel> { SettingsViewModel() }
}