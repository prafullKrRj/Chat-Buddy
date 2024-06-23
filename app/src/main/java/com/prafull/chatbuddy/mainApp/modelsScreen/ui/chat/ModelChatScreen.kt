package com.prafull.chatbuddy.mainApp.modelsScreen.ui.chat

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.model.isGptModel
import com.prafull.chatbuddy.model.Model

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelChatScreen(viewModel: ModelsChatVM, navController: NavController) {
    /*   val state by viewModel.uiState.collectAsState()
       val mA = FirebaseAuth.getInstance()
       val clipboardManager = LocalClipboardManager.current
       val context = LocalContext.current
       val showBackDialog = remember {
           mutableStateOf(false)
       }
       val navigate = remember {
           {
               navController.goBackStack()
           }
       }
       BackHandler {
           showBackDialog.value = true
       }
       val currentModel by viewModel.currentModel.collectAsState()
       val listState = rememberLazyListState()
       Text(text = currentModel.generalName)
       LaunchedEffect(state.messages.size) {
           if (state.messages.isNotEmpty()) {
               listState.animateScrollToItem(state.messages.size)
           }
       }
       Scaffold(
               modifier = Modifier.imePadding(),
               topBar = {
                   CenterAlignedTopAppBar(title = {
                       Text(text = currentModel.generalName)
                   }, navigationIcon = {
                       IconButton(onClick = {
                           showBackDialog.value = true
                       }) {
                           Icon(
                                   imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                   contentDescription = "Back"
                           )
                       }
                   })
               },
               bottomBar = {
                   PromptField(Modifier, viewModel = viewModel)
               }
       ) { paddingValues ->
           if (state.messages.isEmpty()) {
               InitialChatUI(modifier = Modifier.padding(paddingValues), model = currentModel)
           }
           if (viewModel.historyLoading) {
               Column(
                       Modifier.fillMaxSize(),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally
               ) {
                   CircularProgressIndicator()
               }
           }
           if (viewModel.historyError) {
               Column(
                       Modifier.fillMaxSize(),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally
               ) {
                   Button(onClick = {
                       viewModel.updateChat()
                   }) {
                       Text(text = "Retry")
                   }
               }
           }
           LazyColumn(
                   state = listState,
                   contentPadding = paddingValues,
                   modifier = Modifier.fillMaxSize()
           ) {
               itemsIndexed(state.messages) { index, chatMessage ->
                   when (index) {
                       state.messages.lastIndex -> {
                           MessageBubble(
                                   message = chatMessage,
                                   mA = mA,
                                   clipboardManager = clipboardManager,
                                   context = context,
                                   isSecondLast = false,
                                   isLast = true,
                                   viewModel = viewModel
                           )
                       }

                       state.messages.lastIndex - 1 -> {
                           MessageBubble(
                                   message = chatMessage,
                                   mA = mA,
                                   clipboardManager = clipboardManager,
                                   context = context,
                                   isSecondLast = true,
                                   isLast = false,
                                   viewModel = viewModel
                           )
                       }

                       else -> {
                           MessageBubble(
                                   message = chatMessage,
                                   mA = mA,
                                   clipboardManager = clipboardManager,
                                   context = context,
                                   isSecondLast = false,
                                   isLast = false,
                                   viewModel = viewModel
                           )
                       }
                   }
               }
           }
       }
       if (showBackDialog.value) {
           AlertDialog(
                   onDismissRequest = {
                       showBackDialog.value = false
                   },
                   title = { Text(text = "Exit") },
                   text = { Text(text = "Do you want to exit from the conversation!") },
                   confirmButton = {
                       TextButton(onClick = {
                           showBackDialog.value = false
                           navigate()
                       }) { Text("Confirm") }
                   },
                   dismissButton = {
                       TextButton(onClick = { showBackDialog.value = false }) { Text("Dismiss") }
                   }
           )
       }*/
}

@Composable
fun InitialChatUI(modifier: Modifier, model: Model) {
    val context = LocalContext.current
    Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (model.modelGroup.isGeminiModel()) OfflineLogo(R.drawable.gemini)
        else if (model.modelGroup.isGptModel()) OfflineLogo(R.drawable.gpt)
        else if (model.modelGroup.isClaudeModel()) OfflineLogo(R.drawable.claude)
        else OnlineLogo(context, model.image)
        Text(text = model.generalName, fontWeight = SemiBold, fontSize = 20.sp)
    }
}

@Composable
private fun OfflineLogo(@DrawableRes id: Int) {
    Image(
            painter = painterResource(id = id),
            contentDescription = "image",
            modifier = Modifier.width(150.dp)
    )
}

@Composable
private fun OnlineLogo(context: Context, data: String) {
    AsyncImage(
            model = ImageRequest.Builder(context).data(data).build(),
            contentDescription = "image",
            modifier = Modifier.width(150.dp)
    )
}