package com.prafull.chatbuddy.mainApp.ui.homescreen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.ads.rewardedAds
import com.prafull.chatbuddy.mainApp.ui.components.AdWindow
import com.prafull.chatbuddy.mainApp.ui.components.MessageBubble
import com.prafull.chatbuddy.mainApp.ui.components.PromptField
import com.prafull.chatbuddy.mainApp.ui.viewmodels.HomeViewModel


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(modifier: Modifier, chatViewModel: ChatViewModel, homeViewModel: HomeViewModel) {
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