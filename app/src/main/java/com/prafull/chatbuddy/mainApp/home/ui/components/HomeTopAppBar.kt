package com.prafull.chatbuddy.mainApp.home.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.HomeViewModel
import com.prafull.chatbuddy.navigateAndPopBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    homeViewModel: HomeViewModel,
    chatViewModel: ChatViewModel,
    navController: NavController,
    navigationIconClicked: () -> Unit,
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
                        Text(text = "Models ${homeViewModel.currModel}")
                    })
                }
            },
            actions = {
                IconButton(onClick = {
                    navController.navigateAndPopBackStack(Routes.Home)
                    if (chatViewModel.chatting) chatViewModel.loadNewChat()
                }) {
                    Text(text = "\uD83D\uDD8A")
                }
                Spacer(modifier = Modifier.width(4.dp))
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
            },
            navigationIcon = {
                IconButton(onClick = navigationIconClicked) {
                    Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Navigation Drawer Icon",
                    )
                }
            },
    )
}