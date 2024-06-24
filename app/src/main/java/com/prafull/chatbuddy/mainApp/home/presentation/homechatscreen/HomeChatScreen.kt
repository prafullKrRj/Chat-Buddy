package com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.common.components.BotImage
import com.prafull.chatbuddy.mainApp.common.components.ChatTopBar
import com.prafull.chatbuddy.mainApp.common.components.FormattedText
import com.prafull.chatbuddy.mainApp.common.components.SelectModelDialogBox
import com.prafull.chatbuddy.mainApp.common.components.UserImage
import com.prafull.chatbuddy.mainApp.common.model.Participant
import com.prafull.chatbuddy.mainApp.common.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.common.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.common.model.isGptModel
import com.prafull.chatbuddy.mainApp.home.models.NormalHistoryMsg
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.NewHomePromptField
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.ExitDialog
import com.prafull.chatbuddy.utils.UriSaver

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
                NewHomePromptField(
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
        LazyColumn(
                state = listState,
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                modifier = Modifier.fillMaxSize()
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
fun ChatScreenMessageBubble(
    modifier: Modifier,
    participant: String,
    message: Pair<String, List<Bitmap?>>,
    botImage1: String?,
    @DrawableRes botImage2: Int?,
    mA: FirebaseAuth,
    isLast: Boolean,
    isSecondLast: Boolean,
    clipboardManager: ClipboardManager,
    model: String,
    regenerateOutput: () -> Unit
) {
    var isEditingPrompt by rememberSaveable { mutableStateOf(false) }
    var editingPrompt by rememberSaveable { mutableStateOf("") }
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
            imageUri?.let { imageUris.add(it) }
        }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    LaunchedEffect(imageUris.size) {
        if (imageUris.isNotEmpty()) listState.animateScrollToItem(imageUris.size)
    }
    if (participant == Participant.USER.name) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            UserImage(Modifier.size(24.dp), firebaseAuth = mA)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = mA.currentUser?.displayName ?: "User")
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                ) {
                    if (!isEditingPrompt) {
                        LazyRow(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .background(Color.Transparent)
                        ) {
                            items(message.second) { image ->
                                if (image != null) {
                                    PromptedImages(imageUri = image)
                                }
                            }
                        }
                        FormattedText(
                                text = message.first,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(top = 8.dp)
                        )
                        HomeChatMessageFunctions(
                                message = message,
                                participant = participant,
                                context = context,
                                clipboardManager = clipboardManager,
                                isSecondLast = isSecondLast,
                                isLast = isLast,
                                editPrompt = {
                                    isEditingPrompt = true
                                }
                        )
                    } else {
                        ImagePickerRow(imageUris, pickMedia, listState)
                        OutlinedTextField(
                                value = editingPrompt,
                                onValueChange = {
                                    editingPrompt = it
                                },
                                modifier = Modifier.padding(8.dp),
                                trailingIcon = {
                                    IconButton(onClick = {

                                    }) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Default.Send,
                                                contentDescription = "Send"
                                        )
                                    }
                                },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        pickMedia.launch(
                                                PickVisualMediaRequest(
                                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                        )
                                    }) {
                                        Icon(
                                                painter = painterResource(id = R.drawable.baseline_image_24),
                                                contentDescription = "select image"
                                        )
                                    }
                                }
                        )
                    }
                }
            }
        }
    } else {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (botImage2 == null) {
                BotImage(modifier = Modifier.size(24.dp), data = botImage1)
            } else {
                BotImage(modifier = Modifier.size(24.dp), image = botImage2)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = model)
                FormattedText(text = message.first)
                HomeChatMessageFunctions(
                        message = message,
                        participant = participant,
                        context = context,
                        clipboardManager = clipboardManager,
                        isSecondLast = isSecondLast,
                        isLast = isLast,
                        regenerateOutput = regenerateOutput
                )
            }
        }
    }
}

@Composable
fun PromptedImages(imageUri: Bitmap) {
    AsyncImage(model = imageUri, contentDescription = "IMage", modifier = Modifier.height(100.dp))
}

@Composable
fun HomeChatMessageFunctions(
    message: Pair<String, List<Bitmap?>>,
    participant: String,
    context: Context,
    clipboardManager: ClipboardManager,
    isSecondLast: Boolean,
    isLast: Boolean,
    editPrompt: () -> Unit = {},
    regenerateOutput: () -> Unit = {}
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
    ) {
        if (isSecondLast && participant == Participant.USER.name) {
            IconButton(onClick = editPrompt) {
                Icon(
                        painter = painterResource(id = R.drawable.outline_edit_24),
                        contentDescription = "Re Prompt"
                )
            }
        }
        if (isLast && participant == Participant.ASSISTANT.name) {
            IconButton(onClick = regenerateOutput) {
                Icon(
                        painter = painterResource(id = R.drawable.outline_autorenew_24),
                        contentDescription = "Regenerate"
                )
            }
        }
        IconButton(onClick = {
            clipboardManager.setText(AnnotatedString(message.first))
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }) {
            Icon(
                    painter = painterResource(id = R.drawable.baseline_content_copy_24),
                    contentDescription = "Copy",
                    Modifier.size(18.dp)
            )
        }
        IconButton(onClick = {
            shareText(context = context, text = message.first)
        }) {
            Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    Modifier.size(18.dp)
            )
        }
    }
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

@Composable
fun ImagePickerRow(
    imageUris: MutableList<Uri>,
    pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    listState: LazyListState
) {
    LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            state = listState
    ) {
        items(imageUris) { imageUri ->
            SelectedImage(imageUri = imageUri) { imageUris.remove(it) }
        }
        if (imageUris.isNotEmpty()) {
            item {
                FilledTonalIconButton(onClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Image")
                }
            }
        }
    }
}

@Composable
fun SelectedImage(imageUri: Uri, removeImage: (Uri) -> Unit) {
    Box(
            modifier = Modifier
                .padding(4.dp)
                .requiredSize(100.dp)
    ) {
        AsyncImage(
                model = imageUri,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.padding(4.dp)
        )
        IconButton(
                onClick = { removeImage(imageUri) },
                modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Remove image",
                    tint = Color.Red
            )
        }
    }
}