package com.prafull.chatbuddy.mainApp.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.common.model.Model
import com.prafull.chatbuddy.mainApp.common.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.common.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.common.model.isGptModel
import com.prafull.chatbuddy.utils.Resource


@Composable
fun SelectModelDialogBox(
    modelsState: Resource<List<Model>>,
    onModelSelect: (Model) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedModel by remember { mutableStateOf<Model?>(null) }
    AlertDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(onClick = {
                    if (selectedModel != null) {
                        onModelSelect(selectedModel!!)
                    }
                    onDismissRequest()
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissRequest()
                }) {
                    Text(text = "Cancel")
                }
            },
            title = { Text(text = "Select Model") },
            text = {
                Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (modelsState) {
                        is Resource.Initial -> {
                            CircularProgressIndicator()
                        }

                        is Resource.Success -> {
                            LazyColumn(
                                    contentPadding = PaddingValues(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                            ) {
                                items(modelsState.data, key = { it.generalName }) { model ->
                                    SelectModelItem(
                                            onClick = {
                                                selectedModel = model
                                            },
                                            model = model,
                                            selectedModel = selectedModel
                                    )
                                }
                            }
                        }

                        is Resource.Error -> {
                            Button(onClick = { /*TODO*/ }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
            }
    )
}

@Composable
private fun SelectModelItem(onClick: () -> Unit, model: Model, selectedModel: Model?) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                    painter = painterResource(
                            id =
                            if (model.generalName.isGptModel()) {
                                R.drawable.gpt
                            } else if (model.generalName.isClaudeModel()) {
                                R.drawable.claude
                            } else if (model.generalName.isGeminiModel()) {
                                R.drawable.gemini
                            } else {
                                R.drawable.logo
                            }
                    ),
                    contentDescription = null,
                    modifier = Modifier.weight(.2f)
            )
            Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(.7f),
                    verticalArrangement = Arrangement.Center
            ) {
                Text(text = model.generalName)
                Text(text = model.currPricePerToken.toString())
                Text(text = model.taskType)
            }
            Checkbox(
                    modifier = Modifier.weight(.1f),
                    checked = selectedModel == model,
                    onCheckedChange = {
                        if (it) onClick()
                    })
        }
    }
}