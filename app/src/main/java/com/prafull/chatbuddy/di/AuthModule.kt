package com.prafull.chatbuddy.di

import com.prafull.chatbuddy.authScreen.AuthViewModel
import com.prafull.chatbuddy.authScreen.repo.AuthRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> {
        AuthRepository()
    }
    viewModel {
        AuthViewModel()
    }
}