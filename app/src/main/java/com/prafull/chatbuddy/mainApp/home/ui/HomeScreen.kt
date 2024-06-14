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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.AppScreens
import com.prafull.chatbuddy.mainApp.ads.BannerAd
import com.prafull.chatbuddy.mainApp.ads.rewardedAds
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
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
    val mA = FirebaseAuth.getInstance()
    val chatUiState = chatViewModel.uiState.collectAsState()
    val isChatting by chatViewModel.chatting.collectAsState()
    val adButtonState by homeViewModel.adButtonEnabled.collectAsState()
    val modelsState by homeViewModel.modelDialogState.collectAsState()

    if (homeViewModel.modelButtonClicked) {
        SelectModelDialogBox(modelsState = modelsState, onModelSelect = {}, onDismissRequest = {
            homeViewModel.modelButtonClicked = false
        })
    }

    Column(
            modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isChatting) BannerAd()

        if (promptType.isNotEmpty()) {
            PromptCard(promptType, isChatting)
        }
        ChatMessages(
                Modifier.weight(1f),
                chatUiState.value.messages.reversed(),
                mA,
                isChatting,
                navController,
                homeViewModel,
                promptType
        )
        Column(
                Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 4.dp)
        ) {
            PromptField(chatViewModel)
            if (isChatting) BannerAd()
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

@Composable
fun PromptCard(promptType: PromptLibraryItem, isChatting: Boolean) {
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

@Composable
fun ChatMessages(
    modifier: Modifier,
    messages: List<ChatMessage>,
    mA: FirebaseAuth,
    isChatting: Boolean,
    navController: NavController,
    homeViewModel: HomeViewModel,
    promptType: PromptLibraryItem
) {
    val clipboardManager = LocalClipboardManager.current
    LazyColumn(
            modifier = modifier, userScrollEnabled = true, reverseLayout = isChatting
    ) {
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
        items(messages, key = {
            it.id
        }) { message ->
            MessageBubble(message = message, mA = mA, clipboardManager)
        }
    }
}
