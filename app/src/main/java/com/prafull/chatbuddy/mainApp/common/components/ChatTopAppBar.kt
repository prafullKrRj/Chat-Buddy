package com.prafull.chatbuddy.mainApp.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen.getBotImage2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(modelName: String, onSelectModel: () -> Unit, onBackButtonClicked: () -> Unit) {
    CenterAlignedTopAppBar(title = {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
        ) {
            ElevatedAssistChip(onClick = onSelectModel, label = {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    getBotImage2(modelName)?.let {
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
                    Text(text = modelName)
                    Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change Model"
                    )
                }
            })
        }
    }, navigationIcon = {
        IconButton(onClick = onBackButtonClicked) {
            Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
            )
        }

    })
}