package com.prafull.chatbuddy.mainApp.modelsScreen.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.goBackStack
import com.prafull.chatbuddy.mainApp.common.components.BotImage
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsMessage
import com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen.ChatScreenMessageBubble
import com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen.getBotImage2
import com.prafull.chatbuddy.mainApp.newHome.presentation.homescreen.NewHomePromptField
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.ExitDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsNewChatScreen(
    modelsChatNewVM: ModelsChatNewVM,
    navController: NavController,
) {
    val uiState by modelsChatNewVM.chatUiState.collectAsState()
    val mA = FirebaseAuth.getInstance()
    val clipboardManager = LocalClipboardManager.current
    val lazyListState = rememberLazyListState()
    val regenerateOutput = remember {
        {
            modelsChatNewVM.regenerateResponse()
        }
    }
    var showExitDialog by remember {
        mutableStateOf(false)
    }
    BackHandler {
        showExitDialog = true
    }
    LaunchedEffect(key1 = uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }
    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        BotImage(
                                modifier = Modifier.size(24.dp),
                                data = modelsChatNewVM.model.image
                        )
                        Text(text = modelsChatNewVM.model.generalName)
                    }
                }, navigationIcon = {
                    IconButton(onClick = {
                        showExitDialog = true
                    }) {
                        Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back"
                        )
                    }
                })
            },
            bottomBar = {
                NewHomePromptField(
                        onSend = { message, images, participant ->
                            modelsChatNewVM.sendMessage(
                                    ModelsMessage(
                                            text = message,
                                            imageBitmaps = images,
                                            participant = participant,
                                            model = modelsChatNewVM.model.generalName,
                                            botImage = modelsChatNewVM.model.image
                                    )
                            )
                        },
                        modelsChatNewVM.isLoading,
                )
            },
    ) { paddingValues ->
        if (uiState.messages.isNotEmpty()) {
            LazyColumn(
                    contentPadding = paddingValues,
                    state = lazyListState
            ) {
                itemsIndexed(uiState.messages, key = { _, item ->
                    item.id
                }) { idx, message ->
                    ChatScreenMessageBubble(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            participant = message.participant,
                            message = Pair(message.text, message.imageBitmaps),
                            botImage1 = message.botImage,
                            botImage2 = getBotImage2(message.model),
                            mA = mA,
                            isLast = idx == uiState.messages.size - 1,
                            isSecondLast = idx == uiState.messages.size - 2,
                            clipboardManager = clipboardManager,
                            model = message.model,
                            regenerateOutput = regenerateOutput
                    )
                }
            }
        } else {
            Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotImage(modifier = Modifier.size(200.dp), data = modelsChatNewVM.model.image)
            }
        }
    }
    if (showExitDialog) {
        ExitDialog(
                title = "Exit",
                text = "Do you Want to exit the conversation",
                confirm = { navController.goBackStack() },
                dismiss = {
                    showExitDialog = false
                }
        )
    }
}