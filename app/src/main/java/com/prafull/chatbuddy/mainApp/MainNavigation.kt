package com.prafull.chatbuddy.mainApp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.prafull.chatbuddy.navigateAndPopBackStack
import com.prafull.chatbuddy.settings.SettingsScreen
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
    val nc = rememberNavController()
    val destinations = listOf(
            AppDes.Home,
            AppDes.PromptScreen,
            AppDes.HistoryScreen,
            AppDes.ModelsScreen,
            AppDes.SettingsScreen
    )

    Scaffold(
            bottomBar = {
                if (showBottom.value)
                    NavigationBar {
                        destinations.forEach { destination ->
                            NavigationBarItem(
                                    selected = destination.label == currentDestination.intValue,
                                    onClick = {
                                        if (currentDestination.intValue != destination.label) {
                                            currentDestination.intValue = destination.label
                                            nc.navigateAndPopBackStack(destination.route)
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
                                    }
                            )
                        }
                    }
            }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            NavHost(navController = nc, startDestination = Routes.NewHomeNav) {
                homeNavigation(nc, newHomeViewModel) { showBottom.value = it }
                promptLibraryScreen(nc, newHomeViewModel) { showBottom.value = it }
                modelsNavigationScreen(nc, newHomeViewModel) { showBottom.value = it }
                composable<Routes.SettingsScreen> {
                    SettingsScreen(navController = nc)
                }
                composable<Routes.History> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(text = "History")
                    }
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
                    navController = navController,
                    homeViewModel = homeVM
            )
        }
        composable<Routes.ModelChatScreen> { backStackEntry ->
            showNavBar(false)
            val model: Routes.ModelChatScreen = backStackEntry.toRoute()
            val viewModel: ModelsChatNewVM = koinViewModel { parametersOf(model.toModel()) }
            ModelsNewChatScreen(viewModel) {
                showNavBar(true)
                navController.goBackStack()
            }
        }
    }
}


fun NavGraphBuilder.promptLibraryScreen(
    navController: NavHostController,
    newHomeVM: HomeViewModel, showNavBar: (Boolean) -> Unit = {}
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
    navController: NavController,
    homeVm: HomeViewModel, showNavBar: (Boolean) -> Unit = {}
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
