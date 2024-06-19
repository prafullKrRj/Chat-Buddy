package com.prafull.chatbuddy.mainApp.home.ui.homescreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.prafull.chatbuddy.MainActivity
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.mainApp.ads.BannerAd
import com.prafull.chatbuddy.mainApp.ads.rewardedAds
import com.prafull.chatbuddy.mainApp.home.ui.components.AdWindow
import com.prafull.chatbuddy.mainApp.home.ui.components.MessageBubble
import com.prafull.chatbuddy.mainApp.home.ui.components.PremiumPlanComp
import com.prafull.chatbuddy.mainApp.home.ui.components.SelectModelDialogBox
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.model.Model

/**
 * HomeScreen is the main screen of the application.
 * It displays the chat messages, ad window, and premium plan component.
 * It also handles the rewarded ads and updates the UI based on the chat and home view model states.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    chatViewModel: ChatViewModel,
    homeViewModel: HomeViewModel,
    promptType: PromptLibraryItem
) {
    val mA = FirebaseAuth.getInstance()
    val chatUiState = chatViewModel.uiState.collectAsState()
    Log.d("HomeScreen", "Chat UI State: ${chatUiState.value.messages}")
    val modelsState by homeViewModel.modelDialogState.collectAsState()
    val storageReference = FirebaseStorage.getInstance().reference
    val selectedModel = remember<(Model) -> Unit> {
        { model ->
            homeViewModel.modelButtonClicked = false
            chatViewModel.onModelSelected(model)
        }
    }
    if (homeViewModel.modelButtonClicked) {
        SelectModelDialogBox(
                modelsState = modelsState,
                onModelSelect = selectedModel,
                onDismissRequest = {
                    homeViewModel.modelButtonClicked = false
                })
    }
    val clipboardManager = LocalClipboardManager.current
    val lazyListState = rememberLazyListState()
    LaunchedEffect(chatUiState.value.messages.size) {
        if (chatUiState.value.messages.isNotEmpty()) lazyListState.animateScrollToItem(chatUiState.value.messages.size + 1)
    }
    val context = LocalContext.current
    Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
    ) {
        LazyColumn(
                modifier = Modifier,
                contentPadding = paddingValues,
                userScrollEnabled = true,
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!chatViewModel.chatting && promptType.isEmpty()) {
                item(key = "ad") {
                    BannerAd()
                    AdWindow(homeViewModel) {
                        homeViewModel.adButtonEnabled = false
                        rewardedAds(context as MainActivity, failed = {
                            homeViewModel.adButtonEnabled = true
                        }) {
                            homeViewModel.adWatched()
                            homeViewModel.adButtonEnabled = true
                        }
                    }
                }
                item("premium") {
                    PremiumPlanComp {
                        navController.navigate(Routes.PaymentsScreen)
                    }
                }
            }
            item(key = "promptDetails") {
                if (promptType.isNotEmpty() || chatViewModel.getCurrChat().toPromptLibraryItem()
                        .isNotEmpty()
                ) {
                    if (promptType.isNotEmpty()) {
                        PromptCard(promptType)
                    } else {
                        PromptCard(chatViewModel.getCurrChat().toPromptLibraryItem())
                    }
                }

            }
            itemsIndexed(chatUiState.value.messages) { index, chatMessage ->
                when (index) {
                    chatUiState.value.messages.lastIndex -> {
                        MessageBubble(
                                message = chatMessage,
                                mA = mA,
                                clipboardManager = clipboardManager,
                                context = context,
                                isSecondLast = false,
                                isLast = true,
                                chatViewModel = chatViewModel
                        )
                    }

                    chatUiState.value.messages.lastIndex - 1 -> {
                        MessageBubble(
                                message = chatMessage,
                                mA = mA,
                                clipboardManager = clipboardManager,
                                context = context,
                                isSecondLast = true,
                                isLast = false,
                                chatViewModel = chatViewModel
                        )
                    }

                    else -> {
                        MessageBubble(
                                message = chatMessage,
                                mA = mA,
                                clipboardManager = clipboardManager,
                                context = context,
                                isSecondLast = false,
                                isLast = false,
                                chatViewModel = chatViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PromptCard(promptType: PromptLibraryItem) {
    Card(modifier = Modifier.padding(8.dp)) {
        Text(
                text = promptType.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
        )

        Text(
                text = "Description: ${promptType.description}",
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
        )
    }
}