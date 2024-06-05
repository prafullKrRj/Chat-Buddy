package com.prafull.chatbuddy.homeScreen.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.ads.rewardedAds
import com.prafull.chatbuddy.homeScreen.ui.components.AdWindow
import com.prafull.chatbuddy.homeScreen.ui.components.MessageBubble
import com.prafull.chatbuddy.homeScreen.ui.components.PromptField
import com.prafull.chatbuddy.homeScreen.ui.components.TopAppBar
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen() {
    val viewModel: ChatViewModel = getViewModel()
    Scaffold(
            topBar = {
                TopAppBar(viewModel = viewModel)
            }
    ) { paddingValues ->
        MainUI(modifier = Modifier.padding(paddingValues), viewModel)
    }
}

@Composable
fun MainUI(modifier: Modifier, viewModel: ChatViewModel) {
    val mA = FirebaseAuth.getInstance()

    val state = viewModel.uiState.collectAsState()

    val isChatting by viewModel.chatting.collectAsState()

    var watchAd by rememberSaveable {
        mutableStateOf(false)
    }
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
                AdWindow(viewModel) {
                    watchAd = true
                }
                PromptField(viewModel)
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
                PromptField(viewModel)
            }
        }
    }
    if (watchAd) {
        rewardedAds(LocalContext.current as Activity) {
            viewModel.adWatched()
            watchAd = false
        }
    }
}