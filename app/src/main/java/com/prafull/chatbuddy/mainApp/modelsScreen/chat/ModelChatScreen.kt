package com.prafull.chatbuddy.mainApp.modelsScreen.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.goBackStack
import com.prafull.chatbuddy.mainApp.home.ui.components.MessageBubble
import com.prafull.chatbuddy.mainApp.home.ui.components.PromptField

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
        LazyColumn(
                state = listState,
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
        ) {
            items(state.messages, key = {
                it.id
            }) { message ->
                MessageBubble(message = message, mA = mA, clipboardManager)
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
