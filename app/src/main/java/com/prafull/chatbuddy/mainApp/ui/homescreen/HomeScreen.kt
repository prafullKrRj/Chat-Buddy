package com.prafull.chatbuddy.mainApp.ui.homescreen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.ads.BannerAd
import com.prafull.chatbuddy.ads.rewardedAds
import com.prafull.chatbuddy.mainApp.ui.homescreen.components.AdWindow
import com.prafull.chatbuddy.mainApp.ui.homescreen.components.MessageBubble
import com.prafull.chatbuddy.mainApp.ui.homescreen.components.PremiumPlanComp
import com.prafull.chatbuddy.mainApp.ui.homescreen.components.PromptField
import com.prafull.chatbuddy.mainApp.ui.viewmodels.HomeViewModel

/**
 * HomeScreen is the main screen of the application.
 * It displays the chat messages, ad window, and premium plan component.
 * It also handles the rewarded ads and updates the UI based on the chat and home view model states.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(modifier: Modifier, chatViewModel: ChatViewModel, homeViewModel: HomeViewModel) {
    // Firebase Authentication instance
    val mA = FirebaseAuth.getInstance()

    // Collecting the chat view model state
    val state = chatViewModel.uiState.collectAsState()

    // Checking if the user is currently chatting
    val isChatting by chatViewModel.chatting.collectAsState()

    // Checking if the ad button is enabled
    val adButtonState by homeViewModel.adButtonEnabled.collectAsState()

    // Main layout of the HomeScreen
    Column(
            modifier = modifier
                .fillMaxSize()
    ) {
        // Scrollable list for displaying messages, ad window, and premium plan component
        BannerAd()
        LazyColumn(
                modifier = Modifier
                    .weight(1f),
                userScrollEnabled = true,
                reverseLayout = true
        ) {
            // If the user is not chatting, display the ad window and premium plan component
            if (!isChatting) {
                item {
                    AdWindow(homeViewModel) {
                        homeViewModel.updateAdButtonState(false)
                    }
                }
                item {
                    PremiumPlanComp()
                }
            }
            // Display the chat messages
            items(state.value.messages.reversed()) { message ->
                MessageBubble(message = message, mA = mA)
            }
        }
        // Display the prompt field for user input
        Column(Modifier.padding(8.dp)) {
            PromptField(chatViewModel)
        }
    }
    // If the ad button is not enabled, display the rewarded ads
    if (!adButtonState) {
        rewardedAds(LocalContext.current as Activity, failed = {
            homeViewModel.updateAdButtonState(true)
        }) {
            homeViewModel.adWatched()
            homeViewModel.updateAdButtonState(true)
        }
    }
}