package com.prafull.chatbuddy.homeScreen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.homeScreen.ui.components.AdWindow
import com.prafull.chatbuddy.homeScreen.ui.components.BotImage
import com.prafull.chatbuddy.homeScreen.ui.components.PromptField
import com.prafull.chatbuddy.homeScreen.ui.components.UserImage
import com.prafull.chatbuddy.utils.UriSaver
import com.prafull.chatbuddy.utils.toBitmaps
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@Composable
fun HomeScreen() {
    val mA = FirebaseAuth.getInstance()
    val viewModel: ChatViewModel = getViewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val state = viewModel.uiState.collectAsState()
    val keyBoardManager = LocalSoftwareKeyboardController.current
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }

    var isChatting by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(state) {
        if (state.value.messages.isNotEmpty()) listState.scrollToItem(state.value.messages.lastIndex)
    }

    Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                        top = WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateTopPadding(),
                        bottom = WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                ),
            contentAlignment = Alignment.Center
    ) {
        if (!isChatting) {
            Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
            ) {
                AdWindow()
            }
        } else {
            LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopCenter),
                    userScrollEnabled = true,
                    state = listState
            ) {
                items(state.value.messages) { message ->
                    Row (Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = CenterVertically){

                        if (message.participant == Participant.USER) {
                            UserImage(Modifier.weight(.05f), firebaseAuth = mA)
                        } else {
                            BotImage(Modifier.weight(.05f))
                        }

                        Column(Modifier.weight(.9f)) {
                            LazyRow(
                                    modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                items(message.imageUri) { imageUri ->
                                    AsyncImage(
                                            model = imageUri,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .requiredWidth(72.dp)
                                                .clickable {

                                                }
                                    )
                                }
                            }
                            Text(
                                    text = message.text,
                                    modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                }
            }
        }
        Column(Modifier.align(Alignment.BottomCenter)) {
            LazyRow(
                    modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items(imageUris) { imageUri ->
                    AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .requiredSize(72.dp)
                    )
                }
            }
            PromptField(imageUris = {
                imageUris.add(it)
            }) { prompt ->
                isChatting = true
                keyBoardManager?.hide()
                coroutineScope.launch {
                    val bitmaps = imageUris.mapNotNull {
                        it.toBitmaps(context)
                    }
                    viewModel.sendMessage(prompt, bitmaps)
                    imageUris.clear()
                }
            }
        }
    }
}
