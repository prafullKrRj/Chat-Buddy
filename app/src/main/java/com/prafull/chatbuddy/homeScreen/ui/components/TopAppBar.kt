package com.prafull.chatbuddy.homeScreen.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.prafull.chatbuddy.homeScreen.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(viewModel: HomeViewModel, navigationIconClicked: () -> Unit) {
    val coins by viewModel.coins.collectAsState()
    TopAppBar(
            title = {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                ) {
                    ElevatedAssistChip(onClick = { /*TODO*/ }, label = {
                        Text(text = "Models")
                    })
                }
            },
            actions = {
                IconButton(onClick = {

                }) {
                    Text(text = "\uD83D\uDD8A")
                }
                Spacer(modifier = Modifier.width(4.dp))
                ElevatedAssistChip(
                        onClick = {

                        },
                        label = {
                            Text(text = "✨ $coins")
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