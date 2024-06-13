package com.prafull.chatbuddy

import android.app.Application
import com.prafull.chatbuddy.authScreen.di.authModule
import com.prafull.chatbuddy.mainApp.di.repositories
import com.prafull.chatbuddy.mainApp.di.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ChatBuddyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(viewModels, authModule, repositories)
            androidContext(this@ChatBuddyApp)
        }
    }
}
