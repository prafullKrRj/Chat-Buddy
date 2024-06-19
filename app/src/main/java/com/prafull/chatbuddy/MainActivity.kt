package com.prafull.chatbuddy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelSafety
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.model.Model
import com.prafull.chatbuddy.settings.SettingsScreen
import com.prafull.chatbuddy.ui.theme.ChatBuddyTheme
import com.prafull.chatbuddy.ui.theme.themechanging.ThemeOption
import com.prafull.chatbuddy.ui.theme.themechanging.ThemeViewModel
import com.prafull.chatbuddy.utils.Const
import kotlinx.serialization.Serializable
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        MobileAds.initialize(this)
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mAuth by inject<FirebaseAuth>()
        val themeViewModel by inject<ThemeViewModel>()
        setContent {
            val themeOption by themeViewModel.themeOption.collectAsState()
            val isDarkTheme = when (themeOption) {
                ThemeOption.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                ThemeOption.LIGHT -> false
                ThemeOption.DARK -> true
            }
            ChatBuddyTheme(darkTheme = isDarkTheme, dynamicColor = true) {
                Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                ) {
                    val destination: Any =
                        if (mAuth.currentUser == null) Routes.Auth else Routes.App
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = destination) {
                        composable<Routes.Auth> {
                            AuthScreen(
                                    navController = navController,
                                    mAuth = FirebaseAuth.getInstance()
                            )
                        }
                        composable<Routes.App> {
                            MainNavigation(navController)
                        }
                        composable<Routes.SettingsScreen> {
                            SettingsScreen(navController = navController) {
                                navController.goBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}

fun NavController.navigateAndPopBackStack(screen: Any) {
    popBackStack()
    navigate(screen)
}

fun NavController.navigateHomeWithArgs(promptLibraryItem: PromptLibraryItem) {
    navigateAndPopBackStack(
            promptLibraryItem.toHomeArgs()
    )
}

fun NavController.goBackStack() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

fun NavController.signOutAndNavigateToAuth() {
    popBackStack(graph.startDestinationId, true)
    navigate(Routes.Auth)
}

sealed interface Routes {


    @Serializable
    object SettingsScreen

    @Serializable
    object Auth

    @Serializable
    object App


    @Serializable
    object Home

    @Serializable
    data class HomeWithArgs(
        val name: String,
        val description: String,
        val system: String,
        val user: String
    ) {
        fun toPromptLibraryItem() = PromptLibraryItem(name, description, system, user)
    }

    @Serializable
    object ModelsScreen

    @Serializable
    object PromptScreen

    @Serializable
    object PaymentsScreen

    @Serializable
    data class ChatScreen(
        val generalName: String = "",
        val actualName: String = "",
        val currPricePerToken: String = "0.0",
        val image: String = "",
        val hasVision: Boolean = false,
        val hasFiles: Boolean = false,
        val modelGroup: String = "",
        val taskType: String = "",
        val temperature: Float = 0.7f,
        val system: String = Const.GENERAL_SYSTEM_PROMPT,
        val safetySetting: String = ModelSafety.UNSPECIFIED.name
    ) {
        fun toModel() = Model(
                generalName,
                actualName,
                currPricePerToken.toDouble(),
                image,
                hasVision,
                hasFiles,
                modelGroup,
                taskType,
                temperature.toDouble(),
                system,
                safetySetting
        )
    }
}

enum class RoutesStrings {
    SettingsScreen,
    Auth,
    App,
    Home,
    HomeWithArgs,
    ModelsScreen,
    ChatScreen,
    PromptScreen,
    PaymentsScreen
}
