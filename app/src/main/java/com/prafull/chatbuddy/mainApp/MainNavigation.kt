package com.prafull.chatbuddy.mainApp

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.prafull.chatbuddy.AppDes
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.goBackStack
import com.prafull.chatbuddy.mainApp.historyscreen.ui.HistoryScreen
import com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen.HomeChatScreen
import com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen.HomeChatVM
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.NewHomeScreen
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.ModelsScreen
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.chat.ModelsChatNewVM
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.chat.ModelsNewChatScreen
import com.prafull.chatbuddy.mainApp.payments.PaymentsScreen
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptChatScreen
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptChatVM
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptScreen
import com.prafull.chatbuddy.mainApp.settings.SettingsScreen
import com.prafull.chatbuddy.navigateAndPopBackStack
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun MainNavigation(appNavController: NavController) {

    val newHomeViewModel: HomeViewModel = getViewModel()

    val currentDestination = rememberSaveable {
        mutableIntStateOf(R.string.home)
    }
    val showBottom = rememberSaveable {
        mutableStateOf(true)
    }
    val navcontroller = rememberNavController()
    val destinations = listOf(
            AppDes.Home,
            AppDes.PromptScreen,
            AppDes.HistoryScreen,
            AppDes.ModelsScreen,
            AppDes.SettingsScreen
    )

    Scaffold(bottomBar = {
        if (showBottom.value) NavigationBar {
            destinations.forEach { destination ->
                NavigationBarItem(selected = destination.label == currentDestination.intValue,
                        onClick = {
                            if (currentDestination.intValue != destination.label) {
                                currentDestination.intValue = destination.label
                                navcontroller.navigateAndPopBackStack(destination.route)
                            }
                        },
                        icon = {
                            Icon(
                                    painter = painterResource(id = destination.icon),
                                    contentDescription = stringResource(id = destination.contentDescription)
                            )
                        },
                        label = {
                            Text(text = stringResource(id = destination.label))
                        })
            }
        }
    }) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            NavHost(navController = navcontroller, startDestination = Routes.NewHomeNav) {
                homeNavigation(navcontroller, newHomeViewModel) { showBottom.value = it }
                promptLibraryScreen(navcontroller, newHomeViewModel) { showBottom.value = it }
                modelsNavigationScreen(navcontroller, newHomeViewModel) { showBottom.value = it }
                composable<Routes.SettingsScreen> {
                    SettingsScreen(navController = navcontroller)
                }
                composable<Routes.History> {
                    HistoryScreen(navController = navcontroller)
                }
            }
        }
    }
}

fun NavGraphBuilder.modelsNavigationScreen(
    navController: NavHostController, homeVM: HomeViewModel, showNavBar: (Boolean) -> Unit = {}
) {
    navigation<Routes.ModelsNav>(startDestination = Routes.ModelsScreen) {
        composable<Routes.ModelsScreen> {
            showNavBar(true)
            ModelsScreen(
                    navController = navController, homeViewModel = homeVM
            )
        }
        composable<Routes.ModelChatScreen> { backStackEntry ->
            showNavBar(false)
            val model: Routes.ModelChatScreen = backStackEntry.toRoute()
            Log.d("Models Navigation", model.toString())
            val viewModel: ModelsChatNewVM = koinViewModel { parametersOf(model.toModel(), model.id) }
            ModelsNewChatScreen(viewModel) {
                showNavBar(true)
                navController.goBackStack()
            }
        }
    }
}


fun NavGraphBuilder.promptLibraryScreen(
    navController: NavHostController, newHomeVM: HomeViewModel, showNavBar: (Boolean) -> Unit = {}
) {
    navigation<Routes.PromptLibraryNav>(startDestination = Routes.PromptLibraryScreen) {
        composable<Routes.PromptLibraryScreen> {
            showNavBar(true)
            PromptScreen(modifier = Modifier, homeVM = newHomeVM) {
                navController.navigate(it.toPromptChatScreen())
            }
        }
        composable<Routes.PromptLibraryChat> { backStackEntry ->
            showNavBar(false)
            val item: Routes.PromptLibraryChat = backStackEntry.toRoute()
            val chatVM = koinViewModel<PromptChatVM> { parametersOf(item.toPromptLibraryItem()) }
            PromptChatScreen(promptChatVM = chatVM, newHomeVM) {
                showNavBar(true)
                navController.goBackStack()
            }
        }
    }
}

fun NavGraphBuilder.homeNavigation(
    navController: NavController, homeVm: HomeViewModel, showNavBar: (Boolean) -> Unit = {}
) {
    navigation<Routes.NewHomeNav>(startDestination = Routes.Home) {
        composable<Routes.Home> {
            showNavBar(true)
            NewHomeScreen(homeVm, navController)
        }
        composable<Routes.HomeChatScreen> { backStackEntry ->
            showNavBar(false)
            val chatVM: HomeChatVM =
                koinViewModel { parametersOf(homeVm.currPrompt, homeVm.currModel) }
            HomeChatScreen(chatVM, homeVm) {
                showNavBar(true)
                navController.goBackStack()
            }
        }
        composable<Routes.PaymentsScreen> {
            showNavBar(false)
            PaymentsScreen {
                showNavBar(true)
                navController.goBackStack()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsAndPromptTopAppBar(
    title: String
) {
    CenterAlignedTopAppBar(title = {
        Text(text = title)
    })
}
