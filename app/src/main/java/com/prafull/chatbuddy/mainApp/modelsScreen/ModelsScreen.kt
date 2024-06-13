package com.prafull.chatbuddy.mainApp.modelsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.ads.ModelScreenBannerAd
import org.koin.androidx.compose.koinViewModel

@Composable
fun ModelsScreen(paddingValues: PaddingValues) {
    val modelViewModel: ModelViewModel = koinViewModel()
    val uiState by modelViewModel.state.collectAsState()
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        ModelScreenBannerAd()
        if (uiState.loading) {
            CircularProgressIndicator()
        } else if (uiState.error) {
            Button(onClick = {
                modelViewModel.getModels()
            }) {
                Text(text = "Retry")
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = paddingValues) {
                items(uiState.models) { model ->
                    ElevatedCard(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
                        val imageId = when (model.modelGroup) {
                            "Claude" -> {
                                R.drawable.claude
                            }

                            "GPT" -> {
                                R.drawable.gpt
                            }

                            else -> {
                                R.drawable.gemini
                            }
                        }
                        Image(
                                painter = painterResource(id = imageId),
                                contentDescription = "Model Image"
                        )
                        Text(text = model.generalName)
                    }
                }
            }
        }
    }
}