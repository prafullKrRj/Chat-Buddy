package com.prafull.chatbuddy

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.prafull.chatbuddy.authScreen.AuthViewModel
import com.prafull.chatbuddy.authScreen.repo.AuthRepository
import com.prafull.chatbuddy.mainApp.common.data.HomeRepository
import com.prafull.chatbuddy.mainApp.common.data.remote.ClaudeApiService
import com.prafull.chatbuddy.mainApp.common.data.repos.ClaudeRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.FirebaseRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.GeminiRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.OpenAiRepo
import com.prafull.chatbuddy.mainApp.historyscreen.data.HistoryRepo
import com.prafull.chatbuddy.mainApp.historyscreen.ui.HistoryViewModel
import com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen.HomeChatVM
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.ModelViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.chat.ModelsChatNewVM
import com.prafull.chatbuddy.mainApp.promptlibrary.data.PromptLibraryRepo
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptChatVM
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptLibraryViewModel
import com.prafull.chatbuddy.mainApp.settings.SettingsViewModel
import com.prafull.chatbuddy.ui.theme.themechanging.ThemePreferences
import com.prafull.chatbuddy.ui.theme.themechanging.ThemeViewModel
import com.prafull.chatbuddy.utils.SharedPrefManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatBuddyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(viewModels, authModule, repositoryModule, firebaseModule, module {
                single<Gson> {
                    Gson()
                }
                single {
                    SharedPrefManager(get())
                }
            })
            androidContext(this@ChatBuddyApp)

        }
    }
}

val repositoryModule = module {
    single<HomeRepository> { HomeRepository() }

    single<PromptLibraryRepo> { PromptLibraryRepo() }

    single { GeminiRepo() }
    single { ClaudeRepo() }
    single { OpenAiRepo() }
    single { FirebaseRepo() }
    single { HistoryRepo() }
    single<ClaudeApiService> {
        Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ClaudeApiService::class.java)
    }
}
val firebaseModule = module {
    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }
    single<FirebaseStorage> {
        FirebaseStorage.getInstance()
    }
    single<FirebaseFirestore> {
        FirebaseFirestore.getInstance()
    }
}
val viewModels = module {
    viewModel<HomeViewModel> { HomeViewModel() }
    viewModel<HomeChatVM> { HomeChatVM(get(), get()) }
    viewModel { PromptChatVM(get()) }
    viewModel { ModelsChatNewVM(get(),get()) }
    viewModel { HistoryViewModel() }
    viewModel<PromptLibraryViewModel> { PromptLibraryViewModel() }
    viewModel<ModelViewModel> { ModelViewModel() }
    viewModel<SettingsViewModel> { SettingsViewModel() }

    single { ThemePreferences(get()) } // Provide ThemePreferences
    viewModel { ThemeViewModel(get()) }
}

val authModule = module {
    single<AuthRepository> {
        AuthRepository()
    }
    viewModel {
        AuthViewModel()
    }
}