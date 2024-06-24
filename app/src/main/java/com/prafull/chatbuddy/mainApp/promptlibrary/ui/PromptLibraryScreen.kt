package com.prafull.chatbuddy.mainApp.promptlibrary.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafull.chatbuddy.MainActivity
import com.prafull.chatbuddy.mainApp.ModelsAndPromptTopAppBar
import com.prafull.chatbuddy.mainApp.ads.BannerAd
import com.prafull.chatbuddy.mainApp.ads.loadInterstitialAd
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(
        ExperimentalComposeUiApi::class
)
@Composable
fun PromptScreen(
    modifier: Modifier,
    homeVM: HomeViewModel,
    toPromptChat: (PromptLibraryItem) -> Unit
) {
    val promptViewModel: PromptLibraryViewModel = getViewModel()
    val state by promptViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    Scaffold(
            topBar = {
                ModelsAndPromptTopAppBar(title = "Prompt")
            },
            bottomBar = {
                BannerAd()
            }
    ) { paddingValues ->
        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.error != null) {
            Button(onClick = {
                promptViewModel.getPrompts()
            }) {
                Text(text = "Retry")
            }
        } else {
            val searchQuery = remember { mutableStateOf("") }
            val tabs = listOf("Personal Prompts", "Business Prompts")
            val focusManager = LocalFocusManager.current
            val pagerState = rememberPagerState(
                    pageCount = { tabs.size }, initialPage = 0
            )
            var selectedPrompt by remember {
                mutableStateOf(PromptLibraryItem("", "", ""))
            }
            val context = LocalContext.current
            var showPromptDialog by remember { mutableStateOf(false) }
            val activity = context as MainActivity
            Column(
                    Modifier
                        .padding(paddingValues)
                        .pointerInteropFilter {
                            focusManager.clearFocus()
                            false
                        }) {
                OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        label = { Text("Search") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.outlineVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(30)
                )
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(title) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                })
                    }
                }
                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> {
                            DisplayPrompts(state.personalPrompts, searchQuery.value, modifier) {
                                scope.launch {
                                    selectedPrompt = it
                                    showPromptDialog = true
                                }
                            }
                        }

                        1 -> {
                            DisplayPrompts(state.businessPrompts, searchQuery.value, modifier) {
                                scope.launch {
                                    selectedPrompt = it
                                    showPromptDialog = true
                                }
                            }
                        }
                    }
                }
                BannerAd()
            }
            if (showPromptDialog) {
                var isLoading by remember {
                    mutableStateOf(false)
                }
                if (isLoading) {
                    CircularProgressIndicator()
                }
                DialogContent(promptLibraryItem = selectedPrompt,
                        onDismiss = { showPromptDialog = false },
                        confirmButton = { promptLibraryItem ->
                            isLoading = true
                            scope.launch {
                                loadInterstitialAd(context, activity, onAdFailedToLoad = {
                                    isLoading = false
                                    showPromptDialog = false
                                    toPromptChat(promptLibraryItem)
                                }, onAdLoaded = {
                                    toPromptChat(promptLibraryItem)
                                    isLoading = false
                                    showPromptDialog = false
                                })
                            }
                        }
                )
            }
        }
    }
}


@Composable
fun DialogContent(
    modifier: Modifier = Modifier,
    promptLibraryItem: PromptLibraryItem,
    onDismiss: () -> Unit,
    confirmButton: (PromptLibraryItem) -> Unit
) {
    val sheetScroll = rememberScrollState()
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = { confirmButton(promptLibraryItem) }) {
            Text(text = "Go to Chat")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }, text = {
        Column(
                modifier = modifier
                    .padding(12.dp)
                    .verticalScroll(sheetScroll)
        ) {
            Text(
                    text = promptLibraryItem.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Description", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Text(text = promptLibraryItem.description)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "User Prompt example", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Text(text = promptLibraryItem.user)
        }
    })
}

@Composable
fun DisplayPrompts(
    prompts: List<PromptLibraryItem>,
    searchQuery: String,
    modifier: Modifier,
    openPromptExampleSheet: (PromptLibraryItem) -> Unit = {}
) {
    val filteredPrompts = prompts.filter {
        it.name.contains(
                searchQuery, ignoreCase = true
        ) || it.description.contains(searchQuery, ignoreCase = true) || it.system.contains(
                searchQuery,
                ignoreCase = true
        )
    }
    LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            contentPadding = PaddingValues(12.dp)
    ) {
        items(filteredPrompts.chunked(3)) { chunks ->
            LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chunks, key = {
                    it.name
                }) { item ->
                    Card(modifier = Modifier
                        .width(250.dp)
                        .height(100.dp)
                        .clickable {
                            openPromptExampleSheet(item)
                        }) {
                        val verticalScrollState = rememberScrollState()
                        Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .verticalScroll(verticalScrollState),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                    text = item.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp
                            )
                            Text(text = item.description)
                        }
                    }
                }
            }
        }
    }
}
