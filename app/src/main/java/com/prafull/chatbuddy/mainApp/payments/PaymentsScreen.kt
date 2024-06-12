package com.prafull.chatbuddy.mainApp.payments

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.prafull.chatbuddy.goBackStack

@Composable
fun PaymentsScreen(navController: NavController) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    BackHandler {
        showDialog = true
    }
    Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Payments Screen")
    }
    if (showDialog) {
        ExitDialog(onDismiss = { showDialog = false }) {
            showDialog = false
            navController.goBackStack()
        }

    }
}

@Composable
private fun ExitDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = onConfirm) {
            Text(text = "Yes")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = "No")
        }
    }, text = {
        Text(text = "Are you sure you want to exit payments?")
    })
}