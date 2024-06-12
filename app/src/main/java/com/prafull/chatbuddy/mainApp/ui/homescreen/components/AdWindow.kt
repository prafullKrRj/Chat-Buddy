package com.prafull.chatbuddy.mainApp.ui.homescreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafull.chatbuddy.mainApp.ui.viewmodels.HomeViewModel
import com.prafull.chatbuddy.ui.theme.gold

@Composable
fun AdWindow(viewModel: HomeViewModel, watchAd: () -> Unit) {
    val buttonEnabled by viewModel.adButtonEnabled.collectAsState()
    Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        ) {
            Column(
                    modifier = Modifier
                        .weight(.8f)
            ) {
                Text(text = "Watch Video Ad", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Watch a video ad to earn 2000 tokens!")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                        onClick = {
                            watchAd()
                            viewModel.updateAdButtonState(false)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = gold),
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = buttonEnabled
                ) {
                    Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Watch Video Ad"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Watch Ad", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Box(modifier = Modifier.weight(.2f))
        }
    }
}

