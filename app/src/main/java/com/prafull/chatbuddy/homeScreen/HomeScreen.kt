package com.prafull.chatbuddy.homeScreen

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.utils.UriSaver
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@Composable
fun HomeScreen() {
    val viewModel:ChatViewModel = getViewModel()
    val coroutineScope = rememberCoroutineScope()
    val imageRequestBuilder = ImageRequest.Builder(LocalContext.current)
    val imageLoader = ImageLoader.Builder(LocalContext.current).build()
    val listState = rememberLazyListState()
    val state = viewModel.uiState.collectAsState()

    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }

    LaunchedEffect(state){
        if (state.value.messages.isNotEmpty()
            ){
            listState.scrollToItem(state.value.messages.lastIndex)
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(
                top = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateTopPadding(),
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
        )) {
        LazyColumn(
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true,
                state = listState
        ) {
            items(state.value.messages) { message ->
                LazyRow(
                        modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    items(message.imageUri) { imageUri ->
                        AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .requiredWidth(72.dp)
                        )
                    }
                }
                Text(
                        text = message.text,
                        modifier = Modifier.padding(8.dp)
                )
            }
        }
        Column(Modifier.align(Alignment.BottomCenter)) {
            LazyRow(
                    modifier = Modifier.padding(all = 8.dp)
            ) {
                items(imageUris) { imageUri ->
                    AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .requiredSize(72.dp)
                    )
                }
            }
            PromptField(imageUris = {
                imageUris.add(it)
            }) { prompt ->
                coroutineScope.launch {
                    val bitmaps = imageUris.mapNotNull {
                        val imageRequest = imageRequestBuilder
                            .data(it)
                            .size(size = 768)
                            .precision(Precision.EXACT)
                            .build()
                        try {
                            val result = imageLoader.execute(imageRequest)
                            if (result is SuccessResult) {
                                return@mapNotNull (result.drawable as BitmapDrawable).bitmap
                            } else {
                                return@mapNotNull null
                            }
                        } catch (e: Exception) {
                            return@mapNotNull null
                        }
                    }
                    viewModel.sendMessage(prompt, bitmaps)
                    imageUris.clear()
                }
            }
        }
    }
}

@Composable
fun PromptField(imageUris: (Uri) -> Unit, send: (String) -> Unit) {
    var prompt by rememberSaveable {
        mutableStateOf("")
    }
    val pickMedia = rememberLauncherForActivityResult(      // Create a launcher for picking media
            ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let {
            imageUris(it)
        }
    }
    OutlinedTextField(
            value = prompt,
            onValueChange = {
                prompt = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            leadingIcon = {
                IconButton(onClick = {
                    pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)   // Launch the media picker
                    )
                }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_image_24), contentDescription = "Add Image")
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    send(prompt)
                    prompt = ""
                }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            },
            shape = RoundedCornerShape(35),
            colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray
            )
    )

}