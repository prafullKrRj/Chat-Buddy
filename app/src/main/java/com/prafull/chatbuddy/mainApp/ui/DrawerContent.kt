package com.prafull.chatbuddy.mainApp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.AppScreens
import com.prafull.chatbuddy.R.drawable
import com.prafull.chatbuddy.R.string
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
    mAuth: FirebaseAuth,
    previousChats: Resource<List<ChatHistory>>,
    homeViewModel: HomeViewModel,
    currChatUUID: String,
    navController: NavController,
    chatViewModel: ChatViewModel,
    closeDrawer: () -> Unit,
    onSettingsClicked: () -> Unit,
    scope: CoroutineScope,
    onChatClicked: (ChatHistory) -> Unit,
) {
    ModalDrawerSheet(
            Modifier.padding(end = 100.dp),
    ) {
        Box(Modifier.fillMaxHeight()) {
            Column {
                NavigationDrawerItem(
                        label = {
                            Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = CenterVertically
                            ) {
                                Icon(
                                        painter = painterResource(id = drawable.sharp_chat_bubble_outline_24),
                                        contentDescription = stringResource(
                                                string.prompt_library_access_button
                                        )
                                )
                                Spacer(modifier = Modifier.padding(8.dp))
                                Text("Chat Buddy")
                            }
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                navController.navigateIfNotCurrent(
                                        AppScreens.HOME.name,
                                        chatViewModel
                                )
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
                                        painter = painterResource(id = drawable.outline_apps_24),
                                        contentDescription = stringResource(
                                                string.prompt_library_access_button
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
                                        painter = painterResource(id = drawable.baseline_explore_24),
                                        contentDescription = stringResource(string.explore_models)
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
                                                    painter = painterResource(id = drawable.sharp_chat_bubble_outline_24),
                                                    contentDescription = stringResource(string.no_recent_chats)
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
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                        label = {
                            Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = CenterVertically) {
                                    UserImage(modifier = Modifier, firebaseAuth = mAuth)
                                    Spacer(modifier = Modifier.padding(8.dp))
                                    Text(mAuth.currentUser?.displayName ?: "User")
                                }
                                Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = null,
                                        modifier = Modifier.rotate(90f)
                                )
                            }
                        },
                        selected = false,
                        onClick = onSettingsClicked,
                        shape = RectangleShape
                )
            }
        }
    }
}