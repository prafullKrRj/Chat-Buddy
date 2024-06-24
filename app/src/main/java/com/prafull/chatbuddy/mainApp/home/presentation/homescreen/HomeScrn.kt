package com.prafull.chatbuddy.mainApp.home.presentation.homescreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.prafull.chatbuddy.MainActivity
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.mainApp.common.components.PremiumPlanComp
import com.prafull.chatbuddy.mainApp.common.components.SelectModelDialogBox
import com.prafull.chatbuddy.mainApp.common.model.Model
import com.prafull.chatbuddy.mainApp.home.models.NormalHistoryMsg
import com.prafull.chatbuddy.mainApp.home.presentation.components.AdWindow
import com.prafull.chatbuddy.mainApp.home.presentation.components.NewHomeTopAppBar
import com.prafull.chatbuddy.mainApp.home.presentation.components.PromptField
import com.prafull.chatbuddy.utils.ads.BannerAd
import com.prafull.chatbuddy.utils.ads.rewardedAds

@Composable
fun NewHomeScreen(
    viewModel: HomeViewModel, navController: NavController
) {
    val modelsState by viewModel.modelDialogState.collectAsState()
    val selectedModel = remember<(Model) -> Unit> {
        { model ->
            viewModel.modelButtonClicked = false
            viewModel.currModel = model
        }
    }

    if (viewModel.modelButtonClicked) {
        SelectModelDialogBox(modelsState = modelsState,
                onModelSelect = selectedModel,
                onDismissRequest = {
                    viewModel.modelButtonClicked = false
                })
    }
    val context = LocalContext.current

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .imePadding(), topBar = {
        NewHomeTopAppBar(homeViewModel = viewModel)
    }, bottomBar = {
        PromptField(onSend = { message, images, participant ->
            viewModel.updateNewPrompt(
                    NormalHistoryMsg(
                            text = message,
                            imageBitmaps = images,
                            participant = participant,
                    )
            )
            navController.navigate(Routes.HomeChatScreen)
        })
    }) { paddingValues ->
        LazyColumn(
                modifier = Modifier,
                contentPadding = paddingValues,
                userScrollEnabled = true,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "ad") {
                BannerAd()
                AdWindow(viewModel) {
                    viewModel.adButtonEnabled = false
                    rewardedAds(context as MainActivity, failed = {
                        viewModel.adButtonEnabled = true
                    }) {
                        viewModel.adWatched()
                        viewModel.adButtonEnabled = true
                    }
                }
            }
            item("premium") {
                PremiumPlanComp {
                    navController.navigate(Routes.PaymentsScreen)
                }
            }
        }
    }
}






