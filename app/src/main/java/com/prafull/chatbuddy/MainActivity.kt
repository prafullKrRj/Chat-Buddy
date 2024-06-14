package com.prafull.chatbuddy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.authScreen.AuthScreen
import com.prafull.chatbuddy.mainApp.MainNavigation
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.settings.SettingsScreen
import com.prafull.chatbuddy.ui.theme.ChatBuddyTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
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
                            MainNavigation(navController)
                        }
                        composable(AppScreens.SETTINGS.name) {
                            SettingsScreen(navController = navController) {
                                navController.popBackStack()
                                navController.navigate(MajorScreens.App.name)
                            }
                        }
                    }
                }
            }
        }
    }
}


fun NavController.navigateAndPopBackStack(route: String) {
    popBackStack()
    navigate(route)
}

fun NavController.navigateHomeWithArgs(promptLibraryItem: PromptLibraryItem) {
    navigateAndPopBackStack(
            AppScreens.HOME.name + "/${promptLibraryItem.name}/${promptLibraryItem.description}/${promptLibraryItem.system}/${promptLibraryItem.user}"
    )
}

fun NavController.navigateIfNotCurrent(route: String, chatViewModel: ChatViewModel) {
    if (currentDestination?.route == route) return
    chatViewModel.loadNewChat()
    navigateAndPopBackStack(route)
}

fun NavController.goBackStack() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

fun NavController.signOutAndNavigateToAuth() {
    popBackStack(graph.startDestinationId, true)
    navigate(MajorScreens.Auth.name)
}

enum class AppScreens {
    HOME, MODELS, PROMPT, PAYMENTS, SETTINGS
}

enum class MajorScreens {
    Auth,
    App,
}