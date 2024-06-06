package com.prafull.chatbuddy

import android.app.Application
import com.prafull.chatbuddy.homeScreen.di.chatModule
import com.prafull.chatbuddy.authScreen.di.authModule
import com.prafull.chatbuddy.homeScreen.di.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ChatBuddyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(chatModule, authModule, homeModule)
            androidContext(this@ChatBuddyApp)
        }
    }
}
