package com.prafull.chatbuddy.mainApp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.prafull.chatbuddy.mainApp.home.data.remote.ClaudeApiService
import com.prafull.chatbuddy.mainApp.home.data.repos.ClaudeRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.GeminiRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.HomeRepository
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.chat.ModelsChatVM
import com.prafull.chatbuddy.mainApp.promptlibrary.data.PromptLibraryRepo
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptLibraryViewModel
import com.prafull.chatbuddy.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
    single<ClaudeApiService> {
        Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ClaudeApiService::class.java)
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