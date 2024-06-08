package com.prafull.chatbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.authScreen.AuthScreen
import com.prafull.chatbuddy.homeScreen.ui.homescreen.HomeScreen
import com.prafull.chatbuddy.ui.theme.ChatBuddyTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        MobileAds.initialize(this)
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mAuth by inject<FirebaseAuth>()
        setContent {
            ChatBuddyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val destination =
                        if (mAuth.currentUser == null) MajorScreens.Auth.name else MajorScreens.App.name
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = destination) {
                        composable(route = MajorScreens.Auth.name) {
                            AuthScreen(
                                    navController = navController,
                                    mAuth = FirebaseAuth.getInstance()
                            )
                        }
                        composable(route = MajorScreens.App.name) {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}

enum class AppScreens {
    HOME, MODELS, PROMPT
}

enum class MajorScreens {
    Auth,
    App
}