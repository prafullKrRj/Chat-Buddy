package com.prafull.chatbuddy.mainApp.historyscreen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = koinViewModel(), navController: NavController
) {
    val state by historyViewModel.historyState.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        HistoryAppBar()
    }) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            if (state.loading) {
                item {
                    Loader()
                }
            }
            if (state.error.first) {
                item {
                    ErrorScreen(viewModel = historyViewModel, state.error.second)
                }
            }
            items(state.history.modelsHistory) {
                Text(text = state.history.modelsHistory.first().messages.map {
                    it.text
                }.toString())
            }
            items(state.history.normalHistory) {
                Text(text = state.history.normalHistory.first().messages.map {
                    it.text
                }.toString())
            }
            items(state.history.promptLibHistory) {
                Text(text = state.history.promptLibHistory.first().messages.map {
                    it.text
                }.toString())
            }
        }
    }
}

@Composable
fun HistoryItems(modifier: Modifier = Modifier) {

}
@Composable
fun Loader(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(viewModel: HistoryViewModel, second: String) {
    Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = second)
        Button(onClick = { viewModel.getHistory() }) {
            Text(text = "Retry")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryAppBar() {
    TopAppBar(title = {
        Text(text = "History")
    })
}