package com.prafull.chatbuddy.homeScreen.ui.homescreen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.AppScreens
import com.prafull.chatbuddy.ads.rewardedAds
import com.prafull.chatbuddy.homeScreen.ui.components.AdWindow
import com.prafull.chatbuddy.homeScreen.ui.components.DrawerContent
import com.prafull.chatbuddy.homeScreen.ui.components.MessageBubble
import com.prafull.chatbuddy.homeScreen.ui.components.PromptField
import com.prafull.chatbuddy.homeScreen.ui.components.TopAppBar
import com.prafull.chatbuddy.homeScreen.ui.modelscreen.ModelsScreen
import com.prafull.chatbuddy.homeScreen.ui.promplibraryscreen.PromptScreen
import com.prafull.chatbuddy.homeScreen.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@Composable
fun HomeScreen() {
    val chatViewModel: ChatViewModel = getViewModel()
    val homeViewModel: HomeViewModel = getViewModel()
    val scope = rememberCoroutineScope()
    val currChatUUID by chatViewModel.currChatUUID.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val previousChats by homeViewModel.previousChats.collectAsState()
    val mainNavController = rememberNavController()

    ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                        previousChats = previousChats,
                        homeViewModel = homeViewModel,
                        navController = mainNavController,
                        closeDrawer = { scope.launch { drawerState.close() } },
                        currChatUUID = currChatUUID
                ) { chatHistory ->
                    scope.launch {
                        chatViewModel.chatFromHistory(chatHistory)
                        delay(500L)
                        drawerState.close()
                    }
                }
            },
    ) {
        Scaffold(
                topBar = {
                    TopAppBar(homeViewModel = homeViewModel, chatViewModel = chatViewModel) {
                        scope.launch {
                            drawerState.apply {
                                drawerState.open()
                                delay(1000)
                                homeViewModel.getPreviousChats()
                            }
                        }
                    }
                }
        ) { paddingValues ->

            NavHost(navController = mainNavController, startDestination = AppScreens.HOME.name) {
                composable(route = AppScreens.HOME.name) {
                    MainUI(
                            modifier = Modifier.padding(paddingValues),
                            chatViewModel,
                            homeViewModel
                    )
                }
                composable(route = AppScreens.MODELS.name) {
                    ModelsScreen(mainNavController)
                }
                composable(route = AppScreens.PROMPT.name) {
                    PromptScreen(mainNavController)
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainUI(modifier: Modifier, chatViewModel: ChatViewModel, homeViewModel: HomeViewModel) {
    val mA = FirebaseAuth.getInstance()

    val state = chatViewModel.uiState.collectAsState()

    val isChatting by chatViewModel.chatting.collectAsState()

    val adButtonState by homeViewModel.adButtonEnabled.collectAsState()

    Column(
            modifier = modifier
                .fillMaxSize()
    ) {
        if (!isChatting) {
            Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
            ) {
                AdWindow(homeViewModel) {
                    homeViewModel.updateAdButtonState(false)
                }
                PromptField(chatViewModel)
            }
        } else {
            LazyColumn(
                    modifier = Modifier
                        .weight(1f),
                    userScrollEnabled = true,
                    reverseLayout = true
            ) {
                items(state.value.messages.reversed()) { message ->
                    MessageBubble(message = message, mA = mA)
                }
            }
            Column(Modifier.padding(8.dp)) {
                PromptField(chatViewModel)
            }
        }
    }
    if (!adButtonState) {
        rewardedAds(LocalContext.current as Activity, failed = {
            homeViewModel.updateAdButtonState(true)
        }) {
            homeViewModel.adWatched()
            homeViewModel.updateAdButtonState(true)
        }
    }
}