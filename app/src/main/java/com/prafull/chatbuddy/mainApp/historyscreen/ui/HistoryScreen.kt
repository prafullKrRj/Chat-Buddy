package com.prafull.chatbuddy.mainApp.historyscreen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.mainApp.common.data.repos.UserHistory
import com.prafull.chatbuddy.utils.Const
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

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
            if (state.history.history.isEmpty()) {
                item {
                    EmptyHistoryScreen()
                }
            }
            items(state.history.history, key = { UUID.randomUUID() }) { history ->
                HistoryItems(history, navController, historyViewModel)
            }
        }
    }
}

@Composable
fun EmptyHistoryScreen(modifier: Modifier = Modifier) {
    Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "\uD83D\uDE0A No history", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
    }
}

@Composable
fun HistoryItems(
    history: UserHistory,
    navController: NavController,
    historyViewModel: HistoryViewModel
) {
    Card(
            Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        Column(
                Modifier
                    .fillMaxSize()
                    .clickable {
                        when (history.promptType) {
                            Const.NORMAL_HISTORY -> {
                                navController.navigate(Routes.HomeChatScreen(history.id))
                            }

                            Const.MODELS_HISTORY -> {
                                navController.navigate(Routes.ModelChatScreen(history.id))
                            }

                            Const.LIBRARY_HISTORY -> {}
                            Const.CHARACTER_HISTORY -> {
                                navController.navigate(Routes.ModelChatScreen(history.id))
                            }
                        }
                    }
                    .padding(12.dp)
        ) {
            if (history.title.isNotEmpty()) {
                Row {
                    Text(
                            text = history.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(.9f)
                    )
                }
            }
            Text(text = if (history.firstPrompt.length > 300) history.firstPrompt.substring(300) else history.firstPrompt)
            Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                Text(formatTimestamp(history.timestamp.toDate().time))
                IconButton(onClick = {
                    historyViewModel.deleteChat(history.id, history.promptType)
                }) {
                    Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete history composable"
                    )
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val yesterdayStart = todayStart - 24 * 60 * 60 * 1000

    return when {
        timestamp >= todayStart -> {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
        }

        timestamp >= yesterdayStart -> "Yesterday"
        else -> {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }
}

@Composable
fun Loader() {
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