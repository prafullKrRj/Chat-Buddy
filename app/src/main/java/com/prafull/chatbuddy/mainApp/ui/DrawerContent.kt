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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.AppScreens
import com.prafull.chatbuddy.R.drawable
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.ui.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.HomeViewModel
import com.prafull.chatbuddy.navigateAndPopBackStack
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
    ModalDrawerSheet(Modifier.padding(end = 100.dp)) {
        Box(Modifier.fillMaxHeight()) {
            Column {
                DrawerItem(
                        label = "Chat Buddy",
                        iconRes = drawable.sharp_chat_bubble_outline_24,
                        onClick = {
                            scope.launch {
                                navController.navigateAndPopBackStack(AppScreens.HOME.name)
                                chatViewModel.loadNewChat()
                                delay(250L)
                                closeDrawer()
                            }
                        }
                )
                DrawerItem(
                        label = "Prompt Library",
                        iconRes = drawable.outline_apps_24,
                        selected = navController.currentDestination?.route == AppScreens.PROMPT.name,
                        onClick = {
                            scope.launch {
                                navController.navigateAndPopBackStack(AppScreens.PROMPT.name)
                                delay(100L)
                                closeDrawer()
                            }
                        }
                )
                DrawerItem(
                        label = "Models",
                        iconRes = drawable.baseline_explore_24,
                        selected = navController.currentDestination?.route == AppScreens.MODELS.name,
                        onClick = {
                            scope.launch {
                                navController.navigateAndPopBackStack(AppScreens.MODELS.name)
                                delay(250L)
                                closeDrawer()
                            }
                        }
                )
                HorizontalDivider(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                )
                ChatHistorySection(
                        previousChats,
                        homeViewModel,
                        chatViewModel,
                        currChatUUID,
                        onChatClicked
                )
            }
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                Spacer(modifier = Modifier.height(8.dp))
                DrawerUserItem(mAuth, onSettingsClicked)
            }
        }
    }
}

@Composable
fun DrawerItem(
    label: String,
    iconRes: Int,
    selected: Boolean = false,
    onClick: () -> Unit
) {

    NavigationDrawerItem(
            label = {
                Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = label
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(label)
                }
            },
            selected = selected,
            onClick = onClick,
    )
}

@Composable
fun ChatHistorySection(
    previousChats: Resource<List<ChatHistory>>,
    homeViewModel: HomeViewModel,
    chatViewModel: ChatViewModel,
    currChatUUID: String,
    onChatClicked: (ChatHistory) -> Unit
) {
    when (previousChats) {
        is Resource.Initial -> {
            Button(onClick = { homeViewModel.getPreviousChats() }) {
                Text("Load Chats")
            }
        }

        is Resource.Success -> {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                if (previousChats.data.isEmpty()) {
                    item {
                        DrawerItem(
                                label = "No recent chats",
                                iconRes = drawable.sharp_chat_bubble_outline_24,
                                onClick = { /*TODO*/ })
                    }
                } else {
                    items(previousChats.data, key = { it.id }) { chatHistory ->
                        NavigationDrawerItem(
                                label = {
                                    Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                                text = chatHistory.messages.first().text,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(.9f)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(onClick = {
                                            if (chatHistory.id == currChatUUID) {
                                                chatViewModel.loadNewChat()
                                            }
                                            homeViewModel.deleteChat(chatHistory.id)
                                        }, modifier = Modifier.weight(.1f)) {
                                            Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = "Delete Chat"
                                            )
                                        }
                                    }
                                },
                                selected = chatHistory.id == currChatUUID,
                                onClick = { onChatClicked(chatHistory) }
                        )
                    }
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

@Composable
fun DrawerUserItem(
    mAuth: FirebaseAuth,
    onSettingsClicked: () -> Unit
) {
    NavigationDrawerItem(
            label = {
                Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserImage(modifier = Modifier, firebaseAuth = mAuth)
                        Spacer(modifier = Modifier.width(8.dp))
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