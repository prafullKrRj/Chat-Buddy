package com.prafull.chatbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.authScreen.AuthScreen
import com.prafull.chatbuddy.homeScreen.HomeScreen
import com.prafull.chatbuddy.ui.theme.ChatBuddyTheme
import kotlinx.serialization.descriptors.serialDescriptor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatBuddyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val destination = if (FirebaseAuth.getInstance().currentUser == null) MajorScreens.Auth.name else MajorScreens.App.name
                    NavGraph(startDestination = destination)
                }
            }
        }
    }
}

@Composable
fun NavGraph(modifier: Modifier = Modifier, startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = MajorScreens.Auth.name) {
            AuthScreen(navController = navController, mAuth = FirebaseAuth.getInstance())
        }
        composable(route = MajorScreens.App.name) {
            HomeScreen()
        }
    }
}
enum class MajorScreens {
    Auth,
    App
}