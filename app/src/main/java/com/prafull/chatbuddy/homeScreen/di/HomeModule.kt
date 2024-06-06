package com.prafull.chatbuddy.homeScreen.di

import com.prafull.chatbuddy.homeScreen.data.HomeRepository
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