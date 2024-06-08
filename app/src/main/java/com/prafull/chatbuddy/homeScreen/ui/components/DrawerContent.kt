package com.prafull.chatbuddy.homeScreen.ui.components

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
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.homeScreen.models.ChatHistory
import com.prafull.chatbuddy.homeScreen.ui.viewmodels.HomeViewModel
import com.prafull.chatbuddy.utils.Response

@Composable
fun DrawerContent(
    previousChats: Response<List<ChatHistory>>,
    homeViewModel: HomeViewModel
) {
    ModalDrawerSheet(
            Modifier.padding(end = 100.dp),
    ) {
        when (previousChats) {
            is Response.Initial -> {
                Button(onClick = { homeViewModel.getPreviousChats() }) {
                    Text("Load Chats")
                }
            }

            is Response.Success -> {
                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                    item {

                    }
                    item {
                        NavigationDrawerItem(label = {
                            Row(
                                    Modifier.fillMaxWidth(),
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
                        }, selected = false, onClick = { /*TODO*/ })
                    }
                    item {
                        NavigationDrawerItem(label = {
                            Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = CenterVertically
                            ) {
                                Icon(
                                        painter = painterResource(id = R.drawable.baseline_explore_24),
                                        contentDescription = stringResource(R.string.explore_models)
                                )
                                Spacer(modifier = Modifier.padding(8.dp))
                                Text("Models")
                            }
                        }, selected = false, onClick = { /*TODO*/ })
                    }
                    item {
                        HorizontalDivider(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                        )
                    }
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
                        NavigationDrawerItem(label = {
                            Text(
                                    text = chatHistory.messages.first().text,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                            )
                        }, selected = false, onClick = { /*TODO*/ })
                    }
                }
            }

            is Response.Error -> {
                Button(onClick = { homeViewModel.getPreviousChats() }) {
                    Text("Retry")
                }
            }
        }
    }
}