package com.prafull.chatbuddy.mainApp.promptlibrary.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem


@Composable
fun PromptCard(promptType: PromptLibraryItem) {
    Card(modifier = Modifier.padding(8.dp)) {
        Text(
                text = promptType.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
        )

        Text(
                text = "Description: ${promptType.description}",
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
        )
    }
}