package com.prafull.chatbuddy.mainApp

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import com.prafull.chatbuddy.Routes
import com.prafull.chatbuddy.RoutesStrings
import com.prafull.chatbuddy.mainApp.home.ui.components.HomeTopAppBar
import com.prafull.chatbuddy.mainApp.home.ui.components.PromptField
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.ChatViewModel
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.HomeScreen
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelsScreen
import com.prafull.chatbuddy.mainApp.modelsScreen.chat.ModelChatScreen
import com.prafull.chatbuddy.mainApp.modelsScreen.chat.ModelsChatVM
import com.prafull.chatbuddy.mainApp.payments.PaymentsScreen
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.mainApp.promptlibrary.ui.PromptScreen
import com.prafull.chatbuddy.mainApp.ui.DrawerContent
import com.prafull.chatbuddy.navigateAndPopBackStack
import com.prafull.chatbuddy.navigateHomeWithArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun MainNavigation(appNavController: NavController) {
    val chatViewModel: ChatViewModel = getViewModel()
    val homeViewModel: HomeViewModel = getViewModel()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val previousChats by homeViewModel.previousChats.collectAsState()
    val mainNavController = rememberNavController()

    var currDestination by rememberSaveable {
        mutableStateOf(RoutesStrings.Home.name)
    }
    val focusManager = LocalFocusManager.current
    val mAuth = FirebaseAuth.getInstance()
    val focusRequester = remember {
        FocusRequester()
    }
    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            homeViewModel.getPreviousChats()
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(currDestination) {
        focusManager.clearFocus()
    }

    ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                if (currDestination == RoutesStrings.ChatScreen.name || currDestination == RoutesStrings.PaymentsScreen.name) {
                    return@ModalNavigationDrawer
                }
                DrawerContent(
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
                        currChatUUID = chatViewModel.currChatUUID
                ) { chatHistory ->
                    scope.launch {
                        mainNavController.navigateAndPopBackStack(Routes.Home)
                        chatViewModel.chatFromHistory(chatHistory)
                        delay(500L)
                        drawerState.close()
                    }
                }
            }
    ) {
        Scaffold(
                modifier = Modifier.imePadding(),
                topBar = {
                    when (currDestination) {
                        RoutesStrings.Home.name, RoutesStrings.HomeWithArgs.name -> {
                            HomeTopAppBar(
                                    homeViewModel = homeViewModel,
                                    chatViewModel = chatViewModel,
                                    navController = mainNavController
                            ) {
                                scope.launch {
                                    drawerState.apply {
                                        drawerState.open()
                                    }
                                }
                            }
                        }

                        RoutesStrings.ModelsScreen.name -> {
                            ModelsAndPromptTopAppBar(title = "Models", drawerState, scope) {
                                homeViewModel.getPreviousChats()
                            }
                        }

                        RoutesStrings.PromptScreen.name -> {
                            ModelsAndPromptTopAppBar(title = "Prompt", drawerState, scope) {
                                homeViewModel.getPreviousChats()
                            }
                        }
                    }
                },
                bottomBar = {
                    if (currDestination == RoutesStrings.Home.name || currDestination == RoutesStrings.HomeWithArgs.name) {
                        PromptField(
                                Modifier.focusRequester(focusRequester),
                                viewModel = chatViewModel
                        )
                    }
                }
        ) { paddingValues ->
            NavHost(
                    navController = mainNavController,
                    startDestination = Routes.Home
            ) {
                composable<Routes.Home> {
                    currDestination = RoutesStrings.Home.name
                    HomeScreen(
                            paddingValues,
                            mainNavController,
                            chatViewModel,
                            homeViewModel,
                            PromptLibraryItem(),
                            focusRequester
                    )
                }
                composable<Routes.HomeWithArgs> { backStackEntry ->
                    val homeWithArgs: Routes.HomeWithArgs = backStackEntry.toRoute()
                    currDestination = RoutesStrings.HomeWithArgs.name
                    HomeScreen(
                            paddingValues,
                            mainNavController,
                            chatViewModel,
                            homeViewModel,
                            homeWithArgs.toPromptLibraryItem(),
                            focusRequester
                    )
                }
                composable<Routes.ModelsScreen> {
                    currDestination = RoutesStrings.ModelsScreen.name
                    ModelsScreen(paddingValues, mainNavController)
                }
                composable<Routes.PromptScreen> {
                    currDestination = RoutesStrings.PromptScreen.name
                    PromptScreen(
                            Modifier.padding(),
                            paddingValues
                    ) { promptLibraryItem ->
                        chatViewModel.loadFromPromptLibrary(promptLibraryItem)
                        mainNavController.navigateHomeWithArgs(promptLibraryItem)
                    }
                }
                composable<Routes.PaymentsScreen> {
                    currDestination = RoutesStrings.PaymentsScreen.name
                    PaymentsScreen(mainNavController)
                }
                composable<Routes.ChatScreen> { backStackEntry ->
                    currDestination = RoutesStrings.ChatScreen.name
                    val model: Routes.ChatScreen = backStackEntry.toRoute()
                    val viewModel: ModelsChatVM = koinViewModel { parametersOf(model.toModel()) }
                    ModelChatScreen(viewModel, mainNavController)
                }
            }
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
