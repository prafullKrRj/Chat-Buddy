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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafull.chatbuddy.R


/**
 *  Premium Plan Component
 * */
@Composable
fun PremiumPlanComp(modifier: Modifier = Modifier, onPremiumButtonClick: () -> Unit = {}) {
    Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(Modifier.padding(16.dp)) {
            Column(Modifier.weight(.8f)) {
                Text(
                        stringResource(id = R.string.premium_heading),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                )
                Text(stringResource(id = R.string.premium_text))

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                        onClick = onPremiumButtonClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                ) {
                    Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Premium Plan",
                            tint = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                            text = stringResource(id = R.string.upgrade_to_premium),
                            color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Box(modifier = Modifier.weight(.2f))
        }
    }
}