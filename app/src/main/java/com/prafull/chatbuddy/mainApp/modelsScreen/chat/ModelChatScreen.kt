package com.prafull.chatbuddy.mainApp.modelsScreen.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.goBackStack
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.ui.components.MessageBubble
import com.prafull.chatbuddy.mainApp.home.ui.components.PromptField
import com.prafull.chatbuddy.model.Model

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelChatScreen(viewModel: ModelsChatVM, navController: NavController) {
    val state by viewModel.uiState.collectAsState()
    val mA = FirebaseAuth.getInstance()
    val clipboardManager = LocalClipboardManager.current
    Text(text = viewModel.currModel.generalName)
    Text(text = state.messages.toString())
    val showBackDialog = remember {
        mutableStateOf(false)
    }
    val navigate = remember {
        {
            navController.goBackStack()
        }
    }
    BackHandler {
        showBackDialog.value = true
    }
    val listState = rememberLazyListState()
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size)
        }
    }
    val itemsWithAds = mutableListOf<Any>()
    state.messages.forEachIndexed { index, message ->
        itemsWithAds.add(message)
        if ((index + 1) % 6 == 0) { // Add an ad after every 6 messages
            itemsWithAds.add("Ad")
        }
    }
    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Text(text = viewModel.currModel.generalName)
                }, navigationIcon = {
                    IconButton(onClick = {
                        showBackDialog.value = true
                    }) {
                        Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back"
                        )
                    }
                })
            },
            bottomBar = {
                PromptField(viewModel = viewModel)
            }
    ) { paddingValues ->
        if (state.messages.isEmpty()) {
            InitialChatUI(modifier = Modifier.padding(paddingValues), model = viewModel.currModel)
        }
        if (viewModel.historyLoading) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
        }
        if (viewModel.historyError) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {
                    viewModel.updateChat()
                }) {
                    Text(text = "Retry")
                }
            }
        }
        LazyColumn(
                state = listState,
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
        ) {
            items(itemsWithAds, key = { item ->
                if (item == "Ad") {
                    "Ad"
                } else {
                    (item as ChatMessage).id
                }
            }) { item ->
                when (item) {
                    is ChatMessage -> MessageBubble(message = item, mA = mA, clipboardManager)
                    // "Ad" -> BannerAd()
                }
            }
        }
    }
    if (showBackDialog.value) {
        AlertDialog(
                onDismissRequest = {
                    showBackDialog.value = false
                },
                title = { Text(text = "Exit") },
                text = { Text(text = "Do you want to exit from the conversation!") },
                confirmButton = {
                    TextButton(onClick = {
                        showBackDialog.value = false
                        navigate()
                    }) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = { showBackDialog.value = false }) { Text("Dismiss") }
                }
        )
    }
}

@Composable
fun InitialChatUI(modifier: Modifier, model: Model) {
    val context = LocalContext.current
    Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
                model = ImageRequest.Builder(context).data(model.image).build(),
                contentDescription = "image",
                modifier = Modifier.width(150.dp)
        )
        Text(text = model.generalName, fontWeight = SemiBold, fontSize = 20.sp)
    }
}