package com.prafull.chatbuddy.mainApp.home.presentation.homescreen

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.MainActivity
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.mainApp.ads.BannerAd
import com.prafull.chatbuddy.mainApp.ads.rewardedAds
import com.prafull.chatbuddy.mainApp.common.components.PremiumPlanComp
import com.prafull.chatbuddy.mainApp.common.components.SelectModelDialogBox
import com.prafull.chatbuddy.mainApp.common.model.Model
import com.prafull.chatbuddy.mainApp.common.model.Participant
import com.prafull.chatbuddy.mainApp.home.models.NormalHistoryMsg
import com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen.SelectedImage
import com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen.getBotImage2
import com.prafull.chatbuddy.ui.theme.gold
import com.prafull.chatbuddy.utils.UriSaver
import com.prafull.chatbuddy.utils.toBitmaps
import kotlinx.coroutines.launch

@Composable
fun NewHomeScreen(
    viewModel: HomeViewModel, navController: NavController
) {
    val mA = FirebaseAuth.getInstance()
    val modelsState by viewModel.modelDialogState.collectAsState()
    val selectedModel = remember<(Model) -> Unit> {
        { model ->
            viewModel.modelButtonClicked = false
            viewModel.currModel = model
        }
    }

    if (viewModel.modelButtonClicked) {
        SelectModelDialogBox(modelsState = modelsState,
                onModelSelect = selectedModel,
                onDismissRequest = {
                    viewModel.modelButtonClicked = false
                })
    }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .imePadding(), topBar = {
        NewHomeTopAppBar(homeViewModel = viewModel)
    }, bottomBar = {
        NewHomePromptField(onSend = { message, images, participant ->
            viewModel.updateNewPrompt(
                    NormalHistoryMsg(
                            text = message,
                            imageBitmaps = images,
                            participant = participant,
                    )
            )
            navController.navigate(Routes.HomeChatScreen)
        })
    }) { paddingValues ->
        LazyColumn(
                modifier = Modifier,
                contentPadding = paddingValues,
                userScrollEnabled = true,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "ad") {
                BannerAd()
                NewAdWindow(viewModel) {
                    viewModel.adButtonEnabled = false
                    rewardedAds(context as MainActivity, failed = {
                        viewModel.adButtonEnabled = true
                    }) {
                        viewModel.adWatched()
                        viewModel.adButtonEnabled = true
                    }
                }
            }
            item("premium") {
                PremiumPlanComp {
                    navController.navigate(Routes.PaymentsScreen)
                }
            }
        }
    }
}


@Composable
fun NewHomePromptField(
    modifier: Modifier = Modifier,
    onSend: (String, List<Bitmap?>, String) -> Unit,
    loading: Boolean = false,
) {
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let { imageUris.add(it) }
    }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var prompt by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(imageUris.size) {
        if (imageUris.isNotEmpty()) listState.animateScrollToItem(imageUris.size)
    }
    ElevatedCard(
            modifier = modifier,
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
        MessageInputRow(
                modifier = Modifier,
                prompt = prompt,
                onPromptChange = {
                    prompt = it
                },
                onSend = {
                    scope.launch {
                        val images = imageUris.mapNotNull { it.toBitmaps(context) }
                        onSend(prompt, images, Participant.USER.name)
                        focusManager.clearFocus()
                        prompt = ""
                        imageUris.removeAll(elements = imageUris)
                    }
                },
                onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                loading
        )
    }
}

@Composable
fun MessageInputRow(
    modifier: Modifier,
    prompt: String,
    onPromptChange: (String) -> Unit,
    onSend: () -> Unit,
    onPickImage: () -> Unit,
    isLoading: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        IconButton(onClick = onPickImage) {
            Icon(
                    painter = painterResource(id = R.drawable.baseline_image_24),
                    contentDescription = "Pick Image",
                    Modifier.size(24.dp)
            )
        }
        OutlinedTextField(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(40),
                value = prompt,
                label = {
                    if (!isFocused && prompt.isBlank()) {
                        Text("Message")
                    }
                },
                interactionSource = interactionSource,
                onValueChange = { onPromptChange(it) },
                keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default
                ),
                trailingIcon = {
                    if (prompt.isNotBlank()) {
                        IconButton(
                                onClick = {
                                    onSend()
                                }
                        ) {
                            Icon(
                                    Icons.AutoMirrored.Default.Send,
                                    contentDescription = "send",
                                    modifier = Modifier
                            )
                        }
                    }
                    if (prompt.isBlank() && isLoading) {
                        CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.padding(4.dp)
                        )
                    }
                }
        )
    }
}

@Composable
fun NewAdWindow(viewModel: HomeViewModel, watchAd: () -> Unit) {
    Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        ) {
            Column(
                    modifier = Modifier
                        .weight(.8f)
            ) {
                Text(text = "Watch Video Ad", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Watch a video ad to earn tokens!")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                        onClick = {
                            watchAd()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = gold),
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = viewModel.adButtonEnabled
                ) {
                    Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Watch Video Ad"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Watch Ad", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Box(modifier = Modifier.weight(.2f))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHomeTopAppBar(
    homeViewModel: HomeViewModel
) {
    val coins by homeViewModel.coins.collectAsState()
    TopAppBar(
            title = {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                ) {
                    ElevatedAssistChip(onClick = {
                        homeViewModel.getModels()
                        homeViewModel.modelButtonClicked = true
                    }, label = {
                        Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            getBotImage2(homeViewModel.currModel.generalName)?.let {
                                Image(
                                        painter = painterResource(id = it),
                                        contentDescription = "Bot Image",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(
                                                    CircleShape
                                            )
                                )
                            }
                            Text(text = homeViewModel.currModel.generalName)
                            Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Change Model"
                            )
                        }
                    })
                }
            },
            actions = {
                ElevatedAssistChip(
                        onClick = {

                        },
                        label = {
                            if (coins.initial) {
                                Text(text = "✨")
                                Spacer(modifier = Modifier.width(4.dp))
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Text(text = "✨ ${coins.currCoins}")
                            }
                        },
                )
            }
    )
}