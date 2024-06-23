package com.prafull.chatbuddy.mainApp

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.RoutesStrings
import com.prafull.chatbuddy.mainApp.common.components.DrawerContent
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.HomeScreen
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.chat.ModelsChatNewVM
import com.prafull.chatbuddy.mainApp.modelsScreen.chat.ModelsNewChatScreen
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.ModelsScreen
import com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen.HomeChatScreen
import com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen.HomeChatVM
import com.prafull.chatbuddy.mainApp.newHome.presentation.homescreen.NewHomeScreen
import com.prafull.chatbuddy.mainApp.newHome.presentation.homescreen.NewHomeViewModel
import com.prafull.chatbuddy.mainApp.payments.PaymentsScreen
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptChatScreen
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptChatVM
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptScreen
import com.prafull.chatbuddy.navigateAndPopBackStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun MainNavigation(appNavController: NavController) {
    val homeViewModel: HomeViewModel = getViewModel()
    val chatViewModel: ChatViewModel = getViewModel()

    val newHomeViewModel: NewHomeViewModel = getViewModel()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val previousChats by homeViewModel.previousChats.collectAsState()
    val mainNavController = rememberNavController()

    var currDestination by rememberSaveable {
        mutableStateOf(RoutesStrings.Home.name)
    }
    val focusManager = LocalFocusManager.current
    val mAuth = FirebaseAuth.getInstance()

    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            homeViewModel.getPreviousChats()
            focusManager.clearFocus()
        }
    }

    ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                if (currDestination == RoutesStrings.ModelsChatScreen.name || currDestination == RoutesStrings.PaymentsScreen.name) {
                    return@ModalNavigationDrawer
                }
                DrawerContent(
                        currDestination = currDestination,
                        mAuth = mAuth,
                        previousChats = previousChats,
                        homeViewModel = homeViewModel,
                        navController = mainNavController,
                        chatViewModel = chatViewModel,
                        onSettingsClicked = {
                            appNavController.navigate(Routes.SettingsScreen)
                        },
                        closeDrawer = { scope.launch { drawerState.close() } },
                        scope = scope,
                        currChatUUID = ""
                ) { chatHistory ->
                    scope.launch {
                        mainNavController.navigateAndPopBackStack(Routes.Home)
                        //chatViewModel.chatFromHistory(chatHistory)
                        delay(500L)
                        drawerState.close()
                    }
                }
            }
    ) {
        NavHost(
                navController = mainNavController,
                startDestination = Routes.NewHomeNavigation
        ) {
            composable<Routes.Home> {
                currDestination = RoutesStrings.Home.name
                HomeScreen(
                        mainNavController,
                        chatViewModel,
                        homeViewModel,
                        PromptLibraryItem(),
                        drawerState
                )
            }
            composable<Routes.HomeWithArgs> { backStackEntry ->
                val homeWithArgs: Routes.HomeWithArgs = backStackEntry.toRoute()
                currDestination = RoutesStrings.HomeWithArgs.name
                HomeScreen(
                        mainNavController,
                        chatViewModel,
                        homeViewModel,
                        homeWithArgs.toPromptLibraryItem(),
                        drawerState
                )
            }
            composable<Routes.ModelsScreen> {
                currDestination = RoutesStrings.ModelsScreen.name
                ModelsScreen(mainNavController, drawerState, newHomeViewModel)
            }
            composable<Routes.PaymentsScreen> {
                currDestination = RoutesStrings.PaymentsScreen.name
                PaymentsScreen(mainNavController)
            }
            modelsNavigationScreen(
                    mainNavController,
                    drawerState,
                    newHomeViewModel
            ) {
                currDestination = it
            }
            promptLibraryScreen(
                    drawerState,
                    newHomeViewModel,
                    mainNavController
            ) { currDestination = it }
            newHomeNavigation(drawerState, mainNavController, newHomeViewModel) {
                currDestination = it
            }

        }
    }
}

fun NavGraphBuilder.modelsNavigationScreen(
    navController: NavController, drawerState: DrawerState, homeVM: NewHomeViewModel,
    currDestination: (String) -> Unit
) {
    navigation<Routes.ModelsNav>(startDestination = Routes.ModelsScreen) {

        composable<Routes.ModelsScreen> {
            currDestination(RoutesStrings.ModelsScreen.name)
            ModelsScreen(
                    navController = navController,
                    drawerState = drawerState,
                    homeViewModel = homeVM
            )
        }
        composable<Routes.ModelChatScreen> { backStackEntry ->
            currDestination(RoutesStrings.ModelsChatScreen.name)
            val model: Routes.ModelChatScreen = backStackEntry.toRoute()
            val viewModel: ModelsChatNewVM = koinViewModel { parametersOf(model.toModel()) }
            ModelsNewChatScreen(viewModel, navController)
        }
    }
}


fun NavGraphBuilder.promptLibraryScreen(
    drawerState: DrawerState,
    newHomeVM: NewHomeViewModel,
    navController: NavController,
    currDestination: (String) -> Unit
) {
    navigation<Routes.PromptLibraryNav>(startDestination = Routes.PromptLibraryScreen) {
        currDestination(RoutesStrings.PromptScreen.name)
        composable<Routes.PromptLibraryScreen> {
            PromptScreen(modifier = Modifier, drawerState = drawerState, homeVM = newHomeVM) {
                navController.navigate(it.toPromptChatScreen())
            }
        }
        composable<Routes.PromptChatScreen> { backStackEntry ->
            val item: Routes.PromptChatScreen = backStackEntry.toRoute()
            val chatVM = koinViewModel<PromptChatVM> { parametersOf(item.toPromptLibraryItem()) }
            PromptChatScreen(promptChatVM = chatVM, newHomeVM, navController)
        }
    }
}

fun NavGraphBuilder.newHomeNavigation(
    drawerState: DrawerState,
    navController: NavController,
    homeVm: NewHomeViewModel,
    currDestination: (String) -> Unit
) {
    navigation<Routes.NewHomeNavigation>(startDestination = Routes.NewHome) {
        currDestination(RoutesStrings.NewHome.name)
        composable<Routes.NewHome> {
            NewHomeScreen(drawerState, homeVm, navController)
        }
        composable<Routes.HomeChatScreen> { backStackEntry ->
            Log.d("HomeChatScreen", homeVm.currPrompt.toString())
            val chatVM: HomeChatVM =
                koinViewModel { parametersOf(homeVm.currPrompt, homeVm.currModel) }
            HomeChatScreen(chatVM, homeVm, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsAndPromptTopAppBar(
    title: String,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navigate: () -> Unit
) {
    CenterAlignedTopAppBar(title = {
        Text(text = title)
    }, navigationIcon = {
        IconButton(onClick = {
            scope.launch {
                drawerState.open()
                navigate()
            }
        }) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu Navigate")
        }
    })
}
