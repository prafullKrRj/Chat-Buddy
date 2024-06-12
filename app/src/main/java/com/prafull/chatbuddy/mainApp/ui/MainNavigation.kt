package com.prafull.chatbuddy.mainApp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prafull.chatbuddy.AppScreens
import com.prafull.chatbuddy.mainApp.ui.components.DrawerContent
import com.prafull.chatbuddy.mainApp.ui.homescreen.ChatViewModel
import com.prafull.chatbuddy.mainApp.ui.homescreen.HomeScreen
import com.prafull.chatbuddy.mainApp.ui.homescreen.components.HomeTopAppBar
import com.prafull.chatbuddy.mainApp.ui.modelscreen.ModelsScreen
import com.prafull.chatbuddy.mainApp.ui.promplibraryscreen.PromptScreen
import com.prafull.chatbuddy.mainApp.ui.viewmodels.HomeViewModel
import com.prafull.chatbuddy.navigateAndPopBackStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@Composable
fun MainNavigation() {
    val chatViewModel: ChatViewModel = getViewModel()
    val homeViewModel: HomeViewModel = getViewModel()
    val scope = rememberCoroutineScope()
    val currChatUUID by chatViewModel.currChatUUID.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val previousChats by homeViewModel.previousChats.collectAsState()
    val mainNavController = rememberNavController()
    val currDestination = mainNavController.currentBackStackEntryAsState().value?.destination?.route

    ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                        previousChats = previousChats,
                        homeViewModel = homeViewModel,
                        navController = mainNavController,
                        closeDrawer = { scope.launch { drawerState.close() } },
                        scope = scope,
                        currChatUUID = currChatUUID
                ) { chatHistory ->
                    scope.launch {
                        mainNavController.navigateAndPopBackStack(AppScreens.HOME.name)
                        chatViewModel.chatFromHistory(chatHistory)
                        delay(500L)
                        drawerState.close()
                    }
                }
            },
    ) {
        Scaffold(
                topBar = {
                    when (currDestination) {
                        AppScreens.HOME.name -> {
                            HomeTopAppBar(
                                    homeViewModel = homeViewModel,
                                    chatViewModel = chatViewModel
                            ) {
                                scope.launch {
                                    drawerState.apply {
                                        drawerState.open()
                                        homeViewModel.getPreviousChats()
                                    }
                                }
                            }
                        }

                        AppScreens.MODELS.name -> {
                            ModelsAndPromptTopAppBar(title = "Models", drawerState, scope) {
                                homeViewModel.getPreviousChats()
                            }
                        }

                        AppScreens.PROMPT.name -> {
                            ModelsAndPromptTopAppBar(title = "Prompt", drawerState, scope) {
                                homeViewModel.getPreviousChats()
                            }
                        }
                    }
                }
        ) { paddingValues ->
            NavHost(navController = mainNavController, startDestination = AppScreens.HOME.name) {
                composable(route = AppScreens.HOME.name) {
                    HomeScreen(
                            modifier = Modifier.padding(paddingValues),
                            chatViewModel,
                            homeViewModel
                    )
                }
                composable(route = AppScreens.MODELS.name) {
                    ModelsScreen(mainNavController)
                }
                composable(route = AppScreens.PROMPT.name) {
                    PromptScreen(Modifier.padding(), paddingValues, mainNavController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsAndPromptTopAppBar(
    title: String,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navigate: () -> Unit
) {
    CenterAlignedTopAppBar(title = {
        Text(text = title)
    }, navigationIcon = {
        IconButton(onClick = {
            scope.launch {
                drawerState.open()
                navigate()
            }
        }) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu Navigate")
        }
    })
}
