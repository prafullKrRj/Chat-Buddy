package com.prafull.chatbuddy.mainApp.home.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    homeViewModel: HomeViewModel,
    chatViewModel: ChatViewModel,
    navController: NavController,
    navigationIconClicked: () -> Unit,
) {
    /*
    val coins by homeViewModel.coins.collectAsState()
    //  val currentModel by chatViewModel.currentModel.collectAsState()
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
//Text(text = currentModel.generalName)
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
    )*/
}