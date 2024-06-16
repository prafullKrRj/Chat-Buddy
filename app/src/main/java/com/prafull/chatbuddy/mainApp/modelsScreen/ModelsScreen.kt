package com.prafull.chatbuddy.mainApp.modelsScreen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.prafull.chatbuddy.mainApp.ads.ModelScreenBannerAd
import com.prafull.chatbuddy.model.Model
import org.koin.androidx.compose.koinViewModel

@Composable
fun ModelsScreen(paddingValues: PaddingValues, navController: NavController) {
    val modelViewModel: ModelViewModel = koinViewModel()
    val uiState by modelViewModel.state.collectAsState()
    val navigate = remember<(Model) -> Unit> {
        {
            navController.navigate(
                    it.toChatScreen()
            )
        }
    }
    val modelResponse by modelViewModel.modelResponse.collectAsState()
    val context = LocalContext.current
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
            LazyColumn {
                items(modelResponse.modelResponses, key = {
                    it.type
                }) { modelResponse ->
                    Column {
                        Text(
                                text = modelResponse.type.toUpperCase(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                        )
                        modelResponse.groups.forEach { group ->
                            ModelGroupComposable(
                                    context = context,
                                    groupName = group.name,
                                    models = group.models,
                                    onModelClicked = navigate
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModelGroupComposable(
    context: Context,
    groupName: String,
    models: List<Model>,
    onModelClicked: (Model) -> Unit
) {
    if (models.isNotEmpty()) {
        Text(
                text = groupName,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
        )
        LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(models, key = {
                it.actualName
            }) { model ->
                Card(
                        modifier = Modifier
                            .height(100.dp)
                            .width(200.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onModelClicked(model)
                        }) {
                        Row(verticalAlignment = CenterVertically) {
                            AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(model.image).build(),
                                    contentDescription = "Model Image",
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