package com.prafull.chatbuddy.mainApp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafull.chatbuddy.AppScreens
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.HomeViewModel
import com.prafull.chatbuddy.navigateAndPopBackStack
import com.prafull.chatbuddy.navigateIfNotCurrent
import com.prafull.chatbuddy.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    previousChats: Resource<List<ChatHistory>>,
    homeViewModel: HomeViewModel,
    currChatUUID: String,
    navController: NavController,
    chatViewModel: ChatViewModel,
    closeDrawer: () -> Unit,
    scope: CoroutineScope,
    onChatClicked: (ChatHistory) -> Unit,
) {
    ModalDrawerSheet(
            Modifier.padding(end = 100.dp),
    ) {

        NavigationDrawerItem(
                label = {
                    Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = CenterVertically
                    ) {
                        Icon(
                                painter = painterResource(id = R.drawable.sharp_chat_bubble_outline_24),
                                contentDescription = stringResource(
                                        R.string.prompt_library_access_button
                                )
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text("Chat Buddy")
                    }
                },
                selected = false,
                onClick = {
                    scope.launch {
                        navController.navigateIfNotCurrent(AppScreens.HOME.name, chatViewModel)
                        delay(250L)
                        closeDrawer()
                    }
                })

        NavigationDrawerItem(
                label = {
                    Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = CenterVertically
                    ) {
                        Icon(
                                painter = painterResource(id = R.drawable.outline_apps_24),
                                contentDescription = stringResource(
                                        R.string.prompt_library_access_button
                                )
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text("Prompt Library")
                    }
                },
                selected = navController.currentDestination?.route == AppScreens.PROMPT.name,
                onClick = {
                    scope.launch {
                        navController.navigateAndPopBackStack(AppScreens.PROMPT.name)
                        delay(100L)
                        closeDrawer()
                    }
                })

        NavigationDrawerItem(
                label = {
                    Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = CenterVertically
                    ) {
                        Icon(
                                painter = painterResource(id = R.drawable.baseline_explore_24),
                                contentDescription = stringResource(R.string.explore_models)
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text("Models")
                    }
                },
                selected = navController.currentDestination?.route == AppScreens.MODELS.name,
                onClick = {
                    scope.launch {
                        navController.navigateAndPopBackStack(AppScreens.MODELS.name)
                        delay(250L)
                        closeDrawer()
                    }
                })

        HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
        )

        when (previousChats) {
            is Resource.Initial -> {
                Button(onClick = { homeViewModel.getPreviousChats() }) {
                    Text("Load Chats")
                }
            }

            is Resource.Success -> {
                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                    item {
                        if (previousChats.data.isEmpty()) {
                            NavigationDrawerItem(label = {
                                Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = CenterVertically
                                ) {
                                    Icon(
                                            painter = painterResource(id = R.drawable.sharp_chat_bubble_outline_24),
                                            contentDescription = stringResource(R.string.no_recent_chats)
                                    )
                                    Spacer(modifier = Modifier.padding(8.dp))
                                    Text("No recent chats")
                                }
                            }, selected = false, onClick = { /*TODO*/ })
                        }
                    }
                    items(previousChats.data) { chatHistory ->
                        NavigationDrawerItem(
                                label = {
                                    Text(
                                            text = chatHistory.messages.first().text,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                    )
                                },
                                selected = chatHistory.id == currChatUUID,
                                onClick = {
                                    onChatClicked(chatHistory)
                                }
                        )
                    }
                }
            }

            is Resource.Error -> {
                Button(onClick = { homeViewModel.getPreviousChats() }) {
                    Text("Retry")
                }
            }
        }
    }
}