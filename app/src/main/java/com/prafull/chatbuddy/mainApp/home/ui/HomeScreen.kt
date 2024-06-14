package com.prafull.chatbuddy.mainApp.home.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.AppScreens
import com.prafull.chatbuddy.mainApp.ads.BannerAd
import com.prafull.chatbuddy.mainApp.ads.rewardedAds
import com.prafull.chatbuddy.mainApp.home.ui.components.AdWindow
import com.prafull.chatbuddy.mainApp.home.ui.components.MessageBubble
import com.prafull.chatbuddy.mainApp.home.ui.components.PremiumPlanComp
import com.prafull.chatbuddy.mainApp.home.ui.components.PromptField
import com.prafull.chatbuddy.mainApp.home.ui.components.SelectModelDialogBox
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem

/**
 * HomeScreen is the main screen of the application.
 * It displays the chat messages, ad window, and premium plan component.
 * It also handles the rewarded ads and updates the UI based on the chat and home view model states.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    chatViewModel: ChatViewModel,
    homeViewModel: HomeViewModel,
    promptType: PromptLibraryItem
) {
    // Firebase Authentication instance
    val mA = FirebaseAuth.getInstance()

    if (promptType.isNotEmpty()) {
        chatViewModel.loadFromPromptLibrary(promptType)
    }
    // Collecting the chat view model state
    val chatUiState = chatViewModel.uiState.collectAsState()

    // Checking if the user is currently chatting
    val isChatting by chatViewModel.chatting.collectAsState()

    // Checking if the ad button is enabled
    val adButtonState by homeViewModel.adButtonEnabled.collectAsState()

    val modelsState by homeViewModel.modelDialogState.collectAsState()
    if (homeViewModel.modelButtonClicked) {
        SelectModelDialogBox(modelsState = modelsState, onModelSelect = {}, onDismissRequest = {
            homeViewModel.modelButtonClicked = false
        })
    }
    // Main layout of the HomeScreen
    Column(
            modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isChatting) BannerAd()

        if (promptType.isNotEmpty()) {
            Card(modifier = Modifier.padding(8.dp)) {
                Text(
                        text = promptType.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(8.dp)
                )
                if (!isChatting) {
                    Text(
                            text = "Description: ${promptType.description}",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        LazyColumn(
                modifier = Modifier.weight(1f), userScrollEnabled = true, reverseLayout = isChatting
        ) {
            // If the user is not chatting, display the ad window and premium plan component
            if (!isChatting && promptType.isEmpty()) {
                item {
                    AdWindow(homeViewModel) {
                        homeViewModel.updateAdButtonState(false)
                    }
                }
                item {
                    PremiumPlanComp {
                        navController.navigate(AppScreens.PAYMENTS.name)
                    }
                }
            }

            // Display the chat messages
            items(chatUiState.value.messages.reversed()) { message ->
                MessageBubble(message = message, mA = mA)
            }
        }
        // Display the prompt field for user input
        Column(Modifier.padding(8.dp)) {
            PromptField(chatViewModel)
            if (isChatting) BannerAd()
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