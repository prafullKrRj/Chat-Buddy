package com.prafull.chatbuddy

import android.app.Application
import com.prafull.chatbuddy.di.appModule
import com.prafull.chatbuddy.di.authModule
import org.koin.core.context.startKoin

class ChatBuddyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule, authModule)
        }
    }
}
