package com.prafull.chatbuddy.mainApp.promptlibrary.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.goBackStack
import com.prafull.chatbuddy.mainApp.common.components.ChatTopBar
import com.prafull.chatbuddy.mainApp.home.ui.components.SelectModelDialogBox
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.PromptCard
import com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen.ChatScreenMessageBubble
import com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen.getBotImage2
import com.prafull.chatbuddy.mainApp.newHome.presentation.homescreen.NewHomePromptField
import com.prafull.chatbuddy.mainApp.newHome.presentation.homescreen.NewHomeViewModel
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryMessage

@Composable
fun PromptChatScreen(
    promptChatVM: PromptChatVM,
    homeVM: NewHomeViewModel,
    navController: NavController
) {
    var showModelSelectionDialog by remember {
        mutableStateOf(false)
    }
    val modelState by homeVM.modelDialogState.collectAsState()
    val uiState by promptChatVM.chatUiState.collectAsState()
    val mA = FirebaseAuth.getInstance()
    val clipboardManager = LocalClipboardManager.current
    val regenerateOutput = remember {
        {
            promptChatVM.regenerateResponse()
        }
    }
    var showExitDialog by remember {
        mutableStateOf(false)
    }
    BackHandler {
        showExitDialog = true
    }
    Scaffold(
            topBar = {
                ChatTopBar(
                        modelName = promptChatVM.promptModel.generalName,
                        onSelectModel = {
                            showModelSelectionDialog = true
                        },
                        onBackButtonClicked = {
                            navController.popBackStack()
                        }
                )
            },
            bottomBar = {
                NewHomePromptField(
                        onSend = { message, images, participant ->
                            promptChatVM.sendMessage(
                                    PromptLibraryMessage(
                                            text = message,
                                            imageBitmaps = images,
                                            participant = participant,
                                            model = promptChatVM.promptModel.generalName,
                                            botImage = promptChatVM.promptModel.image
                                    )
                            )
                        },
                        promptChatVM.isLoading,
                )
            }
    ) { paddingValues ->
        LazyColumn(
                contentPadding = paddingValues
        ) {
            item("Prompt") {
                PromptCard(promptChatVM.getPromptItem())
            }
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
    }
    if (showModelSelectionDialog) {
        SelectModelDialogBox(modelsState = modelState, onModelSelect = { newModel ->
            promptChatVM.changeModel(newModel)
            showModelSelectionDialog = false
        }) {
            showModelSelectionDialog = false
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

@Composable
fun ExitDialog(title: String, text: String, confirm: () -> Unit, dismiss: () -> Unit) {
    AlertDialog(
            onDismissRequest = {
                dismiss()
            },
            title = { Text(text = "Exit") },
            text = { Text(text = "Do you want to exit from the conversation!") },
            confirmButton = {
                TextButton(onClick = confirm) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = dismiss) { Text("Dismiss") }
            }
    )
}