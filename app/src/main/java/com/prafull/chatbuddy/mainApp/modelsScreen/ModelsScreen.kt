package com.prafull.chatbuddy.mainApp.modelsScreen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.ads.ModelScreenBannerAd
import com.prafull.chatbuddy.model.Model
import com.prafull.chatbuddy.utils.Const
import org.koin.androidx.compose.koinViewModel

@Composable
fun ModelsScreen(paddingValues: PaddingValues) {
    val modelViewModel: ModelViewModel = koinViewModel()
    val uiState by modelViewModel.state.collectAsState()

    Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
    ) {
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
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                item(key = Const.GPT) {
                    ModelGroupComposable(
                            groupName = Const.GPT,
                            models = modelViewModel.gptModels,
                            image = R.drawable.gpt
                    )
                }
                item(key = Const.CLAUDE) {
                    ModelGroupComposable(
                            groupName = Const.CLAUDE,
                            models = modelViewModel.claudeModels,
                            image = R.drawable.claude
                    )
                }
                item(key = Const.GEMINI) {
                    ModelGroupComposable(
                            groupName = Const.GEMINI,
                            models = modelViewModel.geminiModels,
                            image = R.drawable.gemini
                    )
                }
            }
        }
    }
}

@Composable
fun ModelGroupComposable(groupName: String, models: List<Model>, @DrawableRes image: Int) {
    if (models.isNotEmpty()) {
        Text(text = groupName, fontWeight = FontWeight.SemiBold)
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(models, key = {
                it.actualName
            }) { model ->
                Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .wrapContentHeight()
                            .width(200.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .clickable {

                        }) {
                        Row(verticalAlignment = CenterVertically) {
                            Image(
                                    painter = painterResource(id = image),
                                    contentDescription = "Image of $groupName model",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .size(50.dp)
                            )
                            Column {
                                Text(text = model.generalName)
                                Text(text = model.modelGroup)
                            }
                        }
                    }

                }
            }
        }
    }
}