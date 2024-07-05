package com.prafull.chatbuddy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import com.prafull.chatbuddy.mainApp.common.model.Model
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.ModelSafety
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
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

                    }
                }
            }
        }
    }
}

fun NavController.navigateAndPopBackStack(screen: Any) {
    while (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)
        popBackStack()
    navigate(screen)
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
    object NewHomeNav

    @Serializable
    object Home

    @Serializable
    object HomeChatScreen


    @Serializable
    object ModelsNav

    @Serializable
    object ModelsScreen


    @Serializable
    object PromptLibraryNav

    @Serializable
    object PromptLibraryScreen

    @Serializable
    data class PromptLibraryChat(
        val name: String,
        val description: String,
        val system: String,
        val user: String
    ) {
        fun toPromptLibraryItem() = PromptLibraryItem(name, description, system, user)
    }

    @Serializable
    object PaymentsScreen


    @Serializable
    data class ModelChatScreen(
        val id: String = "",
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

    @Serializable
    object History
}

sealed class AppDes(
    val route: Any,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @StringRes val contentDescription: Int,

    ) {
    data object Home : AppDes(
            Routes.NewHomeNav,
            R.string.home,
            R.drawable.sharp_chat_bubble_outline_24,
            R.string.home
    )

    data object PromptScreen : AppDes(
            Routes.PromptLibraryNav,
            R.string.prompt_library,
            R.drawable.outline_apps_24,
            R.string.prompt_library
    )

    data object HistoryScreen : AppDes(
            Routes.History,
            R.string.history,
            R.drawable.sharp_manage_history_24,
            R.string.history
    )

    data object ModelsScreen : AppDes(
            Routes.ModelsNav,
            R.string.explore_models,
            R.drawable.baseline_explore_24,
            R.string.explore_models
    )

    data object SettingsScreen : AppDes(
            Routes.SettingsScreen,
            R.string.settings,
            R.drawable.outline_settings_24,
            R.string.settings
    )
}

enum class RouteString {
    Home, HomeChat, PromptLib, PromptLibChat, Models, ModelsChat, Settings, History
}