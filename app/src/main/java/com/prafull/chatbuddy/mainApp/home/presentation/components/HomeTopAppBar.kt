package com.prafull.chatbuddy.mainApp.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen.getBotImage2
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel

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