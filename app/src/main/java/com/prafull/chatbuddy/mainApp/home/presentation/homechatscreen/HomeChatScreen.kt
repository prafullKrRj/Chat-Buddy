package com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
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
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.common.components.ChatTopBar
import com.prafull.chatbuddy.mainApp.common.components.SelectModelDialogBox
import com.prafull.chatbuddy.mainApp.common.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.common.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.common.model.isGptModel
import com.prafull.chatbuddy.mainApp.historyscreen.ui.Loader
import com.prafull.chatbuddy.mainApp.home.models.NormalHistoryMsg
import com.prafull.chatbuddy.mainApp.home.presentation.components.MessageBubble
import com.prafull.chatbuddy.mainApp.home.presentation.components.PromptField
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.ExitDialog

@Composable
fun HomeChatScreen(
    chatVM: HomeChatVM,
    homeVM: HomeViewModel,
    backHandler: () -> Unit = {}
) {
    val uiState by chatVM.chatUiState.collectAsState()
    val mA = FirebaseAuth.getInstance()
    val clipboardManager = LocalClipboardManager.current
    var showModelSelectionDialog by remember {
        mutableStateOf(false)
    }
    val modelState by homeVM.modelDialogState.collectAsState()
    val listState = rememberLazyListState()

    val regenerateOutput = remember {
        {
            chatVM.regenerateResponse()
        }
    }
    var showExitDialog by remember {
        mutableStateOf(false)
    }
    BackHandler {
        showExitDialog = true
    }
    LaunchedEffect(key1 = uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) listState.animateScrollToItem(uiState.messages.size)
    }
    Scaffold(
            topBar = {
                ChatTopBar(modelName = homeVM.currModel.generalName, onSelectModel = {
                    homeVM.getModels()
                    showModelSelectionDialog = true
                }) {
                    showExitDialog = true
                }
            },
            bottomBar = {
                PromptField(
                        onSend = { message, images, participant ->
                            chatVM.sendMessage(
                                    NormalHistoryMsg(
                                            text = message,
                                            imageBitmaps = images,
                                            participant = participant,
                                            model = homeVM.currModel.generalName,
                                            botImage = homeVM.currModel.image,
                                    )
                            )
                        },
                        loading = chatVM.isLoading,
                        modifier = Modifier.imePadding()
                )
            }
    ) { paddingValues ->
        if (chatVM.screenLoading) {
            Loader()
        } else {
            LazyColumn(
                    state = listState,
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(uiState.messages, key = { _, item ->
                    item.id
                }) { idx, message ->
                    MessageBubble(
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
    }
    if (showModelSelectionDialog) {
        SelectModelDialogBox(modelsState = modelState, onModelSelect = { newModel ->
            chatVM.changeModel(newModel, homeVM)
            showModelSelectionDialog = false
        }) {
            showModelSelectionDialog = false
        }
    }
    if (showExitDialog) {
        ExitDialog(
                title = "Exit",
                text = "Do you Want to exit the conversation",
                confirm = backHandler,
                dismiss = {
                    showExitDialog = false
                }
        )
    }
}

fun getBotImage2(model: String): Int? {
    return if (model.lowercase().contains("buddy")) {
        R.drawable.logo
    } else if (model.isGeminiModel()) R.drawable.gemini
    else if (model.isClaudeModel()) R.drawable.claude
    else if (model.isGptModel()) R.drawable.gpt
    else null
}


@Composable
fun PromptedImages(imageUri: Bitmap) {
    AsyncImage(model = imageUri, contentDescription = "IMage", modifier = Modifier.height(100.dp))
}

fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}